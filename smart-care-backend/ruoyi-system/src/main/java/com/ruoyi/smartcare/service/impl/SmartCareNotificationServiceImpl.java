package com.ruoyi.smartcare.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizMessageNotice;
import com.ruoyi.smartcare.domain.BizWorkOrder;
import com.ruoyi.smartcare.mapper.BizElderlyFamilyMapper;
import com.ruoyi.smartcare.mapper.BizMessageNoticeMapper;
import com.ruoyi.smartcare.service.ISmartCareNotificationService;

@Service
public class SmartCareNotificationServiceImpl implements ISmartCareNotificationService
{
    @Autowired
    private BizMessageNoticeMapper bizMessageNoticeMapper;

    @Autowired
    private BizElderlyFamilyMapper bizElderlyFamilyMapper;

    @Override
    public void notifyAlertCreated(BizAlertRecord bizAlertRecord)
    {
        Set<Long> targetUserIds = new LinkedHashSet<>();
        targetUserIds.addAll(bizElderlyFamilyMapper.selectFamilyUserIdsByElderlyId(bizAlertRecord.getElderlyId()));
        targetUserIds.addAll(bizMessageNoticeMapper.selectUserIdsByRoleKeys(Arrays.asList("smartcare_community", "smartcare_admin")));
        for (Long userId : targetUserIds)
        {
            BizMessageNotice notice = buildBaseNotice(userId, bizAlertRecord.getElderlyId(), bizAlertRecord.getElderlyName());
            notice.setBizKey("ALERT_CREATE_" + bizAlertRecord.getAlertId() + "_" + userId);
            notice.setMessageType("alert");
            notice.setMessageLevel(String.valueOf(bizAlertRecord.getAlertLevel()));
            notice.setAlertId(bizAlertRecord.getAlertId());
            notice.setMessageTitle(buildAlertTitle(bizAlertRecord));
            notice.setMessageContent(bizAlertRecord.getAlertDesc());
            notice.setRelatedRoute("/smartcare/alertRecord?alertId=" + bizAlertRecord.getAlertId());
            bizMessageNoticeMapper.insertIgnoreBizMessageNotice(notice);
        }
    }

    @Override
    public void notifyWorkOrderCreated(BizWorkOrder bizWorkOrder)
    {
        List<Long> userIds = bizMessageNoticeMapper.selectUserIdsByRoleKeys(Arrays.asList("smartcare_community", "smartcare_admin"));
        for (Long userId : userIds)
        {
            BizMessageNotice notice = buildBaseNotice(userId, bizWorkOrder.getElderlyId(), bizWorkOrder.getElderlyName());
            notice.setBizKey("ORDER_CREATE_" + bizWorkOrder.getOrderId() + "_" + userId);
            notice.setMessageType("workOrder");
            notice.setMessageLevel("1");
            notice.setAlertId(bizWorkOrder.getAlertId());
            notice.setOrderId(bizWorkOrder.getOrderId());
            notice.setMessageTitle("新处置工单：" + bizWorkOrder.getElderlyName());
            notice.setMessageContent("系统已自动生成处置工单，请及时处理。");
            notice.setRelatedRoute("/smartcare/workOrder?orderId=" + bizWorkOrder.getOrderId());
            bizMessageNoticeMapper.insertIgnoreBizMessageNotice(notice);
        }
    }

    @Override
    public void notifyWorkOrderStatusChanged(BizWorkOrder bizWorkOrder)
    {
        Set<Long> targetUserIds = new LinkedHashSet<>();
        targetUserIds.addAll(bizMessageNoticeMapper.selectUserIdsByRoleKeys(Arrays.asList("smartcare_community", "smartcare_admin")));
        targetUserIds.addAll(bizElderlyFamilyMapper.selectFamilyUserIdsByElderlyId(bizWorkOrder.getElderlyId()));
        for (Long userId : targetUserIds)
        {
            BizMessageNotice notice = buildBaseNotice(userId, bizWorkOrder.getElderlyId(), bizWorkOrder.getElderlyName());
            notice.setBizKey("ORDER_STATUS_" + bizWorkOrder.getOrderId() + "_" + bizWorkOrder.getStatus() + "_" + userId);
            notice.setMessageType("workOrder");
            notice.setMessageLevel("1");
            notice.setAlertId(bizWorkOrder.getAlertId());
            notice.setOrderId(bizWorkOrder.getOrderId());
            notice.setMessageTitle("工单状态更新：" + bizWorkOrder.getElderlyName());
            notice.setMessageContent("当前处置状态：" + convertStatus(bizWorkOrder.getStatus()));
            notice.setRelatedRoute("/smartcare/workOrder?orderId=" + bizWorkOrder.getOrderId());
            bizMessageNoticeMapper.insertIgnoreBizMessageNotice(notice);
        }
    }

    private BizMessageNotice buildBaseNotice(Long userId, Long elderlyId, String elderlyName)
    {
        BizMessageNotice notice = new BizMessageNotice();
        notice.setUserId(userId);
        notice.setElderlyId(elderlyId);
        notice.setElderlyName(elderlyName);
        notice.setReadStatus("0");
        notice.setCreateBy("system");
        notice.setCreateTime(new Date());
        return notice;
    }

    private String buildAlertTitle(BizAlertRecord bizAlertRecord)
    {
        if (bizAlertRecord.getAlertLevel() != null && bizAlertRecord.getAlertLevel() >= 2)
        {
            return "重要预警：" + bizAlertRecord.getElderlyName() + " " + bizAlertRecord.getAlertType();
        }
        return "关注预警：" + bizAlertRecord.getElderlyName() + " " + bizAlertRecord.getAlertType();
    }

    private String convertStatus(String status)
    {
        if ("1".equals(status))
        {
            return "处理中";
        }
        if ("2".equals(status))
        {
            return "已结办";
        }
        return "待接单";
    }
}
