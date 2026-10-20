package com.ruoyi.smartcare.mapper;

import java.util.Date;
import java.util.List;
import com.ruoyi.smartcare.domain.BizMedicationRecord;
import org.apache.ibatis.annotations.Param;

public interface BizMedicationRecordMapper
{
    BizMedicationRecord selectBizMedicationRecordByRecordId(Long recordId);

    List<BizMedicationRecord> selectBizMedicationRecordList(BizMedicationRecord bizMedicationRecord);

    int insertBizMedicationRecord(BizMedicationRecord bizMedicationRecord);

    int updateBizMedicationRecord(BizMedicationRecord bizMedicationRecord);

    int deleteBizMedicationRecordByRecordId(Long recordId);

    int deleteBizMedicationRecordByRecordIds(Long[] recordIds);

    BizMedicationRecord selectByPlanAndDate(@Param("planId") Long planId, @Param("scheduledDate") Date scheduledDate);
}
