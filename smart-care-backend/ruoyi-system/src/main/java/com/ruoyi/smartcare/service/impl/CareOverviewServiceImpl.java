package com.ruoyi.smartcare.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.CareOverviewMapper;
import com.ruoyi.smartcare.service.ICareOverviewService;

/**
 * 智慧养老社区总览 Service 实现
 */
@Service
public class CareOverviewServiceImpl implements ICareOverviewService
{
    @Autowired
    private CareOverviewMapper careOverviewMapper;

    @Override
    public Map<String, Object> getOverviewData()
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("coreStats", careOverviewMapper.selectCoreStats());
        result.put("alertLevelStats", careOverviewMapper.selectAlertLevelStats());
        result.put("deviceStatusStats", careOverviewMapper.selectDeviceStatusStats());
        result.put("elderlyWatchList", careOverviewMapper.selectElderlyWatchList());
        result.put("recentAlerts", careOverviewMapper.selectRecentAlerts());
        result.put("pendingOrders", careOverviewMapper.selectPendingOrders());
        result.put("latestReports", careOverviewMapper.selectLatestReports());
        return result;
    }
}
