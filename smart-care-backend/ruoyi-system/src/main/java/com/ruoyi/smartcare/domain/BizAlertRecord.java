package com.ruoyi.smartcare.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 异常预警记录对象 biz_alert_record
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizAlertRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警ID */
    private Long alertId;

    /** 关联工单ID */
    private Long orderId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderlyId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderlyName;

    /** 触发设备ID */
    @Excel(name = "触发设备ID")
    private String deviceId;

    /** 触发设备名称 */
    @Excel(name = "触发设备名称")
    private String deviceName;

    /** 预警级别 */
    @Excel(name = "预警级别")
    private Long alertLevel;

    /** 预警类型 */
    @Excel(name = "预警类型")
    private String alertType;

    /** 预警描述 */
    @Excel(name = "预警描述")
    private String alertDesc;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    public void setAlertId(Long alertId) 
    {
        this.alertId = alertId;
    }

    public Long getAlertId() 
    {
        return alertId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setElderlyId(Long elderlyId) 
    {
        this.elderlyId = elderlyId;
    }

    public Long getElderlyId() 
    {
        return elderlyId;
    }

    public void setElderlyName(String elderlyName) 
    {
        this.elderlyName = elderlyName;
    }

    public String getElderlyName() 
    {
        return elderlyName;
    }

    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId() 
    {
        return deviceId;
    }

    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }

    public void setAlertLevel(Long alertLevel) 
    {
        this.alertLevel = alertLevel;
    }

    public Long getAlertLevel() 
    {
        return alertLevel;
    }

    public void setAlertType(String alertType) 
    {
        this.alertType = alertType;
    }

    public String getAlertType() 
    {
        return alertType;
    }

    public void setAlertDesc(String alertDesc) 
    {
        this.alertDesc = alertDesc;
    }

    public String getAlertDesc() 
    {
        return alertDesc;
    }

    public void setHandleStatus(String handleStatus) 
    {
        this.handleStatus = handleStatus;
    }

    public String getHandleStatus() 
    {
        return handleStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("alertId", getAlertId())
            .append("orderId", getOrderId())
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("alertLevel", getAlertLevel())
            .append("alertType", getAlertType())
            .append("alertDesc", getAlertDesc())
            .append("handleStatus", getHandleStatus())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
