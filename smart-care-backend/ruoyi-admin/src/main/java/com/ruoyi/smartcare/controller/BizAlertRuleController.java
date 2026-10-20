package com.ruoyi.smartcare.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.smartcare.domain.BizAlertRule;
import com.ruoyi.smartcare.service.IBizAlertRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预警规则配置Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/alertRule")
public class BizAlertRuleController extends BaseController
{
    @Autowired
    private IBizAlertRuleService bizAlertRuleService;

    /**
     * 查询预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAlertRule bizAlertRule)
    {
        startPage();
        List<BizAlertRule> list = bizAlertRuleService.selectBizAlertRuleList(bizAlertRule);
        return getDataTable(list);
    }

    /**
     * 导出预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:export')")
    @Log(title = "预警规则配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAlertRule bizAlertRule)
    {
        List<BizAlertRule> list = bizAlertRuleService.selectBizAlertRuleList(bizAlertRule);
        ExcelUtil<BizAlertRule> util = new ExcelUtil<BizAlertRule>(BizAlertRule.class);
        util.exportExcel(response, list, "预警规则配置数据");
    }

    /**
     * 获取预警规则配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId)
    {
        return success(bizAlertRuleService.selectBizAlertRuleByRuleId(ruleId));
    }

    /**
     * 新增预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:add')")
    @Log(title = "预警规则配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizAlertRule bizAlertRule)
    {
        return toAjax(bizAlertRuleService.insertBizAlertRule(bizAlertRule));
    }

    /**
     * 修改预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:edit')")
    @Log(title = "预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizAlertRule bizAlertRule)
    {
        return toAjax(bizAlertRuleService.updateBizAlertRule(bizAlertRule));
    }

    /**
     * 删除预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('care:alertRule:remove')")
    @Log(title = "预警规则配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(bizAlertRuleService.deleteBizAlertRuleByRuleIds(ruleIds));
    }
}
