package com.ruoyi.smartcare.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.smartcare.config.SmartCareAiProperties;
import com.ruoyi.smartcare.domain.AiCareGenerateRequest;
import com.ruoyi.smartcare.domain.AiCareGenerateResponse;
import com.ruoyi.smartcare.service.IAiCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AiCareServiceImpl implements IAiCareService
{
    @Autowired
    private SmartCareAiProperties properties;

    @Override
    public AiCareGenerateResponse generateReport(AiCareGenerateRequest request)
    {
        if (!properties.isEnabled())
        {
            throw new ServiceException("AI 功能当前未启用");
        }

        String provider = normalizeProvider(request.getProvider());
        String endpoint = getEndpoint(provider);
        String apiKey = getApiKey(provider);
        String model = getModel(provider, request.getModel());

        if (apiKey == null || apiKey.trim().isEmpty())
        {
            throw new ServiceException("当前 AI 提供商未配置 API Key，请先在后端配置文件中填写");
        }

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("temperature", 0.6);
        payload.put("stream", false);

        JSONArray messages = new JSONArray();
        messages.add(buildSystemMessage());
        messages.add(buildUserMessage(request));
        payload.put("messages", messages);

        String content = doPost(endpoint, apiKey, payload.toJSONString());

        AiCareGenerateResponse response = new AiCareGenerateResponse();
        response.setProvider(provider);
        response.setModel(model);
        response.setContent(content);
        response.setHealthReport(extractSection(content, "【健康评估报告】", "【饮食建议】"));
        response.setDietAdvice(extractSection(content, "【饮食建议】", "【随访与干预建议】"));
        response.setFollowUpAdvice(extractSection(content, "【随访与干预建议】", null));
        return response;
    }

    private JSONObject buildSystemMessage()
    {
        JSONObject item = new JSONObject();
        item.put("role", "system");
        item.put("content",
            "你是智慧养老系统中的老年健康评估助手。请基于输入信息，输出专业、克制、易读的评估结果，不夸大、不做诊断替代。"
                + "请严格按以下三个标题输出："
                + "【健康评估报告】"
                + "【饮食建议】"
                + "【随访与干预建议】");
        return item;
    }

    private JSONObject buildUserMessage(AiCareGenerateRequest request)
    {
        JSONObject item = new JSONObject();
        item.put("role", "user");
        item.put("content", buildPrompt(request));
        return item;
    }

    private String buildPrompt(AiCareGenerateRequest request)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下老人信息生成健康评估报告和饮食建议。\n");
        prompt.append("老人姓名：").append(safe(request.getElderlyName())).append("\n");
        prompt.append("老人ID：").append(request.getElderlyId() == null ? "--" : request.getElderlyId()).append("\n");
        prompt.append("年龄：").append(request.getAge() == null ? "--" : request.getAge()).append("\n");
        prompt.append("性别：").append(safe(request.getGender())).append("\n");
        prompt.append("基础健康状态：").append(safe(request.getHealthStatus())).append("\n");
        prompt.append("近期生命体征/监测摘要：").append(safe(request.getVitalSummary())).append("\n");
        prompt.append("近期预警摘要：").append(safe(request.getAlertSummary())).append("\n");
        prompt.append("过敏/禁忌：").append(safe(request.getAllergyInfo())).append("\n");
        prompt.append("饮食偏好：").append(safe(request.getDietPreference())).append("\n");
        prompt.append("医生/护理补充意见：").append(safe(request.getDoctorAdvice())).append("\n");
        prompt.append("补充要求：").append(safe(request.getExtraPrompt())).append("\n\n");
        prompt.append("输出要求：\n");
        prompt.append("1. 健康评估报告控制在 4-6 条，突出风险等级、重点观察项、可能原因。\n");
        prompt.append("2. 饮食建议需要给出宜吃、少吃/避免、进食方式建议。\n");
        prompt.append("3. 随访与干预建议需要给出社区/家属下一步动作。\n");
        prompt.append("4. 语言面向社区护理人员和家属，通俗但专业。\n");
        return prompt.toString();
    }

    private String doPost(String endpoint, String apiKey, String body)
    {
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey.trim());

            try (OutputStream os = connection.getOutputStream())
            {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            String responseText = readResponse(status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream());
            if (status < 200 || status >= 300)
            {
                throw new ServiceException("AI 接口调用失败：" + responseText);
            }

            JSONObject json = JSON.parseObject(responseText);
            JSONArray choices = json.getJSONArray("choices");
            if (choices == null || choices.isEmpty())
            {
                throw new ServiceException("AI 接口返回为空");
            }
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message == null ? null : message.getString("content");
            if (content == null || content.trim().isEmpty())
            {
                throw new ServiceException("AI 接口未返回有效内容");
            }
            return content.trim();
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("AI 接口调用异常：" + ex.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private String readResponse(InputStream inputStream) throws Exception
    {
        if (inputStream == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private String normalizeProvider(String provider)
    {
        String value = provider == null ? "" : provider.trim().toLowerCase();
        return "qianfan".equals(value) ? "qianfan" : "qwen";
    }

    private String getEndpoint(String provider)
    {
        return "qianfan".equals(provider) ? properties.getQianfanBaseUrl() : properties.getQwenBaseUrl();
    }

    private String getApiKey(String provider)
    {
        return "qianfan".equals(provider) ? properties.getQianfanApiKey() : properties.getQwenApiKey();
    }

    private String getModel(String provider, String requestModel)
    {
        if (requestModel != null && !requestModel.trim().isEmpty())
        {
            return requestModel.trim();
        }
        return "qianfan".equals(provider) ? properties.getQianfanModel() : properties.getQwenModel();
    }

    private String safe(String value)
    {
        return value == null || value.trim().isEmpty() ? "无" : value.trim();
    }

    private String extractSection(String source, String startMarker, String endMarker)
    {
        if (source == null || startMarker == null)
        {
            return "";
        }
        int start = source.indexOf(startMarker);
        if (start < 0)
        {
            return "";
        }
        int contentStart = start + startMarker.length();
        int end = endMarker == null ? source.length() : source.indexOf(endMarker, contentStart);
        if (end < 0)
        {
            end = source.length();
        }
        return source.substring(contentStart, end).trim();
    }
}
