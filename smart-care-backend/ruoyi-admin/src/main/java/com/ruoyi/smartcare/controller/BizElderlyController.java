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
import com.ruoyi.smartcare.domain.BizElderly;
import com.ruoyi.smartcare.service.IBizElderlyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 老人档案Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/elderly")
public class BizElderlyController extends BaseController
{
    @Autowired
    private IBizElderlyService bizElderlyService;

    /**
     * 查询老人档案列表
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizElderly bizElderly)
    {
        startPage();
        List<BizElderly> list = bizElderlyService.selectBizElderlyList(bizElderly);
        return getDataTable(list);
    }

    /**
     * 导出老人档案列表
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:export')")
    @Log(title = "老人档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizElderly bizElderly)
    {
        List<BizElderly> list = bizElderlyService.selectBizElderlyList(bizElderly);
        ExcelUtil<BizElderly> util = new ExcelUtil<BizElderly>(BizElderly.class);
        util.exportExcel(response, list, "老人档案数据");
    }

    /**
     * 获取老人档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:query')")
    @GetMapping(value = "/{elderlyId}")
    public AjaxResult getInfo(@PathVariable("elderlyId") Long elderlyId)
    {
        return success(bizElderlyService.selectBizElderlyByElderlyId(elderlyId));
    }

    /**
     * 新增老人档案
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:add')")
    @Log(title = "老人档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizElderly bizElderly)
    {
        return toAjax(bizElderlyService.insertBizElderly(bizElderly));
    }

    /**
     * 修改老人档案
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:edit')")
    @Log(title = "老人档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizElderly bizElderly)
    {
        return toAjax(bizElderlyService.updateBizElderly(bizElderly));
    }

    /**
     * 删除老人档案
     */
    @PreAuthorize("@ss.hasPermi('care:elderly:remove')")
    @Log(title = "老人档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{elderlyIds}")
    public AjaxResult remove(@PathVariable Long[] elderlyIds)
    {
        return toAjax(bizElderlyService.deleteBizElderlyByElderlyIds(elderlyIds));
    }
}
