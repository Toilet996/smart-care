package com.ruoyi.smartcare.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.smartcare.domain.BizMessageNotice;

public interface IBizMessageNoticeService
{
    BizMessageNotice selectBizMessageNoticeById(Long messageId);

    List<BizMessageNotice> selectBizMessageNoticeList(BizMessageNotice bizMessageNotice);

    Map<String, Object> selectMessageStats();

    int markRead(Long messageId);

    int markAllRead();
}
