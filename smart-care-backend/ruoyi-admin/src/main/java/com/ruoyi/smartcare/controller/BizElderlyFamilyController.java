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
import com.ruoyi.smartcare.domain.BizElderlyFamily;
import com.ruoyi.smartcare.service.IBizElderlyFamilyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 家属关联Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/care/family")
public class BizElderlyFamilyController extends BaseController
{
    @Autowired
    private IBizElderlyFamilyService bizElderlyFamilyService;

    /**
     * 查询家属关联列表
     */
    @PreAuthorize("@ss.hasPermi('care:family:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizElderlyFamily bizElderlyFamily)
    {
        startPage();
        List<BizElderlyFamily> list = bizElderlyFamilyService.selectBizElderlyFamilyList(bizElderlyFamily);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('care:family:list')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(bizElderlyFamilyService.selectFamilyRelationOptions());
    }

    /**
     * 导出家属关联列表
     */
    @PreAuthorize("@ss.hasPermi('care:family:export')")
    @Log(title = "家属关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizElderlyFamily bizElderlyFamily)
    {
        List<BizElderlyFamily> list = bizElderlyFamilyService.selectBizElderlyFamilyList(bizElderlyFamily);
        ExcelUtil<BizElderlyFamily> util = new ExcelUtil<BizElderlyFamily>(BizElderlyFamily.class);
        util.exportExcel(response, list, "家属关联数据");
    }

    /**
     * 获取家属关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('care:family:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizElderlyFamilyService.selectBizElderlyFamilyById(id));
    }

    /**
     * 新增家属关联
     */
    @PreAuthorize("@ss.hasPermi('care:family:add')")
    @Log(title = "家属关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizElderlyFamily bizElderlyFamily)
    {
        return toAjax(bizElderlyFamilyService.insertBizElderlyFamily(bizElderlyFamily));
    }

    /**
     * 修改家属关联
     */
    @PreAuthorize("@ss.hasPermi('care:family:edit')")
    @Log(title = "家属关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizElderlyFamily bizElderlyFamily)
    {
        return toAjax(bizElderlyFamilyService.updateBizElderlyFamily(bizElderlyFamily));
    }

    /**
     * 删除家属关联
     */
    @PreAuthorize("@ss.hasPermi('care:family:remove')")
    @Log(title = "家属关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizElderlyFamilyService.deleteBizElderlyFamilyByIds(ids));
    }
}
