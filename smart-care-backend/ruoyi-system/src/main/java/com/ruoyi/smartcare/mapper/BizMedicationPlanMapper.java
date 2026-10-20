package com.ruoyi.smartcare.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.smartcare.domain.BizMedicationPlan;

public interface BizMedicationPlanMapper
{
    BizMedicationPlan selectBizMedicationPlanByPlanId(Long planId);

    List<BizMedicationPlan> selectBizMedicationPlanList(BizMedicationPlan bizMedicationPlan);

    int insertBizMedicationPlan(BizMedicationPlan bizMedicationPlan);

    int updateBizMedicationPlan(BizMedicationPlan bizMedicationPlan);

    int deleteBizMedicationPlanByPlanId(Long planId);

    int deleteBizMedicationPlanByPlanIds(Long[] planIds);

    List<BizMedicationPlan> selectActivePlansByElderlyAndDate(@Param("elderlyId") Long elderlyId, @Param("scheduledDate") Date scheduledDate);
}
