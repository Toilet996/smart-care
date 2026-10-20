package com.ruoyi.smartcare.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.IotDevice;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.domain.SensorDataDTO;
import com.ruoyi.smartcare.mapper.IotDeviceMapper;
import com.ruoyi.smartcare.mapper.IotSensorLogMapper;
import com.ruoyi.smartcare.service.IAlertAnalysisService;
import com.ruoyi.smartcare.service.IBizMedicationRecordService;
import com.ruoyi.smartcare.service.IIotSensorLogService;

@Service
public class IotSensorLogServiceImpl implements IIotSensorLogService
{
    private static final Logger log = LoggerFactory.getLogger(IotSensorLogServiceImpl.class);

    @Autowired
    private IotSensorLogMapper iotSensorLogMapper;

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private IAlertAnalysisService alertAnalysisService;

    @Autowired
    private IBizMedicationRecordService bizMedicationRecordService;

    @Override
    public IotSensorLog selectIotSensorLogByLogId(Long logId)
    {
        return iotSensorLogMapper.selectIotSensorLogByLogId(logId);
    }

    @Override
    public List<IotSensorLog> selectIotSensorLogList(IotSensorLog iotSensorLog)
    {
        return iotSensorLogMapper.selectIotSensorLogList(iotSensorLog);
    }

    @Override
    public int insertIotSensorLog(IotSensorLog iotSensorLog)
    {
        return iotSensorLogMapper.insertIotSensorLog(iotSensorLog);
    }

    @Override
    public int updateIotSensorLog(IotSensorLog iotSensorLog)
    {
        return iotSensorLogMapper.updateIotSensorLog(iotSensorLog);
    }

    @Override
    public int deleteIotSensorLogByLogIds(Long[] logIds)
    {
        return iotSensorLogMapper.deleteIotSensorLogByLogIds(logIds);
    }

    @Override
    public int deleteIotSensorLogByLogId(Long logId)
    {
        return iotSensorLogMapper.deleteIotSensorLogByLogId(logId);
    }

    @Override
    @Transactional
    public int uploadSensorData(SensorDataDTO sensorData)
    {
        if (sensorData.getDeviceId() == null || sensorData.getDeviceId().isEmpty())
        {
            log.warn("传感器数据上报失败：设备ID为空");
            return 0;
        }

        IotDevice device = iotDeviceMapper.selectIotDeviceByDeviceId(sensorData.getDeviceId());
        if (device == null)
        {
            log.warn("传感器数据上报失败：设备[{}]不存在", sensorData.getDeviceId());
            return 0;
        }

        IotSensorLog sensorLog = createSensorLog(sensorData, device);
        log.info("传感器数据上报成功：设备[{}] 老人[{}] 指标[{}] 值[{}]",
            sensorData.getDeviceId(), device.getElderlyName(), sensorData.getMetricType(), sensorData.getValue());

        int result = iotSensorLogMapper.insertIotSensorLog(sensorLog);
        if (result > 0)
        {
            bizMedicationRecordService.syncBySensorLog(sensorLog);
            runAlertAnalysis(sensorLog);
        }
        return result;
    }

    @Override
    @Transactional
    public int uploadSensorDataBatch(List<SensorDataDTO> sensorDataList)
    {
        if (sensorDataList == null || sensorDataList.isEmpty())
        {
            return 0;
        }

        List<IotSensorLog> logList = new ArrayList<>();
        for (SensorDataDTO sensorData : sensorDataList)
        {
            if (sensorData.getDeviceId() == null || sensorData.getDeviceId().isEmpty())
            {
                continue;
            }

            IotDevice device = iotDeviceMapper.selectIotDeviceByDeviceId(sensorData.getDeviceId());
            if (device == null)
            {
                log.warn("批量上报跳过：设备[{}]不存在", sensorData.getDeviceId());
                continue;
            }

            if (sensorData.getMetricType() != null && !sensorData.getMetricType().isEmpty())
            {
                logList.add(createSensorLog(sensorData, device));
            }
            else if (sensorData.getDataList() != null && !sensorData.getDataList().isEmpty())
            {
                for (SensorDataDTO.SensorMetric metric : sensorData.getDataList())
                {
                    SensorDataDTO item = new SensorDataDTO();
                    item.setDeviceId(sensorData.getDeviceId());
                    item.setMetricType(metric.getMetricType());
                    item.setValue(metric.getValue());
                    item.setReportTime(metric.getReportTime());
                    logList.add(createSensorLog(item, device));
                }
            }
        }

        if (logList.isEmpty())
        {
            return 0;
        }

        int successCount = iotSensorLogMapper.batchInsertIotSensorLog(logList);
        for (IotSensorLog sensorLog : logList)
        {
            bizMedicationRecordService.syncBySensorLog(sensorLog);
            runAlertAnalysis(sensorLog);
        }
        log.info("批量传感器数据上报成功，共[{}]条记录", successCount);
        return successCount;
    }

    private IotSensorLog createSensorLog(SensorDataDTO sensorData, IotDevice device)
    {
        IotSensorLog sensorLog = new IotSensorLog();
        sensorLog.setDeviceId(sensorData.getDeviceId());
        sensorLog.setDeviceName(device.getDeviceName());
        sensorLog.setElderlyId(device.getElderlyId());
        sensorLog.setElderlyName(device.getElderlyName());
        sensorLog.setMetricType(sensorData.getMetricType());
        sensorLog.setMetricValue(sensorData.getValue());
        sensorLog.setReportTime(sensorData.getReportTime() != null ? sensorData.getReportTime() : new Date());
        return sensorLog;
    }

    private void runAlertAnalysis(IotSensorLog sensorLog)
    {
        try
        {
            List<BizAlertRecord> alerts = alertAnalysisService.analyzeSensorData(sensorLog);
            if (alerts != null && !alerts.isEmpty())
            {
                log.info("数据上报触发预警，共[{}]条", alerts.size());
            }
        }
        catch (Exception e)
        {
            log.error("预警分析执行失败: {}", e.getMessage());
        }
    }
}
