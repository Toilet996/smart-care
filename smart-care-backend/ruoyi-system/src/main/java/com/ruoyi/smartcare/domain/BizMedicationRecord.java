package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class BizMedicationRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long planId;

    @Excel(name = "老人ID")
    private Long elderlyId;

    @Excel(name = "老人姓名")
    private String elderlyName;

    @Excel(name = "药品名称")
    private String medicationName;

    @Excel(name = "计划时间")
    private String planTime;

    @Excel(name = "执行状态")
    private String executeStatus;

    @Excel(name = "数据来源")
    private String sourceType;

    private String metricValue;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scheduledDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划时间点", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date scheduledTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际执行时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actualTime;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getElderlyName() { return elderlyName; }
    public void setElderlyName(String elderlyName) { this.elderlyName = elderlyName; }
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getPlanTime() { return planTime; }
    public void setPlanTime(String planTime) { this.planTime = planTime; }
    public String getExecuteStatus() { return executeStatus; }
    public void setExecuteStatus(String executeStatus) { this.executeStatus = executeStatus; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getMetricValue() { return metricValue; }
    public void setMetricValue(String metricValue) { this.metricValue = metricValue; }
    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }
    public Date getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(Date scheduledTime) { this.scheduledTime = scheduledTime; }
    public Date getActualTime() { return actualTime; }
    public void setActualTime(Date actualTime) { this.actualTime = actualTime; }
}
