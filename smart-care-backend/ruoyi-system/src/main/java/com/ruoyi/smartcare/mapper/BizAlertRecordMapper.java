package com.ruoyi.smartcare.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.smartcare.domain.BizAlertRecord;

/**
 * 异常预警记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface BizAlertRecordMapper 
{
    /**
     * 查询异常预警记录
     * 
     * @param alertId 异常预警记录主键
     * @return 异常预警记录
     */
    public BizAlertRecord selectBizAlertRecordByAlertId(Long alertId);

    /**
     * 查询异常预警记录列表
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 异常预警记录集合
     */
    public List<BizAlertRecord> selectBizAlertRecordList(BizAlertRecord bizAlertRecord);

    /**
     * 新增异常预警记录
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 结果
     */
    public int insertBizAlertRecord(BizAlertRecord bizAlertRecord);

    /**
     * 修改异常预警记录
     * 
     * @param bizAlertRecord 异常预警记录
     * @return 结果
     */
    public int updateBizAlertRecord(BizAlertRecord bizAlertRecord);

    /**
     * 删除异常预警记录
     * 
     * @param alertId 异常预警记录主键
     * @return 结果
     */
    public int deleteBizAlertRecordByAlertId(Long alertId);

    /**
     * 批量删除异常预警记录
     * 
     * @param alertIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizAlertRecordByAlertIds(Long[] alertIds);

    BizAlertRecord selectMedicationMissedAlert(@Param("elderlyId") Long elderlyId,
                                               @Param("alertType") String alertType,
                                               @Param("remark") String remark);
}
