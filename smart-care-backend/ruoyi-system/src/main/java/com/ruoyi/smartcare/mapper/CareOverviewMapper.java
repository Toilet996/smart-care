package com.ruoyi.smartcare.mapper;

import java.util.List;
import java.util.Map;

/**
 * 智慧养老社区总览 Mapper 接口
 */
public interface CareOverviewMapper
{
    Map<String, Object> selectCoreStats();

    List<Map<String, Object>> selectAlertLevelStats();

    List<Map<String, Object>> selectDeviceStatusStats();

    List<Map<String, Object>> selectElderlyWatchList();

    List<Map<String, Object>> selectRecentAlerts();

    List<Map<String, Object>> selectPendingOrders();

    List<Map<String, Object>> selectLatestReports();
}
