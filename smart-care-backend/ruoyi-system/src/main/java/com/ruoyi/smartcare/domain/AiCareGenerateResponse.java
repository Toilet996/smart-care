package com.ruoyi.smartcare.domain;

public class AiCareGenerateResponse
{
    private String provider;

    private String model;

    private String content;

    private String healthReport;

    private String dietAdvice;

    private String followUpAdvice;

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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getHealthReport()
    {
        return healthReport;
    }

    public void setHealthReport(String healthReport)
    {
        this.healthReport = healthReport;
    }

    public String getDietAdvice()
    {
        return dietAdvice;
    }

    public void setDietAdvice(String dietAdvice)
    {
        this.dietAdvice = dietAdvice;
    }

    public String getFollowUpAdvice()
    {
        return followUpAdvice;
    }

    public void setFollowUpAdvice(String followUpAdvice)
    {
        this.followUpAdvice = followUpAdvice;
    }
}
