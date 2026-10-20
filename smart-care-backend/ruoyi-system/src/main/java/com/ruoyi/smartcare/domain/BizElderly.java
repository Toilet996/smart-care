package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 老人档案对象 biz_elderly
 *
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizElderly extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long elderlyId;

    @Excel(name = "老人姓名")
    private String elderlyName;

    @Excel(name = "年龄")
    private Long age;

    @Excel(name = "性别")
    private String gender;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "联系电话")
    private String phone;

    @Excel(name = "详细住址")
    private String address;

    @Excel(name = "基础健康状态")
    private String healthStatus;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "纳管状态")
    private String managementStatus;

    @Excel(name = "责任人")
    private String responsiblePerson;

    @Excel(name = "下次随访时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date nextVisitTime;

    @Excel(name = "干预措施")
    private String interventionPlan;

    @Excel(name = "跟进结果")
    private String followUpResult;

    @Excel(name = "最近处置人")
    private String latestHandler;

    @Excel(name = "纳管开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date managementStartTime;

    @Excel(name = "解除原因")
    private String releaseReason;

    private Long deviceCount;

    private Long alertCount;

    private Long openOrderCount;

    public Long getElderlyId()
    {
        return elderlyId;
    }

    public void setElderlyId(Long elderlyId)
    {
        this.elderlyId = elderlyId;
    }

    public String getElderlyName()
    {
        return elderlyName;
    }

    public void setElderlyName(String elderlyName)
    {
        this.elderlyName = elderlyName;
    }

    public Long getAge()
    {
        return age;
    }

    public void setAge(Long age)
    {
        this.age = age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getHealthStatus()
    {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus)
    {
        this.healthStatus = healthStatus;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getManagementStatus()
    {
        return managementStatus;
    }

    public void setManagementStatus(String managementStatus)
    {
        this.managementStatus = managementStatus;
    }

    public String getResponsiblePerson()
    {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson)
    {
        this.responsiblePerson = responsiblePerson;
    }

    public Date getNextVisitTime()
    {
        return nextVisitTime;
    }

    public void setNextVisitTime(Date nextVisitTime)
    {
        this.nextVisitTime = nextVisitTime;
    }

    public String getInterventionPlan()
    {
        return interventionPlan;
    }

    public void setInterventionPlan(String interventionPlan)
    {
        this.interventionPlan = interventionPlan;
    }

    public String getFollowUpResult()
    {
        return followUpResult;
    }

    public void setFollowUpResult(String followUpResult)
    {
        this.followUpResult = followUpResult;
    }

    public String getLatestHandler()
    {
        return latestHandler;
    }

    public void setLatestHandler(String latestHandler)
    {
        this.latestHandler = latestHandler;
    }

    public Date getManagementStartTime()
    {
        return managementStartTime;
    }

    public void setManagementStartTime(Date managementStartTime)
    {
        this.managementStartTime = managementStartTime;
    }

    public String getReleaseReason()
    {
        return releaseReason;
    }

    public void setReleaseReason(String releaseReason)
    {
        this.releaseReason = releaseReason;
    }

    public Long getDeviceCount()
    {
        return deviceCount;
    }

    public void setDeviceCount(Long deviceCount)
    {
        this.deviceCount = deviceCount;
    }

    public Long getAlertCount()
    {
        return alertCount;
    }

    public void setAlertCount(Long alertCount)
    {
        this.alertCount = alertCount;
    }

    public Long getOpenOrderCount()
    {
        return openOrderCount;
    }

    public void setOpenOrderCount(Long openOrderCount)
    {
        this.openOrderCount = openOrderCount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("age", getAge())
            .append("gender", getGender())
            .append("idCard", getIdCard())
            .append("phone", getPhone())
            .append("address", getAddress())
            .append("healthStatus", getHealthStatus())
            .append("status", getStatus())
            .append("managementStatus", getManagementStatus())
            .append("responsiblePerson", getResponsiblePerson())
            .append("nextVisitTime", getNextVisitTime())
            .append("interventionPlan", getInterventionPlan())
            .append("followUpResult", getFollowUpResult())
            .append("latestHandler", getLatestHandler())
            .append("managementStartTime", getManagementStartTime())
            .append("releaseReason", getReleaseReason())
            .append("deviceCount", getDeviceCount())
            .append("alertCount", getAlertCount())
            .append("openOrderCount", getOpenOrderCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
