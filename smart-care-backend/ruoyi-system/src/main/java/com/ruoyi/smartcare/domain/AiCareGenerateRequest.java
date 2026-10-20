package com.ruoyi.smartcare.domain;

public class AiCareGenerateRequest
{
    private String provider;

    private String model;

    private Long elderlyId;

    private String elderlyName;

    private Long age;

    private String gender;

    private String healthStatus;

    private String vitalSummary;

    private String alertSummary;

    private String allergyInfo;

    private String dietPreference;

    private String doctorAdvice;

    private String extraPrompt;

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

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

    public String getHealthStatus()
    {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus)
    {
        this.healthStatus = healthStatus;
    }

    public String getVitalSummary()
    {
        return vitalSummary;
    }

    public void setVitalSummary(String vitalSummary)
    {
        this.vitalSummary = vitalSummary;
    }

    public String getAlertSummary()
    {
        return alertSummary;
    }

    public void setAlertSummary(String alertSummary)
    {
        this.alertSummary = alertSummary;
    }

    public String getAllergyInfo()
    {
        return allergyInfo;
    }

    public void setAllergyInfo(String allergyInfo)
    {
        this.allergyInfo = allergyInfo;
    }

    public String getDietPreference()
    {
        return dietPreference;
    }

    public void setDietPreference(String dietPreference)
    {
        this.dietPreference = dietPreference;
    }

    public String getDoctorAdvice()
    {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice)
    {
        this.doctorAdvice = doctorAdvice;
    }

    public String getExtraPrompt()
    {
        return extraPrompt;
    }

    public void setExtraPrompt(String extraPrompt)
    {
        this.extraPrompt = extraPrompt;
    }
}
