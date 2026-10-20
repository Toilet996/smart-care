package com.ruoyi.smartcare.service;

import com.ruoyi.smartcare.domain.AiCareGenerateRequest;
import com.ruoyi.smartcare.domain.AiCareGenerateResponse;

public interface IAiCareService
{
    AiCareGenerateResponse generateReport(AiCareGenerateRequest request);
}
