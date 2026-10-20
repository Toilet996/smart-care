package com.ruoyi.smartcare.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.smartcare.domain.BizMedicationPlan;
import com.ruoyi.smartcare.service.IBizMedicationPlanService;

@RestController
@RequestMapping("/care/medicationPlan")
public class BizMedicationPlanController extends BaseController
{
    @Autowired
    private IBizMedicationPlanService bizMedicationPlanService;

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMedicationPlan bizMedicationPlan)
    {
        startPage();
        List<BizMedicationPlan> list = bizMedicationPlanService.selectBizMedicationPlanList(bizMedicationPlan);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:export')")
    @Log(title = "服药计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizMedicationPlan bizMedicationPlan)
    {
        List<BizMedicationPlan> list = bizMedicationPlanService.selectBizMedicationPlanList(bizMedicationPlan);
        ExcelUtil<BizMedicationPlan> util = new ExcelUtil<>(BizMedicationPlan.class);
        util.exportExcel(response, list, "服药计划数据");
    }

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:query')")
    @GetMapping("/{planId}")
    public AjaxResult getInfo(@PathVariable Long planId)
    {
        return success(bizMedicationPlanService.selectBizMedicationPlanByPlanId(planId));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:add')")
    @Log(title = "服药计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizMedicationPlan bizMedicationPlan)
    {
        return toAjax(bizMedicationPlanService.insertBizMedicationPlan(bizMedicationPlan));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:edit')")
    @Log(title = "服药计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizMedicationPlan bizMedicationPlan)
    {
        return toAjax(bizMedicationPlanService.updateBizMedicationPlan(bizMedicationPlan));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationPlan:remove')")
    @Log(title = "服药计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds)
    {
        return toAjax(bizMedicationPlanService.deleteBizMedicationPlanByPlanIds(planIds));
    }
}
