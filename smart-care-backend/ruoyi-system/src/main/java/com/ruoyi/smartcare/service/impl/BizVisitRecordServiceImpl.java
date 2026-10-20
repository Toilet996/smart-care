package com.ruoyi.smartcare.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizVisitRecordMapper;
import com.ruoyi.smartcare.domain.BizVisitRecord;
import com.ruoyi.smartcare.service.IBizVisitRecordService;

/**
 * 日常随访记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizVisitRecordServiceImpl implements IBizVisitRecordService 
{
    @Autowired
    private BizVisitRecordMapper bizVisitRecordMapper;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    /**
     * 查询日常随访记录
     * 
     * @param recordId 日常随访记录主键
     * @return 日常随访记录
     */
    @Override
    public BizVisitRecord selectBizVisitRecordByRecordId(Long recordId)
    {
        BizVisitRecord record = bizVisitRecordMapper.selectBizVisitRecordByRecordId(recordId);
        if (record != null)
        {
            familyAccessService.assertCanAccessElderly(record.getElderlyId());
        }
        return record;
    }

    /**
     * 查询日常随访记录列表
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 日常随访记录
     */
    @Override
    public List<BizVisitRecord> selectBizVisitRecordList(BizVisitRecord bizVisitRecord)
    {
        familyAccessService.applyFamilyScope(bizVisitRecord);
        return bizVisitRecordMapper.selectBizVisitRecordList(bizVisitRecord);
    }

    /**
     * 新增日常随访记录
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 结果
     */
    @Override
    public int insertBizVisitRecord(BizVisitRecord bizVisitRecord)
    {
        return bizVisitRecordMapper.insertBizVisitRecord(bizVisitRecord);
    }

    /**
     * 修改日常随访记录
     * 
     * @param bizVisitRecord 日常随访记录
     * @return 结果
     */
    @Override
    public int updateBizVisitRecord(BizVisitRecord bizVisitRecord)
    {
        return bizVisitRecordMapper.updateBizVisitRecord(bizVisitRecord);
    }

    /**
     * 批量删除日常随访记录
     * 
     * @param recordIds 需要删除的日常随访记录主键
     * @return 结果
     */
    @Override
    public int deleteBizVisitRecordByRecordIds(Long[] recordIds)
    {
        return bizVisitRecordMapper.deleteBizVisitRecordByRecordIds(recordIds);
    }

    /**
     * 删除日常随访记录信息
     * 
     * @param recordId 日常随访记录主键
     * @return 结果
     */
    @Override
    public int deleteBizVisitRecordByRecordId(Long recordId)
    {
        return bizVisitRecordMapper.deleteBizVisitRecordByRecordId(recordId);
    }
}
