package com.ruoyi.smartcare.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizAlertRule;
import com.ruoyi.smartcare.domain.IotDevice;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.mapper.BizAlertRuleMapper;
import com.ruoyi.smartcare.mapper.IotDeviceMapper;
import com.ruoyi.smartcare.mapper.IotSensorLogMapper;
import com.ruoyi.smartcare.service.IAlertAnalysisService;
import com.ruoyi.smartcare.service.IBizAlertRecordService;

/**
 * 预警分析服务实现。
 */
@Service
public class AlertAnalysisServiceImpl implements IAlertAnalysisService
{
    private static final Logger log = LoggerFactory.getLogger(AlertAnalysisServiceImpl.class);

    private static final String RULE_ENABLED = "0";
    private static final String HANDLE_PENDING = "0";

    private static final String DEVICE_BED_MAT = "bed_mat";
    private static final String DEVICE_SMOKE = "smoke";

    private static final String METRIC_HEART_RATE = "heart_rate";
    private static final String METRIC_BREATH_RATE = "breath_rate";
    private static final String METRIC_LEAVE_BED = "leave_bed";
    private static final String METRIC_SMOKE_VALUE = "smoke_value";

    private static final String ALERT_TYPE_INACTIVITY = "inactivity";

    @Autowired
    private BizAlertRuleMapper alertRuleMapper;

    @Autowired
    private IBizAlertRecordService bizAlertRecordService;

    @Autowired
    private IotSensorLogMapper sensorLogMapper;

    @Autowired
    private IotDeviceMapper deviceMapper;

    @Override
    @Transactional
    public List<BizAlertRecord> analyzeSensorData(IotSensorLog sensorLog)
    {
        List<BizAlertRecord> alerts = new ArrayList<>();
        if (sensorLog == null || sensorLog.getDeviceId() == null || sensorLog.getMetricType() == null)
        {
            return alerts;
        }

        BizAlertRule query = new BizAlertRule();
        query.setDeviceType(resolveDeviceType(sensorLog));
        query.setMetricType(sensorLog.getMetricType());
        query.setStatus(RULE_ENABLED);

        List<BizAlertRule> rules = alertRuleMapper.selectBizAlertRuleList(query);
        if (rules.isEmpty())
        {
            query.setDeviceType(null);
            rules = alertRuleMapper.selectBizAlertRuleList(query);
        }

        for (BizAlertRule rule : rules)
        {
            if (!matchRule(sensorLog, rule))
            {
                continue;
            }

            BizAlertRecord alert = createAlertRecord(sensorLog, rule);
            bizAlertRecordService.insertBizAlertRecord(alert);
            alerts.add(alert);
            log.info("触发预警: elderlyName={}, ruleName={}, alertLevel={}",
                sensorLog.getElderlyName(), rule.getRuleName(), rule.getAlertLevel());
        }

        return alerts;
    }

    @Override
    public List<BizAlertRecord> analyzeSensorDataBatch(List<IotSensorLog> sensorLogs)
    {
        List<BizAlertRecord> alerts = new ArrayList<>();
        if (sensorLogs == null || sensorLogs.isEmpty())
        {
            return alerts;
        }

        for (IotSensorLog sensorLog : sensorLogs)
        {
            alerts.addAll(analyzeSensorData(sensorLog));
        }
        return alerts;
    }

    @Override
    @Transactional
    public void runScheduledAnalysis()
    {
        log.info("开始执行定时预警扫描");
        checkNightBedLeaveTimeout();
        checkInactivityAlerts();
        checkEnvironmentalAlerts();
        log.info("定时预警扫描完成");
    }

