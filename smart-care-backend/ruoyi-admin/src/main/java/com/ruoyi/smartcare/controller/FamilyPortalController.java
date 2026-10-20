package com.ruoyi.smartcare.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.smartcare.domain.BizElderlyFamily;
import com.ruoyi.smartcare.domain.FamilyPortalContactRequest;
import com.ruoyi.smartcare.domain.FamilyPortalSosRequest;
import com.ruoyi.smartcare.service.IFamilyPortalService;

/**
 * 家属门户 Controller
 */
@RestController
@RequestMapping("/care/familyPortal")
public class FamilyPortalController extends BaseController
{
    @Autowired
    private IFamilyPortalService familyPortalService;

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @GetMapping("/data")
    public AjaxResult data(@RequestParam(value = "userId", required = false) Long userId,
        @RequestParam(value = "elderlyId", required = false) Long elderlyId)
    {
        Map<String, Object> data = familyPortalService.getPortalData(userId, elderlyId);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @GetMapping("/contacts")
    public AjaxResult contacts(@RequestParam(value = "userId", required = false) Long userId)
    {
        List<BizElderlyFamily> list = familyPortalService.listContacts(userId);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @PostMapping("/contacts")
    public AjaxResult addContact(@RequestParam(value = "userId", required = false) Long userId,
        @RequestBody FamilyPortalContactRequest request)
    {
        return toAjax(familyPortalService.saveContact(userId, request));
    }

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @PutMapping("/contacts")
    public AjaxResult editContact(@RequestParam(value = "userId", required = false) Long userId,
        @RequestBody FamilyPortalContactRequest request)
    {
        return toAjax(familyPortalService.updateContact(userId, request));
    }

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @DeleteMapping("/contacts/{ids}")
    public AjaxResult removeContacts(@RequestParam(value = "userId", required = false) Long userId,
        @PathVariable Long[] ids)
    {
        return toAjax(familyPortalService.deleteContacts(userId, ids));
    }

    @PreAuthorize("@ss.hasPermi('care:family:portal')")
    @PostMapping("/sos")
    public AjaxResult triggerSos(@RequestParam(value = "userId", required = false) Long userId,
        @RequestBody FamilyPortalSosRequest request)
    {
        return success(familyPortalService.triggerSos(userId, request));
    }
}
