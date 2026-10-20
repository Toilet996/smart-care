package com.ruoyi.smartcare.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.smartcare.domain.AiCareGenerateRequest;
import com.ruoyi.smartcare.service.IAiCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/care/aiCare")
public class AiCareController extends BaseController
{
    @Autowired
    private IAiCareService aiCareService;

    @PreAuthorize("@ss.hasPermi('care:ai:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody AiCareGenerateRequest request)
    {
        return success(aiCareService.generateReport(request));
    }
}
