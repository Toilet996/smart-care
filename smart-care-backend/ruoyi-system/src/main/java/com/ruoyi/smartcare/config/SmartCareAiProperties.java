package com.ruoyi.smartcare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smartcare.ai")
public class SmartCareAiProperties
{
    private boolean enabled = true;

    private String qwenBaseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private String qwenApiKey;

    private String qwenModel = "qwen-plus";

    private String qianfanBaseUrl = "https://qianfan.baidubce.com/v2/chat/completions";

    private String qianfanApiKey;

    private String qianfanModel = "ernie-4.5-turbo-128k";

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getQwenBaseUrl()
    {
        return qwenBaseUrl;
    }

    public void setQwenBaseUrl(String qwenBaseUrl)
    {
        this.qwenBaseUrl = qwenBaseUrl;
    }

    public String getQwenApiKey()
    {
        return qwenApiKey;
    }

    public void setQwenApiKey(String qwenApiKey)
    {
        this.qwenApiKey = qwenApiKey;
    }

    public String getQwenModel()
    {
        return qwenModel;
    }

    public void setQwenModel(String qwenModel)
    {
        this.qwenModel = qwenModel;
    }

    public String getQianfanBaseUrl()
    {
        return qianfanBaseUrl;
    }

    public void setQianfanBaseUrl(String qianfanBaseUrl)
    {
        this.qianfanBaseUrl = qianfanBaseUrl;
    }

    public String getQianfanApiKey()
    {
        return qianfanApiKey;
    }

    public void setQianfanApiKey(String qianfanApiKey)
    {
        this.qianfanApiKey = qianfanApiKey;
    }

    public String getQianfanModel()
    {
        return qianfanModel;
    }

    public void setQianfanModel(String qianfanModel)
    {
        this.qianfanModel = qianfanModel;
    }
}
