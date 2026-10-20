package com.ruoyi.smartcare.mapper;

import java.util.List;
import com.ruoyi.smartcare.domain.BizElderly;

/**
 * 老人档案Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface BizElderlyMapper 
{
    /**
     * 查询老人档案
     * 
     * @param elderlyId 老人档案主键
     * @return 老人档案
     */
    public BizElderly selectBizElderlyByElderlyId(Long elderlyId);

    /**
     * 查询老人档案列表
     * 
     * @param bizElderly 老人档案
     * @return 老人档案集合
     */
    public List<BizElderly> selectBizElderlyList(BizElderly bizElderly);

    /**
     * 新增老人档案
     * 
     * @param bizElderly 老人档案
     * @return 结果
     */
    public int insertBizElderly(BizElderly bizElderly);

    /**
     * 修改老人档案
     * 
     * @param bizElderly 老人档案
     * @return 结果
     */
    public int updateBizElderly(BizElderly bizElderly);

    /**
     * 删除老人档案
     * 
     * @param elderlyId 老人档案主键
     * @return 结果
     */
    public int deleteBizElderlyByElderlyId(Long elderlyId);

    /**
     * 批量删除老人档案
     * 
     * @param elderlyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizElderlyByElderlyIds(Long[] elderlyIds);
}
