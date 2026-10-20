package com.ruoyi.smartcare.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizAlertRule;
import com.ruoyi.smartcare.domain.IotDevice;

/**
 * 预警分析服务接口
 * 根据传感器数据和规则进行实时预警分析
 * 
 * @author ruoyi
 * @date 2026-04-01
 */
public interface IAlertAnalysisService
{
    /**
     * 分析单条传感器数据，匹配预警规则
     * 
     * @param sensorLog 传感器数据
     * @return 预警记录列表（可能为空）
     */
    public List<BizAlertRecord> analyzeSensorData(IotSensorLog sensorLog);

    /**
     * 分析传感器数据列表
     * 
     * @param sensorLogs 传感器数据列表
     * @return 触发的预警列表
     */
    public List<BizAlertRecord> analyzeSensorDataBatch(List<IotSensorLog> sensorLogs);

    /**
     * 执行定时预警扫描
     * 扫描最近N分钟内的传感器数据，分析异常
     */
    public void runScheduledAnalysis();

    /**
     * 检查老人是否长时间未活动
     * 
     * @param elderlyId 老人ID
     * @param minutes 分钟数
     * @return true表示异常
     */
    public boolean checkInactivity(Long elderlyId, int minutes);
}
