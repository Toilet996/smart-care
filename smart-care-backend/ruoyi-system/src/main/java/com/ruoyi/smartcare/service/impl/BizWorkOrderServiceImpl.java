package com.ruoyi.smartcare.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizWorkOrderMapper;
import com.ruoyi.smartcare.domain.BizWorkOrder;
import com.ruoyi.smartcare.service.IBizAlertRecordService;
import com.ruoyi.smartcare.service.IBizWorkOrderService;
import com.ruoyi.smartcare.service.ISmartCareNotificationService;

/**
 * 处置工单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizWorkOrderServiceImpl implements IBizWorkOrderService 
{
    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private IBizAlertRecordService bizAlertRecordService;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    @Autowired
    private ISmartCareNotificationService smartCareNotificationService;

    /**
     * 查询处置工单
     * 
     * @param orderId 处置工单主键
     * @return 处置工单
     */
    @Override
    public BizWorkOrder selectBizWorkOrderByOrderId(Long orderId)
    {
        BizWorkOrder workOrder = bizWorkOrderMapper.selectBizWorkOrderByOrderId(orderId);
        if (workOrder != null)
        {
            familyAccessService.assertCanAccessElderly(workOrder.getElderlyId());
        }
        return workOrder;
    }

    /**
     * 查询处置工单列表
     * 
     * @param bizWorkOrder 处置工单
     * @return 处置工单
     */
    @Override
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder)
    {
        familyAccessService.applyFamilyScope(bizWorkOrder);
        return bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
    }

    /**
     * 新增处置工单
     * 
     * @param bizWorkOrder 处置工单
     * @return 结果
     */
    @Override
    public int insertBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        bizWorkOrder.setCreateTime(DateUtils.getNowDate());
        int rows = bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
        if (rows > 0)
        {
            smartCareNotificationService.notifyWorkOrderCreated(bizWorkOrder);
        }
        return rows;
    }

    /**
     * 修改处置工单
     * 
     * @param bizWorkOrder 处置工单
     * @return 结果
     */
    @Override
    public int updateBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        int rows = bizWorkOrderMapper.updateBizWorkOrder(bizWorkOrder);
        if (rows > 0)
        {
            syncAlertHandleStatus(bizWorkOrder);
            smartCareNotificationService.notifyWorkOrderStatusChanged(bizWorkOrder);
        }
        return rows;
    }

    /**
     * 批量删除处置工单
     * 
     * @param orderIds 需要删除的处置工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderByOrderIds(Long[] orderIds)
    {
        return bizWorkOrderMapper.deleteBizWorkOrderByOrderIds(orderIds);
    }

    /**
     * 删除处置工单信息
     * 
     * @param orderId 处置工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderByOrderId(Long orderId)
    {
        return bizWorkOrderMapper.deleteBizWorkOrderByOrderId(orderId);
    }

    private void syncAlertHandleStatus(BizWorkOrder bizWorkOrder)
    {
        if (bizWorkOrder.getAlertId() == null)
        {
            return;
        }

        String handleStatus;
        switch (bizWorkOrder.getStatus())
        {
            case "0":
                handleStatus = "0";
                break;
            case "1":
                handleStatus = "1";
                break;
            case "2":
                handleStatus = "2";
                break;
            default:
                handleStatus = null;
                break;
        }

        if (handleStatus != null)
        {
            bizAlertRecordService.updateHandleStatus(bizWorkOrder.getAlertId(), handleStatus);
        }
    }
}
