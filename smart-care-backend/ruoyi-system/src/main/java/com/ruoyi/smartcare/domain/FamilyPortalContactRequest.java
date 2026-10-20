package com.ruoyi.smartcare.domain;

/**
 * 家属门户联系人维护请求
 */
public class FamilyPortalContactRequest
{
    private Long id;

    private Long elderlyId;

    private String familyName;

    private String familyPhone;

    private String relation;

    private String isEmergency;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getElderlyId()
    {
        return elderlyId;
    }

    public void setElderlyId(Long elderlyId)
    {
        this.elderlyId = elderlyId;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    public String getFamilyPhone()
    {
        return familyPhone;
    }

    public void setFamilyPhone(String familyPhone)
    {
        this.familyPhone = familyPhone;
    }

    public String getRelation()
    {
        return relation;
    }

    public void setRelation(String relation)
    {
        this.relation = relation;
    }

    public String getIsEmergency()
    {
        return isEmergency;
    }

    public void setIsEmergency(String isEmergency)
    {
        this.isEmergency = isEmergency;
    }
}
