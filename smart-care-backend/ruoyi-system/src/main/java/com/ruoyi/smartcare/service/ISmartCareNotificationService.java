package com.ruoyi.smartcare.service;

import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizWorkOrder;

public interface ISmartCareNotificationService
{
    void notifyAlertCreated(BizAlertRecord bizAlertRecord);

    void notifyWorkOrderCreated(BizWorkOrder bizWorkOrder);

    void notifyWorkOrderStatusChanged(BizWorkOrder bizWorkOrder);
}
