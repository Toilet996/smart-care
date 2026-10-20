package com.ruoyi.smartcare.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 家属关联对象 biz_elderly_family
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizElderlyFamily extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联主键 */
    private Long id;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderlyId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderlyName;

    /** 家属用户ID */
    @Excel(name = "家属用户ID")
    private Long userId;

    /** 家属姓名 */
    @Excel(name = "家属姓名")
    private String familyName;

    /** 家属电话 */
    @Excel(name = "家属电话")
    private String familyPhone;

    /** 亲属关系 */
    @Excel(name = "亲属关系")
    private String relation;

    /** 是否紧急联系人 (0否 1是) */
    @Excel(name = "是否紧急联系人 (0否 1是)")
    private String isEmergency;

    /** 关联老人数量 */
    private Long relatedElderlyCount;

    /** 关联老人名单 */
    private String elderlyNames;

    /** 最近预警老人 */
    private String latestAlertElderlyName;

    /** 最近预警时间 */
    private java.util.Date latestAlertTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setFamilyName(String familyName) 
    {
        this.familyName = familyName;
    }

    public String getFamilyName() 
    {
        return familyName;
    }

    public void setFamilyPhone(String familyPhone) 
    {
        this.familyPhone = familyPhone;
    }

    public String getFamilyPhone() 
    {
        return familyPhone;
    }

    public void setRelation(String relation) 
    {
        this.relation = relation;
    }

    public String getRelation() 
    {
        return relation;
    }

    public void setIsEmergency(String isEmergency) 
    {
        this.isEmergency = isEmergency;
    }

    public String getIsEmergency() 
    {
        return isEmergency;
    }

    public void setRelatedElderlyCount(Long relatedElderlyCount)
    {
        this.relatedElderlyCount = relatedElderlyCount;
    }

    public Long getRelatedElderlyCount()
    {
        return relatedElderlyCount;
    }

    public void setElderlyNames(String elderlyNames)
    {
        this.elderlyNames = elderlyNames;
    }

    public String getElderlyNames()
    {
        return elderlyNames;
    }

    public void setLatestAlertElderlyName(String latestAlertElderlyName)
    {
        this.latestAlertElderlyName = latestAlertElderlyName;
    }

    public String getLatestAlertElderlyName()
    {
        return latestAlertElderlyName;
    }

    public void setLatestAlertTime(java.util.Date latestAlertTime)
    {
        this.latestAlertTime = latestAlertTime;
    }

    public java.util.Date getLatestAlertTime()
    {
        return latestAlertTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("userId", getUserId())
            .append("familyName", getFamilyName())
            .append("familyPhone", getFamilyPhone())
            .append("relation", getRelation())
            .append("isEmergency", getIsEmergency())
            .append("relatedElderlyCount", getRelatedElderlyCount())
            .append("elderlyNames", getElderlyNames())
            .append("latestAlertElderlyName", getLatestAlertElderlyName())
            .append("latestAlertTime", getLatestAlertTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
