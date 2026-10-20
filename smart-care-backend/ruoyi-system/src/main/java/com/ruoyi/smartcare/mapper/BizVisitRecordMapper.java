package com.ruoyi.smartcare.mapper;

import java.util.List;
import com.ruoyi.smartcare.domain.BizVisitRecord;

/**
 * 日常随访记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface BizVisitRecordMapper 
{
    /**
     * 查询日常随访记录
     * 
     * @param recordId 日常随访记录主键
     * @return 日常随访记录
     */
    public BizVisitRecord selectBizVisitRecordByRecordId(Long recordId);

    /**
     * 查询日常随访记录列表
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 日常随访记录集合
     */
    public List<BizVisitRecord> selectBizVisitRecordList(BizVisitRecord bizVisitRecord);

    /**
     * 新增日常随访记录
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 结果
     */
    public int insertBizVisitRecord(BizVisitRecord bizVisitRecord);

    /**
     * 修改日常随访记录
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 结果
     */
    public int updateBizVisitRecord(BizVisitRecord bizVisitRecord);

    /**
     * 删除日常随访记录
     * 
     * @param recordId 日常随访记录主键
     * @return 结果
     */
    public int deleteBizVisitRecordByRecordId(Long recordId);

    /**
     * 批量删除日常随访记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizVisitRecordByRecordIds(Long[] recordIds);
}
