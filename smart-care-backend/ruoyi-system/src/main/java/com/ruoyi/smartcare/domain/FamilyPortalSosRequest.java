package com.ruoyi.smartcare.domain;

/**
 * 家属门户 SOS 请求对象
 */
public class FamilyPortalSosRequest
{
    private Long elderlyId;

    private String description;

    private String locationHint;

    private String callbackPhone;

    public Long getElderlyId()
    {
        return elderlyId;
    }

    public void setElderlyId(Long elderlyId)
    {
        this.elderlyId = elderlyId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLocationHint()
    {
        return locationHint;
    }

    public void setLocationHint(String locationHint)
    {
        this.locationHint = locationHint;
    }

    public String getCallbackPhone()
    {
        return callbackPhone;
    }

    public void setCallbackPhone(String callbackPhone)
    {
        this.callbackPhone = callbackPhone;
    }
}
