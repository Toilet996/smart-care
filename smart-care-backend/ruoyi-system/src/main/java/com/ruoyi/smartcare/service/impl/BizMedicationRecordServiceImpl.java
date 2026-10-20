package com.ruoyi.smartcare.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizMedicationPlan;
import com.ruoyi.smartcare.domain.BizMedicationRecord;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.mapper.BizAlertRecordMapper;
import com.ruoyi.smartcare.mapper.BizMedicationPlanMapper;
import com.ruoyi.smartcare.mapper.BizMedicationRecordMapper;
import com.ruoyi.smartcare.service.IBizAlertRecordService;
import com.ruoyi.smartcare.service.IBizMedicationRecordService;

@Service
public class BizMedicationRecordServiceImpl implements IBizMedicationRecordService
{
    private static final String ALERT_TYPE_MEDICATION_MISSED = "medication_missed";

    @Autowired
    private BizMedicationRecordMapper bizMedicationRecordMapper;

    @Autowired
    private BizMedicationPlanMapper bizMedicationPlanMapper;

    @Autowired
    private BizAlertRecordMapper bizAlertRecordMapper;

    @Autowired
    private IBizAlertRecordService bizAlertRecordService;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    @Override
    public BizMedicationRecord selectBizMedicationRecordByRecordId(Long recordId)
    {
        BizMedicationRecord record = bizMedicationRecordMapper.selectBizMedicationRecordByRecordId(recordId);
        if (record != null)
        {
            familyAccessService.assertCanAccessElderly(record.getElderlyId());
        }
        return record;
    }

    @Override
    public List<BizMedicationRecord> selectBizMedicationRecordList(BizMedicationRecord bizMedicationRecord)
    {
        familyAccessService.applyFamilyScope(bizMedicationRecord);
        return bizMedicationRecordMapper.selectBizMedicationRecordList(bizMedicationRecord);
    }

    @Override
    public int insertBizMedicationRecord(BizMedicationRecord bizMedicationRecord)
    {
        if (bizMedicationRecord.getCreateTime() == null)
        {
            bizMedicationRecord.setCreateTime(DateUtils.getNowDate());
        }
        return bizMedicationRecordMapper.insertBizMedicationRecord(bizMedicationRecord);
    }

    @Override
    public int updateBizMedicationRecord(BizMedicationRecord bizMedicationRecord)
    {
        return bizMedicationRecordMapper.updateBizMedicationRecord(bizMedicationRecord);
    }

    @Override
    public int deleteBizMedicationRecordByRecordIds(Long[] recordIds)
    {
        return bizMedicationRecordMapper.deleteBizMedicationRecordByRecordIds(recordIds);
    }

    @Override
    public int deleteBizMedicationRecordByRecordId(Long recordId)
    {
        return bizMedicationRecordMapper.deleteBizMedicationRecordByRecordId(recordId);
    }

    @Override
    public void syncBySensorLog(IotSensorLog sensorLog)
    {
        if (sensorLog == null || !"pill_status".equals(sensorLog.getMetricType()) || sensorLog.getElderlyId() == null)
        {
            return;
        }

        Date reportTime = sensorLog.getReportTime() == null ? new Date() : sensorLog.getReportTime();
        Date scheduledDate = startOfDay(reportTime);
        List<BizMedicationPlan> planList = bizMedicationPlanMapper.selectActivePlansByElderlyAndDate(sensorLog.getElderlyId(), scheduledDate);
        if (planList == null || planList.isEmpty())
        {
            return;
        }

        BizMedicationPlan targetPlan = chooseClosestPlan(planList, reportTime);
        if (targetPlan == null)
        {
            return;
        }

        BizMedicationRecord existing = bizMedicationRecordMapper.selectByPlanAndDate(targetPlan.getPlanId(), scheduledDate);
        if (existing == null)
        {
            BizMedicationRecord record = buildRecord(targetPlan, reportTime, sensorLog.getMetricValue(), scheduledDate);
            bizMedicationRecordMapper.insertBizMedicationRecord(record);
        }
        else
        {
            existing.setExecuteStatus(convertExecuteStatus(sensorLog.getMetricValue()));
            existing.setActualTime(reportTime);
            existing.setSourceType("sensor");
            existing.setMetricValue(sensorLog.getMetricValue());
            existing.setRemark(buildRecordRemark(sensorLog.getMetricValue()));
            bizMedicationRecordMapper.updateBizMedicationRecord(existing);
        }

        createMissedMedicationAlertIfNeeded(targetPlan, sensorLog, scheduledDate);
    }

