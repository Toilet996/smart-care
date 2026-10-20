package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.domain.SensorDataDTO;

/**
 * 传感器流水Service接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface IIotSensorLogService 
{
    /**
     * 查询传感器流水
     * 
     * @param logId 传感器流水主键
     * @return 传感器流水
     */
    public IotSensorLog selectIotSensorLogByLogId(Long logId);

    /**
     * 查询传感器流水列表
     * 
     * @param iotSensorLog 传感器流水
     * @return 传感器流水集合
     */
    public List<IotSensorLog> selectIotSensorLogList(IotSensorLog iotSensorLog);

    /**
     * 新增传感器流水
     * 
     * @param iotSensorLog 传感器流水
     * @return 结果
     */
    public int insertIotSensorLog(IotSensorLog iotSensorLog);

    /**
     * 修改传感器流水
     * 
     * @param iotSensorLog 传感器流水
     * @return 结果
     */
    public int updateIotSensorLog(IotSensorLog iotSensorLog);

    /**
     * 批量删除传感器流水
     * 
     * @param logIds 需要删除的传感器流水主键集合
     * @return 结果
     */
    public int deleteIotSensorLogByLogIds(Long[] logIds);

    /**
     * 删除传感器流水信息
     * 
     * @param logId 传感器流水主键
     * @return 结果
     */
    public int deleteIotSensorLogByLogId(Long logId);

    /**
     * 传感器数据上报
     * 根据deviceId自动关联老人信息
     * 
     * @param sensorData 传感器数据
     * @return 结果
     */
    public int uploadSensorData(SensorDataDTO sensorData);

    /**
     * 批量传感器数据上报
     * 
     * @param sensorDataList 传感器数据列表
     * @return 成功上传的数据条数
     */
    public int uploadSensorDataBatch(List<SensorDataDTO> sensorDataList);
}
