package com.ruoyi.smartcare.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizElderlyFamilyMapper;
import com.ruoyi.smartcare.domain.BizElderlyFamily;
import com.ruoyi.smartcare.service.IBizElderlyFamilyService;

/**
 * 家属关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizElderlyFamilyServiceImpl implements IBizElderlyFamilyService 
{
    @Autowired
    private BizElderlyFamilyMapper bizElderlyFamilyMapper;

    /**
     * 查询家属关联
     * 
     * @param id 家属关联主键
     * @return 家属关联
     */
    @Override
    public BizElderlyFamily selectBizElderlyFamilyById(Long id)
    {
        return bizElderlyFamilyMapper.selectBizElderlyFamilyById(id);
    }

    /**
     * 查询家属关联列表
     * 
     * @param bizElderlyFamily 家属关联
     * @return 家属关联
     */
    @Override
    public List<BizElderlyFamily> selectBizElderlyFamilyList(BizElderlyFamily bizElderlyFamily)
    {
        return bizElderlyFamilyMapper.selectBizElderlyFamilyList(bizElderlyFamily);
    }

    /**
     * 新增家属关联
     * 
     * @param bizElderlyFamily 家属关联
     * @return 结果
     */
    @Override
    public int insertBizElderlyFamily(BizElderlyFamily bizElderlyFamily)
    {
        bizElderlyFamily.setCreateTime(DateUtils.getNowDate());
        return bizElderlyFamilyMapper.insertBizElderlyFamily(bizElderlyFamily);
    }

    /**
     * 修改家属关联
     * 
     * @param bizElderlyFamily 家属关联
     * @return 结果
     */
    @Override
    public int updateBizElderlyFamily(BizElderlyFamily bizElderlyFamily)
    {
        return bizElderlyFamilyMapper.updateBizElderlyFamily(bizElderlyFamily);
    }

    /**
     * 批量删除家属关联
     * 
     * @param ids 需要删除的家属关联主键
     * @return 结果
     */
    @Override
    public int deleteBizElderlyFamilyByIds(Long[] ids)
    {
        return bizElderlyFamilyMapper.deleteBizElderlyFamilyByIds(ids);
    }

    /**
     * 删除家属关联信息
     * 
     * @param id 家属关联主键
     * @return 结果
     */
    @Override
    public int deleteBizElderlyFamilyById(Long id)
    {
        return bizElderlyFamilyMapper.deleteBizElderlyFamilyById(id);
    }

    @Override
    public Map<String, Object> selectFamilyRelationOptions()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("familyUsers", bizElderlyFamilyMapper.selectFamilyUserOptions());
        result.put("elderlyList", bizElderlyFamilyMapper.selectElderlyOptions());
        return result;
    }
}
