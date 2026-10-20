package com.ruoyi.smartcare.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.domain.BizMedicationPlan;
import com.ruoyi.smartcare.mapper.BizMedicationPlanMapper;
import com.ruoyi.smartcare.service.IBizMedicationPlanService;

@Service
public class BizMedicationPlanServiceImpl implements IBizMedicationPlanService
{
    @Autowired
    private BizMedicationPlanMapper bizMedicationPlanMapper;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    @Override
    public BizMedicationPlan selectBizMedicationPlanByPlanId(Long planId)
    {
        BizMedicationPlan plan = bizMedicationPlanMapper.selectBizMedicationPlanByPlanId(planId);
        if (plan != null)
        {
            familyAccessService.assertCanAccessElderly(plan.getElderlyId());
        }
        return plan;
    }

    @Override
    public List<BizMedicationPlan> selectBizMedicationPlanList(BizMedicationPlan bizMedicationPlan)
    {
        familyAccessService.applyFamilyScope(bizMedicationPlan);
        return bizMedicationPlanMapper.selectBizMedicationPlanList(bizMedicationPlan);
    }

    @Override
    public int insertBizMedicationPlan(BizMedicationPlan bizMedicationPlan)
    {
        return bizMedicationPlanMapper.insertBizMedicationPlan(bizMedicationPlan);
    }

    @Override
    public int updateBizMedicationPlan(BizMedicationPlan bizMedicationPlan)
    {
        return bizMedicationPlanMapper.updateBizMedicationPlan(bizMedicationPlan);
    }

    @Override
    public int deleteBizMedicationPlanByPlanIds(Long[] planIds)
    {
        return bizMedicationPlanMapper.deleteBizMedicationPlanByPlanIds(planIds);
    }

    @Override
    public int deleteBizMedicationPlanByPlanId(Long planId)
    {
        return bizMedicationPlanMapper.deleteBizMedicationPlanByPlanId(planId);
    }
}
