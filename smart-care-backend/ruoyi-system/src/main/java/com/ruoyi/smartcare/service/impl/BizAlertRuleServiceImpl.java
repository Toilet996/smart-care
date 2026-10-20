package com.ruoyi.smartcare.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizAlertRuleMapper;
import com.ruoyi.smartcare.domain.BizAlertRule;
import com.ruoyi.smartcare.service.IBizAlertRuleService;

/**
 * 预警规则配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizAlertRuleServiceImpl implements IBizAlertRuleService 
{
    @Autowired
    private BizAlertRuleMapper bizAlertRuleMapper;

    /**
     * 查询预警规则配置
     * 
     * @param ruleId 预警规则配置主键
     * @return 预警规则配置
     */
    @Override
    public BizAlertRule selectBizAlertRuleByRuleId(Long ruleId)
    {
        return bizAlertRuleMapper.selectBizAlertRuleByRuleId(ruleId);
    }

    /**
     * 查询预警规则配置列表
     * 
     * @param bizAlertRule 预警规则配置
     * @return 预警规则配置
     */
    @Override
    public List<BizAlertRule> selectBizAlertRuleList(BizAlertRule bizAlertRule)
    {
        return bizAlertRuleMapper.selectBizAlertRuleList(bizAlertRule);
    }

    /**
     * 新增预警规则配置
     * 
     * @param bizAlertRule 预警规则配置
     * @return 结果
     */
    @Override
    public int insertBizAlertRule(BizAlertRule bizAlertRule)
    {
        bizAlertRule.setCreateTime(DateUtils.getNowDate());
        return bizAlertRuleMapper.insertBizAlertRule(bizAlertRule);
    }

    /**
     * 修改预警规则配置
     * 
     * @param bizAlertRule 预警规则配置
     * @return 结果
     */
    @Override
    public int updateBizAlertRule(BizAlertRule bizAlertRule)
    {
        return bizAlertRuleMapper.updateBizAlertRule(bizAlertRule);
    }

    /**
     * 批量删除预警规则配置
     * 
     * @param ruleIds 需要删除的预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteBizAlertRuleByRuleIds(Long[] ruleIds)
    {
        return bizAlertRuleMapper.deleteBizAlertRuleByRuleIds(ruleIds);
    }

    /**
     * 删除预警规则配置信息
     * 
     * @param ruleId 预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteBizAlertRuleByRuleId(Long ruleId)
    {
        return bizAlertRuleMapper.deleteBizAlertRuleByRuleId(ruleId);
    }
}
