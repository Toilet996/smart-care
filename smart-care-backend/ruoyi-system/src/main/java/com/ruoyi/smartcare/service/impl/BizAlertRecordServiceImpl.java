package com.ruoyi.smartcare.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizAlertRecordMapper;
import com.ruoyi.smartcare.mapper.BizWorkOrderMapper;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizWorkOrder;
import com.ruoyi.smartcare.service.IBizAlertRecordService;
import com.ruoyi.smartcare.service.ISmartCareNotificationService;

/**
 * 异常预警记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizAlertRecordServiceImpl implements IBizAlertRecordService 
{
    @Autowired
    private BizAlertRecordMapper bizAlertRecordMapper;

    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    @Autowired
    private ISmartCareNotificationService smartCareNotificationService;

    /**
     * 查询异常预警记录
     * 
     * @param alertId 异常预警记录主键
     * @return 异常预警记录
     */
    @Override
    public BizAlertRecord selectBizAlertRecordByAlertId(Long alertId)
    {
        BizAlertRecord alertRecord = bizAlertRecordMapper.selectBizAlertRecordByAlertId(alertId);
        if (alertRecord != null)
        {
            familyAccessService.assertCanAccessElderly(alertRecord.getElderlyId());
        }
        return alertRecord;
    }

    /**
     * 查询异常预警记录列表
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 异常预警记录
     */
    @Override
    public List<BizAlertRecord> selectBizAlertRecordList(BizAlertRecord bizAlertRecord)
    {
        familyAccessService.applyFamilyScope(bizAlertRecord);
        return bizAlertRecordMapper.selectBizAlertRecordList(bizAlertRecord);
    }

    /**
     * 新增异常预警记录
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 结果
     */
    @Override
    public int insertBizAlertRecord(BizAlertRecord bizAlertRecord)
    {
        bizAlertRecord.setCreateTime(DateUtils.getNowDate());
        int rows = bizAlertRecordMapper.insertBizAlertRecord(bizAlertRecord);
        if (rows > 0)
        {
            smartCareNotificationService.notifyAlertCreated(bizAlertRecord);
            createWorkOrderIfAbsent(bizAlertRecord);
        }
        return rows;
    }

    /**
     * 修改异常预警记录
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 结果
     */
    @Override
    public int updateBizAlertRecord(BizAlertRecord bizAlertRecord)
    {
        return bizAlertRecordMapper.updateBizAlertRecord(bizAlertRecord);
    }

    @Override
    public int updateHandleStatus(Long alertId, String handleStatus)
    {
        if (alertId == null || handleStatus == null || handleStatus.isEmpty())
        {
            return 0;
        }

        BizAlertRecord alertRecord = bizAlertRecordMapper.selectBizAlertRecordByAlertId(alertId);
        if (alertRecord == null)
        {
            return 0;
        }

        alertRecord.setHandleStatus(handleStatus);
        return bizAlertRecordMapper.updateBizAlertRecord(alertRecord);
    }

    /**
     * 批量删除异常预警记录
     * 
     * @param alertIds 需要删除的异常预警记录主键
     * @return 结果
     */
    @Override
    public int deleteBizAlertRecordByAlertIds(Long[] alertIds)
    {
        return bizAlertRecordMapper.deleteBizAlertRecordByAlertIds(alertIds);
    }

    /**
     * 删除异常预警记录信息
     * 
     * @param alertId 异常预警记录主键
     * @return 结果
     */
    @Override
    public int deleteBizAlertRecordByAlertId(Long alertId)
    {
        return bizAlertRecordMapper.deleteBizAlertRecordByAlertId(alertId);
    }

    private void createWorkOrderIfAbsent(BizAlertRecord bizAlertRecord)
    {
        if (bizAlertRecord.getAlertId() == null)
        {
            return;
        }

        BizWorkOrder existing = bizWorkOrderMapper.selectBizWorkOrderByAlertId(bizAlertRecord.getAlertId());
        if (existing != null)
        {
            return;
        }

        BizWorkOrder workOrder = new BizWorkOrder();
        workOrder.setAlertId(bizAlertRecord.getAlertId());
        workOrder.setElderlyId(bizAlertRecord.getElderlyId());
        workOrder.setElderlyName(bizAlertRecord.getElderlyName());
        workOrder.setProcessRecord(buildDefaultProcessRecord(bizAlertRecord));
        workOrder.setStatus("0");
        workOrder.setCreateTime(DateUtils.getNowDate());
        bizWorkOrderMapper.insertBizWorkOrder(workOrder);
        smartCareNotificationService.notifyWorkOrderCreated(workOrder);
    }

    private String buildDefaultProcessRecord(BizAlertRecord bizAlertRecord)
    {
        String alertType = bizAlertRecord.getAlertType() == null ? "未知类型" : bizAlertRecord.getAlertType();
        String alertDesc = bizAlertRecord.getAlertDesc() == null ? "待处置" : bizAlertRecord.getAlertDesc();
        return "系统自动派单，预警类型：" + alertType + "，预警描述：" + alertDesc;
    }
}
