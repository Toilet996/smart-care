package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.BizMedicationRecord;
import com.ruoyi.smartcare.domain.IotSensorLog;

public interface IBizMedicationRecordService
{
    BizMedicationRecord selectBizMedicationRecordByRecordId(Long recordId);

    List<BizMedicationRecord> selectBizMedicationRecordList(BizMedicationRecord bizMedicationRecord);

    int insertBizMedicationRecord(BizMedicationRecord bizMedicationRecord);

    int updateBizMedicationRecord(BizMedicationRecord bizMedicationRecord);

    int deleteBizMedicationRecordByRecordIds(Long[] recordIds);

    int deleteBizMedicationRecordByRecordId(Long recordId);

    void syncBySensorLog(IotSensorLog sensorLog);
}
