package com.ruoyi.smartcare.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.smartcare.domain.BizAlertRecord;
import com.ruoyi.smartcare.domain.BizElderlyFamily;
import com.ruoyi.smartcare.domain.FamilyPortalContactRequest;
import com.ruoyi.smartcare.domain.FamilyPortalSosRequest;
import com.ruoyi.smartcare.mapper.BizAlertRecordMapper;
import com.ruoyi.smartcare.mapper.BizElderlyFamilyMapper;
import com.ruoyi.smartcare.mapper.FamilyPortalMapper;
import com.ruoyi.smartcare.service.IBizAlertRecordService;
import com.ruoyi.smartcare.service.IFamilyPortalService;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 家属门户 Service 实现
 */
@Service
public class FamilyPortalServiceImpl implements IFamilyPortalService
{
    @Autowired
    private FamilyPortalMapper familyPortalMapper;

    @Autowired
    private BizElderlyFamilyMapper bizElderlyFamilyMapper;

    @Autowired
    private IBizAlertRecordService bizAlertRecordService;

    @Autowired
    private BizAlertRecordMapper bizAlertRecordMapper;

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public Map<String, Object> getPortalData(Long requestedUserId, Long elderlyId)
    {
        Long effectiveUserId = resolveUserId(requestedUserId);
        List<Map<String, Object>> elderlyList = familyPortalMapper.selectLinkedElderlyList(effectiveUserId);
        Long focusElderlyId = resolveFocusElderlyId(elderlyId, elderlyList);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("effectiveUserId", effectiveUserId);
        result.put("familyProfile", familyPortalMapper.selectFamilyProfile(effectiveUserId));
        result.put("coreStats", familyPortalMapper.selectCoreStats(effectiveUserId));
        result.put("elderlyList", elderlyList);
        result.put("recentAlerts", familyPortalMapper.selectRecentAlerts(effectiveUserId));
        result.put("latestReports", familyPortalMapper.selectLatestReports(effectiveUserId));
        result.put("emergencyContacts", familyPortalMapper.selectEmergencyContacts(effectiveUserId));
        result.put("recentSosAlerts", familyPortalMapper.selectRecentSosAlerts(effectiveUserId));
        result.put("serviceHotlines", buildServiceHotlines());
        result.put("focusElderlyId", focusElderlyId);
        result.put("vitalsTrend", focusElderlyId == null ? null : familyPortalMapper.selectVitalsTrend(focusElderlyId));
        result.put("todayMedicationSummary", focusElderlyId == null ? null : familyPortalMapper.selectTodayMedicationSummary(effectiveUserId, focusElderlyId));
        result.put("todayMedicationPlans", focusElderlyId == null ? null : familyPortalMapper.selectTodayMedicationPlans(effectiveUserId, focusElderlyId));
        return result;
    }

    @Override
    public List<BizElderlyFamily> listContacts(Long requestedUserId)
    {
        BizElderlyFamily query = new BizElderlyFamily();
        query.setUserId(resolveUserId(requestedUserId));
        return bizElderlyFamilyMapper.selectBizElderlyFamilyList(query);
    }

    @Override
    public int saveContact(Long requestedUserId, FamilyPortalContactRequest request)
    {
        Long effectiveUserId = resolveUserId(requestedUserId);
        Map<String, Object> elderly = requireLinkedElderly(effectiveUserId, request.getElderlyId());

        BizElderlyFamily entity = new BizElderlyFamily();
        entity.setUserId(effectiveUserId);
        entity.setElderlyId(request.getElderlyId());
        entity.setElderlyName(String.valueOf(elderly.get("elderlyName")));
        entity.setFamilyName(StringUtils.trim(request.getFamilyName()));
        entity.setFamilyPhone(StringUtils.trim(request.getFamilyPhone()));
        entity.setRelation(StringUtils.trim(request.getRelation()));
        entity.setIsEmergency(normalizeEmergencyFlag(request.getIsEmergency()));

        validateContact(entity);
        return bizElderlyFamilyMapper.insertBizElderlyFamily(entity);
    }

    @Override
    public int updateContact(Long requestedUserId, FamilyPortalContactRequest request)
    {
        Long effectiveUserId = resolveUserId(requestedUserId);
        BizElderlyFamily existing = requireOwnedContact(request.getId(), effectiveUserId);
        Map<String, Object> elderly = requireLinkedElderly(effectiveUserId, request.getElderlyId());

        existing.setElderlyId(request.getElderlyId());
        existing.setElderlyName(String.valueOf(elderly.get("elderlyName")));
        existing.setFamilyName(StringUtils.trim(request.getFamilyName()));
        existing.setFamilyPhone(StringUtils.trim(request.getFamilyPhone()));
        existing.setRelation(StringUtils.trim(request.getRelation()));
        existing.setIsEmergency(normalizeEmergencyFlag(request.getIsEmergency()));

        validateContact(existing);
        return bizElderlyFamilyMapper.updateBizElderlyFamily(existing);
    }

    @Override
    public int deleteContacts(Long requestedUserId, Long[] ids)
    {
        Long effectiveUserId = resolveUserId(requestedUserId);
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        for (Long id : ids)
        {
            requireOwnedContact(id, effectiveUserId);
        }
        return bizElderlyFamilyMapper.deleteBizElderlyFamilyByIds(ids);
    }