    private void checkNightBedLeaveTimeout()
    {
        int hour = new Date().getHours();
        if (hour < 22 && hour >= 6)
        {
            return;
        }

        IotDevice query = new IotDevice();
        query.setDeviceType(DEVICE_BED_MAT);
        List<IotDevice> beds = deviceMapper.selectIotDeviceList(query);

        for (IotDevice bed : beds)
        {
            if (bed.getElderlyId() == null || !checkInactivity(bed.getElderlyId(), 30))
            {
                continue;
            }

            BizAlertRecord alert = new BizAlertRecord();
            alert.setElderlyId(bed.getElderlyId());
            alert.setElderlyName(bed.getElderlyName());
            alert.setDeviceId(bed.getDeviceId());
            alert.setDeviceName(bed.getDeviceName());
            alert.setAlertLevel(1L);
            alert.setAlertType(METRIC_LEAVE_BED);
            alert.setAlertDesc("夜间长时间离床，持续超过30分钟");
            alert.setHandleStatus(HANDLE_PENDING);
            alert.setCreateTime(new Date());
            bizAlertRecordService.insertBizAlertRecord(alert);
        }
    }

    private void checkInactivityAlerts()
    {
        BizAlertRule query = new BizAlertRule();
        query.setAlertType(ALERT_TYPE_INACTIVITY);
        query.setStatus(RULE_ENABLED);
        List<BizAlertRule> rules = alertRuleMapper.selectBizAlertRuleList(query);

        for (BizAlertRule rule : rules)
        {
            if (!isNumeric(rule.getThresholdValue()) || rule.getDeviceType() == null)
            {
                continue;
            }

            int thresholdMinutes = new BigDecimal(rule.getThresholdValue()).intValue();
            IotDevice deviceQuery = new IotDevice();
            deviceQuery.setDeviceType(rule.getDeviceType());
            List<IotDevice> devices = deviceMapper.selectIotDeviceList(deviceQuery);

            for (IotDevice device : devices)
            {
                if (device.getElderlyId() == null || !checkInactivity(device.getElderlyId(), thresholdMinutes))
                {
                    continue;
                }

                BizAlertRecord alert = new BizAlertRecord();
                alert.setElderlyId(device.getElderlyId());
                alert.setElderlyName(device.getElderlyName());
                alert.setDeviceId(device.getDeviceId());
                alert.setDeviceName(device.getDeviceName());
                alert.setAlertLevel(rule.getAlertLevel());
                alert.setAlertType(rule.getAlertType());
                alert.setAlertDesc(rule.getRuleName() + "，持续超过" + thresholdMinutes + "分钟");
                alert.setHandleStatus(HANDLE_PENDING);
                alert.setCreateTime(new Date());
                bizAlertRecordService.insertBizAlertRecord(alert);
            }
        }
    }

    private void checkEnvironmentalAlerts()
    {
        IotSensorLog query = new IotSensorLog();
        query.setMetricType(METRIC_SMOKE_VALUE);
        List<IotSensorLog> logs = sensorLogMapper.selectIotSensorLogList(query);

        for (IotSensorLog sensorLog : logs)
        {
            if (!isSmokeAbnormal(sensorLog.getMetricValue()))
            {
                continue;
            }

            BizAlertRecord alert = new BizAlertRecord();
            alert.setElderlyId(sensorLog.getElderlyId());
            alert.setElderlyName(sensorLog.getElderlyName());
            alert.setDeviceId(sensorLog.getDeviceId());
            alert.setDeviceName(sensorLog.getDeviceName());
            alert.setAlertLevel(2L);
            alert.setAlertType(METRIC_SMOKE_VALUE);
            alert.setAlertDesc("检测到烟雾浓度异常");
            alert.setHandleStatus(HANDLE_PENDING);
            alert.setCreateTime(new Date());
            bizAlertRecordService.insertBizAlertRecord(alert);
        }
    }

    @Override
    public boolean checkInactivity(Long elderlyId, int minutes)
    {
        if (elderlyId == null)
        {
            return false;
        }

        IotSensorLog query = new IotSensorLog();
        query.setElderlyId(elderlyId);
        List<IotSensorLog> logs = sensorLogMapper.selectIotSensorLogList(query);
        if (logs == null || logs.isEmpty())
        {
            return true;
        }

        IotSensorLog latest = logs.stream()
            .filter(item -> item.getReportTime() != null)
            .max(Comparator.comparing(IotSensorLog::getReportTime))
            .orElse(null);
        if (latest == null || latest.getReportTime() == null)
        {
            return true;
        }

        long diffMinutes = (System.currentTimeMillis() - latest.getReportTime().getTime()) / (1000 * 60);
        return diffMinutes > minutes;
    }

