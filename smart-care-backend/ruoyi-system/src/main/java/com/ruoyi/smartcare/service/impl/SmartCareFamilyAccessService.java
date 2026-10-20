package com.ruoyi.smartcare.service.impl;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.smartcare.mapper.BizElderlyFamilyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 智慧养老家属数据权限支持
 */
@Service
public class SmartCareFamilyAccessService
{
    private static final String FAMILY_ROLE_KEY = "smartcare_family";

    @Autowired
    private BizElderlyFamilyMapper bizElderlyFamilyMapper;

    public void applyFamilyScope(BaseEntity entity)
    {
        if (entity == null || !isFamilyUser())
        {
            return;
        }
        entity.getParams().put("currentUserId", SecurityUtils.getUserId());
    }

    public void assertCanAccessElderly(Long elderlyId)
    {
        if (!isFamilyUser() || elderlyId == null)
        {
            return;
        }
        Long count = bizElderlyFamilyMapper.countLinkedElderlyByUserId(SecurityUtils.getUserId(), elderlyId);
        if (count == null || count <= 0)
        {
            throw new ServiceException("无权访问该老人数据");
        }
    }

    public boolean isFamilyUser()
    {
        return !SecurityUtils.isAdmin() && SecurityUtils.hasRole(FAMILY_ROLE_KEY);
    }
}