    @Override
    public Map<String, Object> triggerSos(Long requestedUserId, FamilyPortalSosRequest request)
    {
        Long effectiveUserId = resolveUserId(requestedUserId);
        Map<String, Object> elderly = requireLinkedElderly(effectiveUserId, request.getElderlyId());

        BizAlertRecord alertRecord = new BizAlertRecord();
        alertRecord.setElderlyId(request.getElderlyId());
        alertRecord.setElderlyName(String.valueOf(elderly.get("elderlyName")));
        alertRecord.setDeviceId("family_portal");
        alertRecord.setDeviceName("家属门户");
        alertRecord.setAlertLevel(2L);
        alertRecord.setAlertType("sos_help");
        alertRecord.setAlertDesc(buildSosDesc(request));
        alertRecord.setHandleStatus("0");
        alertRecord.setRemark(buildSosRemark(effectiveUserId, request));

        bizAlertRecordService.insertBizAlertRecord(alertRecord);

        BizAlertRecord saved = bizAlertRecordMapper.selectBizAlertRecordByAlertId(alertRecord.getAlertId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("alertId", alertRecord.getAlertId());
        result.put("orderId", saved == null ? null : saved.getOrderId());
        result.put("elderlyId", alertRecord.getElderlyId());
        result.put("elderlyName", alertRecord.getElderlyName());
        result.put("message", "SOS 已提交，系统已生成紧急预警并自动派发处置工单。");
        return result;
    }

    private void validateContact(BizElderlyFamily entity)
    {
        if (entity.getElderlyId() == null)
        {
            throw new ServiceException("请选择关联老人");
        }
        if (StringUtils.isBlank(entity.getFamilyName()))
        {
            throw new ServiceException("请输入联系人姓名");
        }
        if (StringUtils.isBlank(entity.getFamilyPhone()))
        {
            throw new ServiceException("请输入联系人电话");
        }
        if (StringUtils.isBlank(entity.getRelation()))
        {
            throw new ServiceException("请选择亲属关系");
        }
    }

    private BizElderlyFamily requireOwnedContact(Long id, Long effectiveUserId)
    {
        if (id == null)
        {
            throw new ServiceException("缺少联系人ID");
        }
        BizElderlyFamily family = bizElderlyFamilyMapper.selectBizElderlyFamilyById(id);
        if (family == null || family.getUserId() == null || !family.getUserId().equals(effectiveUserId))
        {
            throw new ServiceException("当前联系人不存在或无权操作");
        }
        return family;
    }

    private Map<String, Object> requireLinkedElderly(Long effectiveUserId, Long elderlyId)
    {
        if (elderlyId == null)
        {
            throw new ServiceException("请选择老人");
        }
        Map<String, Object> elderly = familyPortalMapper.selectLinkedElderlyBasic(effectiveUserId, elderlyId);
        if (elderly == null || elderly.isEmpty())
        {
            throw new ServiceException("当前老人未绑定到该家属账号");
        }
        return elderly;
    }

    private Map<String, Object> buildServiceHotlines()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("communityHotline", defaultIfBlank(sysConfigService.selectConfigByKey("smartcare.community.hotline"), "12349"));
        result.put("medicalHotline", defaultIfBlank(sysConfigService.selectConfigByKey("smartcare.medical.hotline"), "120"));
        result.put("policeHotline", defaultIfBlank(sysConfigService.selectConfigByKey("smartcare.police.hotline"), "110"));
        return result;
    }

    private String buildSosDesc(FamilyPortalSosRequest request)
    {
        StringBuilder builder = new StringBuilder("家属门户发起一键 SOS");
        if (StringUtils.isNotBlank(request.getDescription()))
        {
            builder.append("，情况说明：").append(request.getDescription().trim());
        }
        if (StringUtils.isNotBlank(request.getLocationHint()))
        {
            builder.append("，位置提示：").append(request.getLocationHint().trim());
        }
        return builder.toString();
    }

    private String buildSosRemark(Long effectiveUserId, FamilyPortalSosRequest request)
    {
        StringBuilder builder = new StringBuilder("来源=family_portal");
        builder.append(";userId=").append(effectiveUserId);
        if (StringUtils.isNotBlank(request.getCallbackPhone()))
        {
            builder.append(";callbackPhone=").append(request.getCallbackPhone().trim());
        }
        return builder.toString();
    }

    private String normalizeEmergencyFlag(String value)
    {
        return StringUtils.equalsAnyIgnoreCase(StringUtils.trim(value), "1", "Y", "YES", "TRUE") ? "Y" : "N";
    }

    private String defaultIfBlank(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private Long resolveUserId(Long requestedUserId)
    {
        if (requestedUserId != null && SecurityUtils.isAdmin())
        {
            return requestedUserId;
        }
        return SecurityUtils.getUserId();
    }

    private Long resolveFocusElderlyId(Long elderlyId, List<Map<String, Object>> elderlyList)
    {
        if (elderlyId != null)
        {
            return elderlyId;
        }
        if (elderlyList == null || elderlyList.isEmpty())
        {
            return null;
        }
        Object value = elderlyList.get(0).get("elderlyId");
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }
}
