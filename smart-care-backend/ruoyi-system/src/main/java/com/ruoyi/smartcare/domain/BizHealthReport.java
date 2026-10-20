package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 健康评估报告对象 biz_health_report
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class BizHealthReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告ID */
    private Long reportId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderlyId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderlyName;

    /** 报告类型 */
    @Excel(name = "报告类型")
    private String reportType;

    /** 报告日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportDate;

    /** 睡眠得分 */
    @Excel(name = "睡眠得分")
    private Long sleepScore;

    /** 活力得分 */
    @Excel(name = "活力得分")
    private Long activityScore;

    /** AI健康综合评估 */
    @Excel(name = "AI健康综合评估")
    private String aiEvaluation;

    public void setReportId(Long reportId) 
    {
        this.reportId = reportId;
    }

    public Long getReportId() 
    {
        return reportId;
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

    public void setReportType(String reportType) 
    {
        this.reportType = reportType;
    }

    public String getReportType() 
    {
        return reportType;
    }

    public void setReportDate(Date reportDate) 
    {
        this.reportDate = reportDate;
    }

    public Date getReportDate() 
    {
        return reportDate;
    }

    public void setSleepScore(Long sleepScore) 
    {
        this.sleepScore = sleepScore;
    }

    public Long getSleepScore() 
    {
        return sleepScore;
    }

    public void setActivityScore(Long activityScore) 
    {
        this.activityScore = activityScore;
    }

    public Long getActivityScore() 
    {
        return activityScore;
    }

    public void setAiEvaluation(String aiEvaluation) 
    {
        this.aiEvaluation = aiEvaluation;
    }

    public String getAiEvaluation() 
    {
        return aiEvaluation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("reportId", getReportId())
            .append("elderlyId", getElderlyId())
            .append("elderlyName", getElderlyName())
            .append("reportType", getReportType())
            .append("reportDate", getReportDate())
            .append("sleepScore", getSleepScore())
            .append("activityScore", getActivityScore())
            .append("aiEvaluation", getAiEvaluation())
            .append("createTime", getCreateTime())
            .toString();
    }
}
