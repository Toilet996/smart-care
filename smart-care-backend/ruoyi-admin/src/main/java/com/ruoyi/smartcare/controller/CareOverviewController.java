package com.ruoyi.smartcare.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.smartcare.service.ICareOverviewService;

/**
 * 社区监控总览 Controller
 */
@RestController
@RequestMapping("/care/overview")
public class CareOverviewController extends BaseController
{
    @Autowired
    private ICareOverviewService careOverviewService;

    @PreAuthorize("@ss.hasPermi('care:overview:list')")
    @GetMapping("/data")
    public AjaxResult data()
    {
        Map<String, Object> data = careOverviewService.getOverviewData();
        return success(data);
    }
}
