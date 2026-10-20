package com.ruoyi.smartcare.mapper;

import java.util.List;
import com.ruoyi.smartcare.domain.BizWorkOrder;

/**
 * 处置工单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface BizWorkOrderMapper 
{
    /**
     * 查询处置工单
     * 
     * @param orderId 处置工单主键
     * @return 处置工单
     */
    public BizWorkOrder selectBizWorkOrderByOrderId(Long orderId);

    /**
     * 根据预警ID查询工单
     *
     * @param alertId 预警ID
     * @return 工单
     */
    public BizWorkOrder selectBizWorkOrderByAlertId(Long alertId);

    /**
     * 查询处置工单列表
     * 
     * @param bizWorkOrder 处置工单
     * @return 处置工单集合
     */
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder);

    /**
     * 新增处置工单
     * 
     * @param bizWorkOrder 处置工单
     * @return 结果
     */
    public int insertBizWorkOrder(BizWorkOrder bizWorkOrder);

    /**
     * 修改处置工单
     * 
     * @param bizWorkOrder 处置工单
     * @return 结果
     */
    public int updateBizWorkOrder(BizWorkOrder bizWorkOrder);

    /**
     * 删除处置工单
     * 
     * @param orderId 处置工单主键
     * @return 结果
     */
    public int deleteBizWorkOrderByOrderId(Long orderId);

    /**
     * 批量删除处置工单
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizWorkOrderByOrderIds(Long[] orderIds);
}
