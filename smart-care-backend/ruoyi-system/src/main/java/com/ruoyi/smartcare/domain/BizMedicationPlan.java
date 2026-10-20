package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class BizMedicationPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long planId;

    @Excel(name = "老人ID")
    private Long elderlyId;

    @Excel(name = "老人姓名")
    private String elderlyName;

    @Excel(name = "药品名称")
    private String medicationName;

    @Excel(name = "单次剂量")
    private String dosage;

    @Excel(name = "用药方式")
    private String usageMethod;

    @Excel(name = "服药时段")
    private String periodCode;

    @Excel(name = "计划时间")
    private String planTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "计划状态")
    private String status;

    @Excel(name = "提醒开关")
    private String reminderEnabled;

    private Long recentTakenCount;
    private Long recentMissedCount;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getElderlyId() { return elderlyId; }
    public void setElderlyId(Long elderlyId) { this.elderlyId = elderlyId; }
    public String getElderlyName() { return elderlyName; }
    public void setElderlyName(String elderlyName) { this.elderlyName = elderlyName; }
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getUsageMethod() { return usageMethod; }
    public void setUsageMethod(String usageMethod) { this.usageMethod = usageMethod; }
    public String getPeriodCode() { return periodCode; }
    public void setPeriodCode(String periodCode) { this.periodCode = periodCode; }
    public String getPlanTime() { return planTime; }
    public void setPlanTime(String planTime) { this.planTime = planTime; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(String reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public Long getRecentTakenCount() { return recentTakenCount; }
    public void setRecentTakenCount(Long recentTakenCount) { this.recentTakenCount = recentTakenCount; }
    public Long getRecentMissedCount() { return recentMissedCount; }
    public void setRecentMissedCount(Long recentMissedCount) { this.recentMissedCount = recentMissedCount; }
}
