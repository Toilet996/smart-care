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
import com.ruoyi.smartcare.domain.BizVisitRecord;
import com.ruoyi.smartcare.service.IBizVisitRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 日常随访记录Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/visitRecord")
public class BizVisitRecordController extends BaseController
{
    @Autowired
    private IBizVisitRecordService bizVisitRecordService;

    /**
     * 查询日常随访记录列表
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizVisitRecord bizVisitRecord)
    {
        startPage();
        List<BizVisitRecord> list = bizVisitRecordService.selectBizVisitRecordList(bizVisitRecord);
        return getDataTable(list);
    }

    /**
     * 导出日常随访记录列表
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:export')")
    @Log(title = "日常随访记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizVisitRecord bizVisitRecord)
    {
        List<BizVisitRecord> list = bizVisitRecordService.selectBizVisitRecordList(bizVisitRecord);
        ExcelUtil<BizVisitRecord> util = new ExcelUtil<BizVisitRecord>(BizVisitRecord.class);
        util.exportExcel(response, list, "日常随访记录数据");
    }

    /**
     * 获取日常随访记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(bizVisitRecordService.selectBizVisitRecordByRecordId(recordId));
    }

    /**
     * 新增日常随访记录
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:add')")
    @Log(title = "日常随访记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizVisitRecord bizVisitRecord)
    {
        return toAjax(bizVisitRecordService.insertBizVisitRecord(bizVisitRecord));
    }

    /**
     * 修改日常随访记录
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:edit')")
    @Log(title = "日常随访记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizVisitRecord bizVisitRecord)
    {
        return toAjax(bizVisitRecordService.updateBizVisitRecord(bizVisitRecord));
    }

    /**
     * 删除日常随访记录
     */
    @PreAuthorize("@ss.hasPermi('care:visitRecord:remove')")
    @Log(title = "日常随访记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(bizVisitRecordService.deleteBizVisitRecordByRecordIds(recordIds));
    }
}
