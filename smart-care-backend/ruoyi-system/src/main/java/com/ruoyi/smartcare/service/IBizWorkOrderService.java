package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.BizWorkOrder;

/**
 * 处置工单Service接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface IBizWorkOrderService 
{
    /**
     * 查询处置工单
     * 
     * @param orderId 处置工单主键
     * @return 处置工单
     */
    public BizWorkOrder selectBizWorkOrderByOrderId(Long orderId);

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
     * 批量删除处置工单
     * 
     * @param orderIds 需要删除的处置工单主键集合
     * @return 结果
     */
    public int deleteBizWorkOrderByOrderIds(Long[] orderIds);

    /**
     * 删除处置工单信息
     * 
     * @param orderId 处置工单主键
     * @return 结果
     */
    public int deleteBizWorkOrderByOrderId(Long orderId);
}
