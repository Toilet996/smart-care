package com.ruoyi.smartcare.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.IotDeviceMapper;
import com.ruoyi.smartcare.domain.IotDevice;
import com.ruoyi.smartcare.service.IIotDeviceService;

/**
 * 设备台账Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class IotDeviceServiceImpl implements IIotDeviceService 
{
    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    /**
     * 查询设备台账
     * 
     * @param id 设备台账主键
     * @return 设备台账
     */
    @Override
    public IotDevice selectIotDeviceById(Long id)
    {
        return iotDeviceMapper.selectIotDeviceById(id);
    }

    /**
     * 查询设备台账列表
     * 
     * @param iotDevice 设备台账
     * @return 设备台账
     */
    @Override
    public List<IotDevice> selectIotDeviceList(IotDevice iotDevice)
    {
        return iotDeviceMapper.selectIotDeviceList(iotDevice);
    }

    /**
     * 新增设备台账
     * 如果设备编号为空，自动生成
     * 
     * @param iotDevice 设备台账
     * @return 结果
     */
    @Override
    public int insertIotDevice(IotDevice iotDevice)
    {
        // 如果设备编号为空，自动生成
        if (iotDevice.getDeviceId() == null || iotDevice.getDeviceId().isEmpty())
        {
            iotDevice.setDeviceId("DEV_" + System.currentTimeMillis());
        }
        return iotDeviceMapper.insertIotDevice(iotDevice);
    }

    /**
     * 修改设备台账
     * 
     * @param iotDevice 设备台账
     * @return 结果
     */
    @Override
    public int updateIotDevice(IotDevice iotDevice)
    {
        return iotDeviceMapper.updateIotDevice(iotDevice);
    }

    /**
     * 批量删除设备台账
     * 
     * @param ids 需要删除的设备台账主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceByIds(Long[] ids)
    {
        return iotDeviceMapper.deleteIotDeviceByIds(ids);
    }

    /**
     * 删除设备台账信息
     * 
     * @param id 设备台账主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceById(Long id)
    {
        return iotDeviceMapper.deleteIotDeviceById(id);
    }

    /**
     * 根据设备编号查询设备台账
     * 
     * @param deviceId 设备编号
     * @return 设备台账
     */
    @Override
    public IotDevice selectIotDeviceByDeviceId(String deviceId)
    {
        return iotDeviceMapper.selectIotDeviceByDeviceId(deviceId);
    }

    /**
     * 删除设备台账（根据设备编号）
     * 
     * @param deviceIds 设备编号数组
     * @return 结果
     */
    @Override
    public int deleteIotDeviceByDeviceIds(String[] deviceIds)
    {
        return iotDeviceMapper.deleteIotDeviceByDeviceIds(deviceIds);
    }
}