    private BizMedicationPlan chooseClosestPlan(List<BizMedicationPlan> planList, Date reportTime)
    {
        LocalDateTime reportDateTime = LocalDateTime.ofInstant(reportTime.toInstant(), ZoneId.systemDefault());
        BizMedicationPlan target = null;
        long minDiff = Long.MAX_VALUE;
        for (BizMedicationPlan plan : planList)
        {
            LocalTime localPlanTime = parsePlanTime(plan.getPlanTime());
            long diff = Math.abs(Duration.between(localPlanTime, reportDateTime.toLocalTime()).toMinutes());
            if (diff < minDiff)
            {
                minDiff = diff;
                target = plan;
            }
        }
        return target;
    }

    private BizMedicationRecord buildRecord(BizMedicationPlan plan, Date reportTime, String metricValue, Date scheduledDate)
    {
        BizMedicationRecord record = new BizMedicationRecord();
        record.setPlanId(plan.getPlanId());
        record.setElderlyId(plan.getElderlyId());
        record.setElderlyName(plan.getElderlyName());
        record.setMedicationName(plan.getMedicationName());
        record.setPlanTime(plan.getPlanTime());
        record.setScheduledDate(scheduledDate);
        record.setScheduledTime(buildScheduledTime(scheduledDate, plan.getPlanTime()));
        record.setExecuteStatus(convertExecuteStatus(metricValue));
        record.setSourceType("sensor");
        record.setMetricValue(metricValue);
        record.setActualTime(reportTime);
        record.setCreateTime(DateUtils.getNowDate());
        record.setRemark(buildRecordRemark(metricValue));
        return record;
    }

    private Date buildScheduledTime(Date scheduledDate, String planTime)
    {
        LocalDate localDate = scheduledDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = parsePlanTime(planTime);
        return Date.from(LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date startOfDay(Date value)
    {
        LocalDate localDate = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalTime parsePlanTime(String planTime)
    {
        if (StringUtils.isBlank(planTime))
        {
            return LocalTime.of(8, 0);
        }
        return LocalTime.parse(planTime + (planTime.length() == 5 ? ":00" : ""));
    }

    private String convertExecuteStatus(String metricValue)
    {
        if ("taken".equalsIgnoreCase(metricValue))
        {
            return "1";
        }
        if ("missed".equalsIgnoreCase(metricValue))
        {
            return "2";
        }
        return "0";
    }

    private String buildRecordRemark(String metricValue)
    {
        if ("taken".equalsIgnoreCase(metricValue))
        {
            return "来自智能药盒上报：已服药";
        }
        if ("missed".equalsIgnoreCase(metricValue))
        {
            return "来自智能药盒上报：漏服";
        }
        return "来自智能药盒上报：待服药";
    }

    private void createMissedMedicationAlertIfNeeded(BizMedicationPlan plan, IotSensorLog sensorLog, Date scheduledDate)
    {
        if (!"missed".equalsIgnoreCase(sensorLog.getMetricValue()) || plan == null)
        {
            return;
        }

        String alertRemark = buildMedicationAlertRemark(plan.getPlanId(), scheduledDate);
        BizAlertRecord existed = bizAlertRecordMapper.selectMedicationMissedAlert(plan.getElderlyId(), ALERT_TYPE_MEDICATION_MISSED, alertRemark);
        if (existed != null)
        {
            return;
        }

        BizAlertRecord alertRecord = new BizAlertRecord();
        alertRecord.setElderlyId(plan.getElderlyId());
        alertRecord.setElderlyName(plan.getElderlyName());
        alertRecord.setDeviceId(sensorLog.getDeviceId());
        alertRecord.setDeviceName(sensorLog.getDeviceName());
        alertRecord.setAlertLevel(1L);
        alertRecord.setAlertType(ALERT_TYPE_MEDICATION_MISSED);
        alertRecord.setAlertDesc(buildMedicationAlertDesc(plan));
        alertRecord.setHandleStatus("0");
        alertRecord.setRemark(alertRemark);
        bizAlertRecordService.insertBizAlertRecord(alertRecord);
    }

    private String buildMedicationAlertRemark(Long planId, Date scheduledDate)
    {
        LocalDate localDate = scheduledDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return "medication_plan:" + planId + ":" + localDate;
    }

    private String buildMedicationAlertDesc(BizMedicationPlan plan)
    {
        String elderlyName = StringUtils.defaultIfBlank(plan.getElderlyName(), "老人");
        String planTime = StringUtils.defaultIfBlank(plan.getPlanTime(), "--:--");
        String medicationName = StringUtils.defaultIfBlank(plan.getMedicationName(), "药品");
        return elderlyName + " 在 " + planTime + " 的 " + medicationName + " 疑似漏服，请及时确认";
    }
}
