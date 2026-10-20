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
import com.ruoyi.smartcare.domain.BizHealthReport;
import com.ruoyi.smartcare.service.IBizHealthReportService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 健康评估报告Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/healthReport")
public class BizHealthReportController extends BaseController
{
    @Autowired
    private IBizHealthReportService bizHealthReportService;

    /**
     * 查询健康评估报告列表
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizHealthReport bizHealthReport)
    {
        startPage();
        List<BizHealthReport> list = bizHealthReportService.selectBizHealthReportList(bizHealthReport);
        return getDataTable(list);
    }

    /**
     * 导出健康评估报告列表
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:export')")
    @Log(title = "健康评估报告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizHealthReport bizHealthReport)
    {
        List<BizHealthReport> list = bizHealthReportService.selectBizHealthReportList(bizHealthReport);
        ExcelUtil<BizHealthReport> util = new ExcelUtil<BizHealthReport>(BizHealthReport.class);
        util.exportExcel(response, list, "健康评估报告数据");
    }

    /**
     * 获取健康评估报告详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:query')")
    @GetMapping(value = "/{reportId}")
    public AjaxResult getInfo(@PathVariable("reportId") Long reportId)
    {
        return success(bizHealthReportService.selectBizHealthReportByReportId(reportId));
    }

    /**
     * 新增健康评估报告
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:add')")
    @Log(title = "健康评估报告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizHealthReport bizHealthReport)
    {
        return toAjax(bizHealthReportService.insertBizHealthReport(bizHealthReport));
    }

    /**
     * 修改健康评估报告
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:edit')")
    @Log(title = "健康评估报告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizHealthReport bizHealthReport)
    {
        return toAjax(bizHealthReportService.updateBizHealthReport(bizHealthReport));
    }

    /**
     * 删除健康评估报告
     */
    @PreAuthorize("@ss.hasPermi('care:healthReport:remove')")
    @Log(title = "健康评估报告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{reportIds}")
    public AjaxResult remove(@PathVariable Long[] reportIds)
    {
        return toAjax(bizHealthReportService.deleteBizHealthReportByReportIds(reportIds));
    }
}
