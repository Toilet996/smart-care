package com.ruoyi.smartcare.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 家属门户 Mapper
 */
public interface FamilyPortalMapper
{
    Map<String, Object> selectFamilyProfile(@Param("userId") Long userId);

    Map<String, Object> selectCoreStats(@Param("userId") Long userId);

    List<Map<String, Object>> selectLinkedElderlyList(@Param("userId") Long userId);

    List<Map<String, Object>> selectRecentAlerts(@Param("userId") Long userId);

    List<Map<String, Object>> selectLatestReports(@Param("userId") Long userId);

    List<Map<String, Object>> selectEmergencyContacts(@Param("userId") Long userId);

    List<Map<String, Object>> selectVitalsTrend(@Param("elderlyId") Long elderlyId);

    List<Map<String, Object>> selectRecentSosAlerts(@Param("userId") Long userId);

    Map<String, Object> selectLinkedElderlyBasic(@Param("userId") Long userId, @Param("elderlyId") Long elderlyId);

    Map<String, Object> selectTodayMedicationSummary(@Param("userId") Long userId, @Param("elderlyId") Long elderlyId);

    List<Map<String, Object>> selectTodayMedicationPlans(@Param("userId") Long userId, @Param("elderlyId") Long elderlyId);
}
