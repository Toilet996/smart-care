package com.ruoyi.smartcare.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizElderlyMapper;
import com.ruoyi.smartcare.domain.BizElderly;
import com.ruoyi.smartcare.service.IBizElderlyService;

/**
 * 老人档案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizElderlyServiceImpl implements IBizElderlyService 
{
    @Autowired
    private BizElderlyMapper bizElderlyMapper;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    /**
     * 查询老人档案
     * 
     * @param elderlyId 老人档案主键
     * @return 老人档案
     */
    @Override
    public BizElderly selectBizElderlyByElderlyId(Long elderlyId)
    {
        familyAccessService.assertCanAccessElderly(elderlyId);
        return bizElderlyMapper.selectBizElderlyByElderlyId(elderlyId);
    }

    /**
     * 查询老人档案列表
     * 
     * @param bizElderly 老人档案
     * @return 老人档案
     */
    @Override
    public List<BizElderly> selectBizElderlyList(BizElderly bizElderly)
    {
        familyAccessService.applyFamilyScope(bizElderly);
        return bizElderlyMapper.selectBizElderlyList(bizElderly);
    }

    /**
     * 新增老人档案
     * 
     * @param bizElderly 老人档案
     * @return 结果
     */
    @Override
    public int insertBizElderly(BizElderly bizElderly)
    {
        bizElderly.setCreateTime(DateUtils.getNowDate());
        return bizElderlyMapper.insertBizElderly(bizElderly);
    }

    /**
     * 修改老人档案
     * 
     * @param bizElderly 老人档案
     * @return 结果
     */
    @Override
    public int updateBizElderly(BizElderly bizElderly)
    {
        return bizElderlyMapper.updateBizElderly(bizElderly);
    }

    /**
     * 批量删除老人档案
     * 
     * @param elderlyIds 需要删除的老人档案主键
     * @return 结果
     */
    @Override
    public int deleteBizElderlyByElderlyIds(Long[] elderlyIds)
    {
        return bizElderlyMapper.deleteBizElderlyByElderlyIds(elderlyIds);
    }

    /**
     * 删除老人档案信息
     * 
     * @param elderlyId 老人档案主键
     * @return 结果
     */
    @Override
    public int deleteBizElderlyByElderlyId(Long elderlyId)
    {
        return bizElderlyMapper.deleteBizElderlyByElderlyId(elderlyId);
    }
}
