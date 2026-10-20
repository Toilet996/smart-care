package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.BizMedicationPlan;

public interface IBizMedicationPlanService
{
    BizMedicationPlan selectBizMedicationPlanByPlanId(Long planId);

    List<BizMedicationPlan> selectBizMedicationPlanList(BizMedicationPlan bizMedicationPlan);

    int insertBizMedicationPlan(BizMedicationPlan bizMedicationPlan);

    int updateBizMedicationPlan(BizMedicationPlan bizMedicationPlan);

    int deleteBizMedicationPlanByPlanIds(Long[] planIds);

    int deleteBizMedicationPlanByPlanId(Long planId);
}
