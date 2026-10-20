package com.ruoyi.smartcare.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警规则配置对象 biz_alert_rule
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizAlertRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 监控设备类型 */
    @Excel(name = "监控设备类型")
    private String deviceType;

    /** 监控指标 */
    @Excel(name = "监控指标")
    private String metricType;

    /** 判断条件 */
    @Excel(name = "判断条件")
    private String conditionOp;

    /** 阈值 */
    @Excel(name = "阈值")
    private String thresholdValue;

    /** 触发级别 */
    @Excel(name = "触发级别")
    private Long alertLevel;

    /** 规则状态（0正常 1停用） */
    @Excel(name = "规则状态")
    private String status;

    /** 预警类型 */
    @Excel(name = "预警类型")
    private String alertType;

    public void setRuleId(Long ruleId) 
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId() 
    {
        return ruleId;
    }

    public void setRuleName(String ruleName) 
    {
        this.ruleName = ruleName;
    }

    public String getRuleName() 
    {
        return ruleName;
    }

    public void setDeviceType(String deviceType) 
    {
        this.deviceType = deviceType;
    }

    public String getDeviceType() 
    {
        return deviceType;
    }

    public void setMetricType(String metricType) 
    {
        this.metricType = metricType;
    }

    public String getMetricType() 
    {
        return metricType;
    }

    public void setConditionOp(String conditionOp) 
    {
        this.conditionOp = conditionOp;
    }

    public String getConditionOp() 
    {
        return conditionOp;
    }

    public void setThresholdValue(String thresholdValue) 
    {
        this.thresholdValue = thresholdValue;
    }

    public String getThresholdValue() 
    {
        return thresholdValue;
    }

    public void setAlertLevel(Long alertLevel) 
    {
        this.alertLevel = alertLevel;
    }

    public Long getAlertLevel() 
    {
        return alertLevel;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAlertType(String alertType) 
    {
        this.alertType = alertType;
    }

    public String getAlertType() 
    {
        return alertType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ruleId", getRuleId())
            .append("ruleName", getRuleName())
            .append("deviceType", getDeviceType())
            .append("metricType", getMetricType())
            .append("conditionOp", getConditionOp())
            .append("thresholdValue", getThresholdValue())
            .append("alertLevel", getAlertLevel())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
