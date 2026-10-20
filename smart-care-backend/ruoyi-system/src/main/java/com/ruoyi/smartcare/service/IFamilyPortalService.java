package com.ruoyi.smartcare.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.smartcare.domain.BizElderlyFamily;
import com.ruoyi.smartcare.domain.FamilyPortalContactRequest;
import com.ruoyi.smartcare.domain.FamilyPortalSosRequest;

/**
 * 家属门户 Service
 */
public interface IFamilyPortalService
{
    Map<String, Object> getPortalData(Long requestedUserId, Long elderlyId);

    List<BizElderlyFamily> listContacts(Long requestedUserId);

    int saveContact(Long requestedUserId, FamilyPortalContactRequest request);

    int updateContact(Long requestedUserId, FamilyPortalContactRequest request);

    int deleteContacts(Long requestedUserId, Long[] ids);

    Map<String, Object> triggerSos(Long requestedUserId, FamilyPortalSosRequest request);
}
