package com.ruoyi.smartcare.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 处置工单对象 biz_work_order
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizWorkOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工单ID */
    private Long orderId;

    /** 关联预警ID */
    @Excel(name = "关联预警ID")
    private Long alertId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderlyId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderlyName;

    /** 处置记录 */
    @Excel(name = "处置记录")
    private String processRecord;

    /** 工单状态 */
    @Excel(name = "工单状态")
    private String status;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setAlertId(Long alertId) 
    {
        this.alertId = alertId;
    }

    public Long getAlertId() 
    {
        return alertId;
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

    public void setProcessRecord(String processRecord) 
    {
        this.processRecord = processRecord;
    }

    public String getProcessRecord() 
    {
        return processRecord;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("alertId", getAlertId())
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("processRecord", getProcessRecord())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
