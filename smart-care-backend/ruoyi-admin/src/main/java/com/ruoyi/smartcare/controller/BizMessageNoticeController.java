package com.ruoyi.smartcare.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.smartcare.domain.BizMessageNotice;
import com.ruoyi.smartcare.service.IBizMessageNoticeService;

@RestController
@RequestMapping("/care/message")
public class BizMessageNoticeController extends BaseController
{
    @Autowired
    private IBizMessageNoticeService bizMessageNoticeService;

    @PreAuthorize("@ss.hasPermi('care:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMessageNotice bizMessageNotice)
    {
        startPage();
        List<BizMessageNotice> list = bizMessageNoticeService.selectBizMessageNoticeList(bizMessageNotice);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('care:message:list')")
    @GetMapping("/stats")
    public AjaxResult stats()
    {
        Map<String, Object> stats = bizMessageNoticeService.selectMessageStats();
        return success(stats);
    }

    @PreAuthorize("@ss.hasPermi('care:message:list')")
    @GetMapping("/{messageId}")
    public AjaxResult getInfo(@PathVariable Long messageId)
    {
        return success(bizMessageNoticeService.selectBizMessageNoticeById(messageId));
    }

    @PreAuthorize("@ss.hasPermi('care:message:list')")
    @PostMapping("/{messageId}/read")
    public AjaxResult markRead(@PathVariable Long messageId)
    {
        return toAjax(bizMessageNoticeService.markRead(messageId));
    }

    @PreAuthorize("@ss.hasPermi('care:message:list')")
    @PostMapping("/readAll")
    public AjaxResult markAllRead()
    {
        return toAjax(bizMessageNoticeService.markAllRead());
    }
}
