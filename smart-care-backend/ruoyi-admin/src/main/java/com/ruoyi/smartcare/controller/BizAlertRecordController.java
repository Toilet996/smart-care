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
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.service.IBizAlertRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 异常预警记录Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/alertRecord")
public class BizAlertRecordController extends BaseController
{
    @Autowired
    private IBizAlertRecordService bizAlertRecordService;

    /**
     * 查询异常预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAlertRecord bizAlertRecord)
    {
        startPage();
        List<BizAlertRecord> list = bizAlertRecordService.selectBizAlertRecordList(bizAlertRecord);
        return getDataTable(list);
    }

    /**
     * 导出异常预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:export')")
    @Log(title = "异常预警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAlertRecord bizAlertRecord)
    {
        List<BizAlertRecord> list = bizAlertRecordService.selectBizAlertRecordList(bizAlertRecord);
        ExcelUtil<BizAlertRecord> util = new ExcelUtil<BizAlertRecord>(BizAlertRecord.class);
        util.exportExcel(response, list, "异常预警记录数据");
    }

    /**
     * 获取异常预警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:query')")
    @GetMapping(value = "/{alertId}")
    public AjaxResult getInfo(@PathVariable("alertId") Long alertId)
    {
        return success(bizAlertRecordService.selectBizAlertRecordByAlertId(alertId));
    }

    /**
     * 新增异常预警记录
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:add')")
    @Log(title = "异常预警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizAlertRecord bizAlertRecord)
    {
        return toAjax(bizAlertRecordService.insertBizAlertRecord(bizAlertRecord));
    }

    /**
     * 修改异常预警记录
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:edit')")
    @Log(title = "异常预警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizAlertRecord bizAlertRecord)
    {
        return toAjax(bizAlertRecordService.updateBizAlertRecord(bizAlertRecord));
    }

    /**
     * 删除异常预警记录
     */
    @PreAuthorize("@ss.hasPermi('care:alertRecord:remove')")
    @Log(title = "异常预警记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alertIds}")
    public AjaxResult remove(@PathVariable Long[] alertIds)
    {
        return toAjax(bizAlertRecordService.deleteBizAlertRecordByAlertIds(alertIds));
    }
}
