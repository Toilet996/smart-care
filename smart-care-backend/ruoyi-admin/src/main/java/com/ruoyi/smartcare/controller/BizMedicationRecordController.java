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
import com.ruoyi.smartcare.domain.BizMedicationRecord;
import com.ruoyi.smartcare.service.IBizMedicationRecordService;

@RestController
@RequestMapping("/care/medicationRecord")
public class BizMedicationRecordController extends BaseController
{
    @Autowired
    private IBizMedicationRecordService bizMedicationRecordService;

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMedicationRecord bizMedicationRecord)
    {
        startPage();
        List<BizMedicationRecord> list = bizMedicationRecordService.selectBizMedicationRecordList(bizMedicationRecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:export')")
    @Log(title = "服药执行记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizMedicationRecord bizMedicationRecord)
    {
        List<BizMedicationRecord> list = bizMedicationRecordService.selectBizMedicationRecordList(bizMedicationRecord);
        ExcelUtil<BizMedicationRecord> util = new ExcelUtil<>(BizMedicationRecord.class);
        util.exportExcel(response, list, "服药执行记录数据");
    }

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:list')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId)
    {
        return success(bizMedicationRecordService.selectBizMedicationRecordByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:add')")
    @Log(title = "服药执行记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizMedicationRecord bizMedicationRecord)
    {
        return toAjax(bizMedicationRecordService.insertBizMedicationRecord(bizMedicationRecord));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:edit')")
    @Log(title = "服药执行记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizMedicationRecord bizMedicationRecord)
    {
        return toAjax(bizMedicationRecordService.updateBizMedicationRecord(bizMedicationRecord));
    }

    @PreAuthorize("@ss.hasPermi('care:medicationRecord:remove')")
    @Log(title = "服药执行记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(bizMedicationRecordService.deleteBizMedicationRecordByRecordIds(recordIds));
    }
}
