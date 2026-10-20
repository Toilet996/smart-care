package com.ruoyi.smartcare.mapper;

import java.util.List;
import com.ruoyi.smartcare.domain.IotDevice;

/**
 * 设备台账Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface IotDeviceMapper 
{
    /**
     * 查询设备台账
     * 
     * @param id 设备台账主键
     * @return 设备台账
     */
    public IotDevice selectIotDeviceById(Long id);

    /**
     * 查询设备台账列表
     * 
     * @param iotDevice 设备台账
     * @return 设备台账集合
     */
    public List<IotDevice> selectIotDeviceList(IotDevice iotDevice);

    /**
     * 新增设备台账
     * 
     * @param iotDevice 设备台账
     * @return 结果
     */
    public int insertIotDevice(IotDevice iotDevice);

    /**
     * 修改设备台账
     * 
     * @param iotDevice 设备台账
     * @return 结果
     */
    public int updateIotDevice(IotDevice iotDevice);

    /**
     * 删除设备台账
     * 
     * @param id 设备台账主键
     * @return 结果
     */
    public int deleteIotDeviceById(Long id);

    /**
     * 批量删除设备台账
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotDeviceByIds(Long[] ids);

    /**
     * 根据设备编号查询设备信息
     * 
     * @param deviceId 设备编号
     * @return 设备台账
     */
    public IotDevice selectIotDeviceByDeviceId(String deviceId);

    /**
     * 根据设备编号批量删除设备台账
     * 
     * @param deviceIds 设备编号集合
     * @return 结果
     */
    public int deleteIotDeviceByDeviceIds(String[] deviceIds);
}
