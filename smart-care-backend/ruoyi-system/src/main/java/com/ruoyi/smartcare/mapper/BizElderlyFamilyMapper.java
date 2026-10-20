package com.ruoyi.smartcare.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.ruoyi.smartcare.domain.BizElderlyFamily;

/**
 * 家属关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface BizElderlyFamilyMapper 
{
    /**
     * 查询家属关联
     * 
     * @param id 家属关联主键
     * @return 家属关联
     */
    public BizElderlyFamily selectBizElderlyFamilyById(Long id);

    /**
     * 查询家属关联列表
     * 
     * @param bizElderlyFamily 家属关联
     * @return 家属关联集合
     */
    public List<BizElderlyFamily> selectBizElderlyFamilyList(BizElderlyFamily bizElderlyFamily);

    /**
     * 新增家属关联
     * 
     * @param bizElderlyFamily 家属关联
     * @return 结果
     */
    public int insertBizElderlyFamily(BizElderlyFamily bizElderlyFamily);

    /**
     * 修改家属关联
     * 
     * @param bizElderlyFamily 家属关联
     * @return 结果
     */
    public int updateBizElderlyFamily(BizElderlyFamily bizElderlyFamily);

    /**
     * 删除家属关联
     * 
     * @param id 家属关联主键
     * @return 结果
     */
    public int deleteBizElderlyFamilyById(Long id);

    /**
     * 批量删除家属关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizElderlyFamilyByIds(Long[] ids);

    public Long countLinkedElderlyByUserId(@Param("userId") Long userId, @Param("elderlyId") Long elderlyId);

    public List<Map<String, Object>> selectFamilyUserOptions();

    public List<Map<String, Object>> selectElderlyOptions();

    public List<Long> selectFamilyUserIdsByElderlyId(Long elderlyId);
}
