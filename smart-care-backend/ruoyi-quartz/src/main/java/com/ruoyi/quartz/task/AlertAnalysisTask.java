package com.ruoyi.quartz.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.smartcare.service.IAlertAnalysisService;

/**
 * 预警分析定时任务
 * 每分钟执行一次预警扫描
 * 
 * @author ruoyi
 * @date 2026-04-01
 */
@Component("alertAnalysisTask")
public class AlertAnalysisTask
{
    @Autowired
    private IAlertAnalysisService alertAnalysisService;

    /**
     * 执行预警分析定时任务
     * 
     * Cron表达式: 0 * * * * ? = 每分钟执行一次
     */
    public void analyzeAlert()
    {
        try
        {
            alertAnalysisService.runScheduledAnalysis();
        }
        catch (Exception e)
        {
            System.err.println("预警分析定时任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
