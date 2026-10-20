package com.ruoyi.smartcare.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 传感器数据上报DTO
 * 用于接收设备上报的数据
 * 
 * @author ruoyi
 * @date 2026-04-01
 */
public class SensorDataDTO
{
    private static final long serialVersionUID = 1L;

    /** 设备编号 */
    private String deviceId;

    /** 指标类型 */
    private String metricType;

    /** 指标数值 */
    private String value;

    /** 上报时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /** 数据列表（批量上报时使用） */
    private java.util.List<SensorMetric> dataList;

    public String getDeviceId() 
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getMetricType() 
    {
        return metricType;
    }

    public void setMetricType(String metricType) 
    {
        this.metricType = metricType;
    }

    public String getValue() 
    {
        return value;
    }

    public void setValue(String value) 
    {
        this.value = value;
    }

    public Date getReportTime() 
    {
        return reportTime;
    }

    public void setReportTime(Date reportTime) 
    {
        this.reportTime = reportTime;
    }

    public java.util.List<SensorMetric> getDataList() 
    {
        return dataList;
    }

    public void setDataList(java.util.List<SensorMetric> dataList) 
    {
        this.dataList = dataList;
    }

    /**
     * 单个指标数据
     */
    public static class SensorMetric
    {
        private String metricType;
        private String value;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date reportTime;

        public String getMetricType() 
        {
            return metricType;
        }

        public void setMetricType(String metricType) 
        {
            this.metricType = metricType;
        }

        public String getValue() 
        {
            return value;
        }

        public void setValue(String value) 
        {
            this.value = value;
        }

        public Date getReportTime() 
        {
            return reportTime;
        }

        public void setReportTime(Date reportTime) 
        {
            this.reportTime = reportTime;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceId", getDeviceId())
            .append("metricType", getMetricType())
            .append("value", getValue())
            .append("reportTime", getReportTime())
            .toString();
    }
}
