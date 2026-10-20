package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 日常随访记录对象 biz_visit_record
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizVisitRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderlyId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderlyName;

    /** 随访类型 */
    @Excel(name = "随访类型")
    private String visitType;

    /** 上门时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上门时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date visitTime;

    /** 随访详细记录 */
    @Excel(name = "随访详细记录")
    private String visitContent;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
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

    public void setVisitType(String visitType) 
    {
        this.visitType = visitType;
    }

    public String getVisitType() 
    {
        return visitType;
    }

    public void setVisitTime(Date visitTime) 
    {
        this.visitTime = visitTime;
    }

    public Date getVisitTime() 
    {
        return visitTime;
    }

    public void setVisitContent(String visitContent) 
    {
        this.visitContent = visitContent;
    }

    public String getVisitContent() 
    {
        return visitContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("visitType", getVisitType())
            .append("visitTime", getVisitTime())
            .append("visitContent", getVisitContent())
            .append("createBy", getCreateBy())
            .toString();
    }
}
