package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.BizAlertRule;

/**
 * 预警规则配置Service接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface IBizAlertRuleService 
{
    /**
     * 查询预警规则配置
     * 
     * @param ruleId 预警规则配置主键
     * @return 预警规则配置
     */
    public BizAlertRule selectBizAlertRuleByRuleId(Long ruleId);

    /**
     * 查询预警规则配置列表
     * 
     * @param bizAlertRule 预警规则配置
     * @return 预警规则配置集合
     */
    public List<BizAlertRule> selectBizAlertRuleList(BizAlertRule bizAlertRule);

    /**
     * 新增预警规则配置
     * 
     * @param bizAlertRule 预警规则配置
     * @return 结果
     */
    public int insertBizAlertRule(BizAlertRule bizAlertRule);

    /**
     * 修改预警规则配置
     * 
     * @param bizAlertRule 预警规则配置
     * @return 结果
     */
    public int updateBizAlertRule(BizAlertRule bizAlertRule);

    /**
     * 批量删除预警规则配置
     * 
     * @param ruleIds 需要删除的预警规则配置主键集合
     * @return 结果
     */
    public int deleteBizAlertRuleByRuleIds(Long[] ruleIds);

    /**
     * 删除预警规则配置信息
     * 
     * @param ruleId 预警规则配置主键
     * @return 结果
     */
    public int deleteBizAlertRuleByRuleId(Long ruleId);
}
