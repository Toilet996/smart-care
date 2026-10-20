package com.ruoyi.smartcare.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.smartcare.domain.BizMessageNotice;
import com.ruoyi.smartcare.mapper.BizMessageNoticeMapper;
import com.ruoyi.smartcare.service.IBizMessageNoticeService;

@Service
public class BizMessageNoticeServiceImpl implements IBizMessageNoticeService
{
    @Autowired
    private BizMessageNoticeMapper bizMessageNoticeMapper;

    @Override
    public BizMessageNotice selectBizMessageNoticeById(Long messageId)
    {
        BizMessageNotice notice = bizMessageNoticeMapper.selectBizMessageNoticeById(messageId);
        if (notice != null && !SecurityUtils.getUserId().equals(notice.getUserId()))
        {
            return null;
        }
        return notice;
    }

    @Override
    public List<BizMessageNotice> selectBizMessageNoticeList(BizMessageNotice bizMessageNotice)
    {
        bizMessageNotice.setUserId(SecurityUtils.getUserId());
        return bizMessageNoticeMapper.selectBizMessageNoticeList(bizMessageNotice);
    }

    @Override
    public Map<String, Object> selectMessageStats()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0L);
        result.put("unread", 0L);
        result.put("urgent", 0L);
        result.put("today", 0L);
        List<Map<String, Object>> rows = bizMessageNoticeMapper.selectMessageStats(SecurityUtils.getUserId());
        for (Map<String, Object> row : rows)
        {
            result.put(String.valueOf(row.get("statKey")), row.get("statValue"));
        }
        return result;
    }

    @Override
    public int markRead(Long messageId)
    {
        return bizMessageNoticeMapper.updateBizMessageNoticeRead(messageId, SecurityUtils.getUserId());
    }

    @Override
    public int markAllRead()
    {
        return bizMessageNoticeMapper.updateAllBizMessageNoticeRead(SecurityUtils.getUserId());
    }
}