    private boolean matchRule(IotSensorLog sensorLog, BizAlertRule rule)
    {
        if (rule == null || !RULE_ENABLED.equals(rule.getStatus()))
        {
            return false;
        }

        String metricValue = sensorLog.getMetricValue();
        String thresholdValue = rule.getThresholdValue();
        String conditionOp = normalizeCondition(rule.getConditionOp());
        if (metricValue == null || thresholdValue == null || conditionOp == null)
        {
            return false;
        }

        try
        {
            if (isNumeric(metricValue) && isNumeric(thresholdValue))
            {
                BigDecimal value = new BigDecimal(metricValue);
                BigDecimal threshold = new BigDecimal(thresholdValue);
                switch (conditionOp)
                {
                    case ">":
                        return value.compareTo(threshold) > 0;
                    case ">=":
                        return value.compareTo(threshold) >= 0;
                    case "<":
                        return value.compareTo(threshold) < 0;
                    case "<=":
                        return value.compareTo(threshold) <= 0;
                    case "=":
                    case "==":
                        return value.compareTo(threshold) == 0;
                    case "!=":
                        return value.compareTo(threshold) != 0;
                    default:
                        return false;
                }
            }

            switch (conditionOp)
            {
                case "=":
                case "==":
                    return metricValue.equals(thresholdValue);
                case "!=":
                    return !metricValue.equals(thresholdValue);
                default:
                    return false;
            }
        }
        catch (NumberFormatException ex)
        {
            log.warn("预警规则比较失败: metricValue={}, thresholdValue={}", metricValue, thresholdValue);
            return false;
        }
    }

    private BizAlertRecord createAlertRecord(IotSensorLog sensorLog, BizAlertRule rule)
    {
        BizAlertRecord alert = new BizAlertRecord();
        alert.setElderlyId(sensorLog.getElderlyId());
        alert.setElderlyName(sensorLog.getElderlyName());
        alert.setDeviceId(sensorLog.getDeviceId());
        alert.setDeviceName(sensorLog.getDeviceName());
        alert.setAlertLevel(rule.getAlertLevel());
        alert.setAlertType(rule.getAlertType() != null ? rule.getAlertType() : sensorLog.getMetricType());
        alert.setAlertDesc(rule.getRuleName() + "，检测值：" + sensorLog.getMetricValue() + "，阈值：" + rule.getThresholdValue());
        alert.setHandleStatus(HANDLE_PENDING);
        alert.setCreateTime(new Date());
        return alert;
    }

    private String resolveDeviceType(IotSensorLog sensorLog)
    {
        IotDevice device = deviceMapper.selectIotDeviceByDeviceId(sensorLog.getDeviceId());
        if (device != null && device.getDeviceType() != null)
        {
            return device.getDeviceType();
        }

        String deviceName = sensorLog.getDeviceName();
        if (deviceName == null)
        {
            return null;
        }
        if (deviceName.contains("床") || deviceName.toLowerCase().contains("bed"))
        {
            return DEVICE_BED_MAT;
        }
        if (deviceName.contains("烟") || deviceName.toLowerCase().contains("smoke"))
        {
            return DEVICE_SMOKE;
        }
        return null;
    }

    private String normalizeCondition(String conditionOp)
    {
        if (conditionOp == null)
        {
            return null;
        }
        return conditionOp
            .replace("&gt;=", ">=")
            .replace("&lt;=", "<=")
            .replace("&gt;", ">")
            .replace("&lt;", "<");
    }

    private boolean isSmokeAbnormal(String metricValue)
    {
        if (metricValue == null)
        {
            return false;
        }
        if ("true".equalsIgnoreCase(metricValue) || "1".equals(metricValue))
        {
            return true;
        }
        if (!isNumeric(metricValue))
        {
            return false;
        }
        return new BigDecimal(metricValue).compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isNumeric(String value)
    {
        if (value == null || value.isEmpty())
        {
            return false;
        }
        try
        {
            new BigDecimal(value);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
