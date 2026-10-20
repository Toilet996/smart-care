package com.ruoyi.smartcare.service;

import java.util.List;
import com.ruoyi.smartcare.domain.BizHealthReport;

/**
 * 健康评估报告Service接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface IBizHealthReportService 
{
    /**
     * 查询健康评估报告
     * 
     * @param reportId 健康评估报告主键
     * @return 健康评估报告
     */
    public BizHealthReport selectBizHealthReportByReportId(Long reportId);

    /**
     * 查询健康评估报告列表
     * 
     * @param bizHealthReport 健康评估报告
     * @return 健康评估报告集合
     */
    public List<BizHealthReport> selectBizHealthReportList(BizHealthReport bizHealthReport);

    /**
     * 新增健康评估报告
     * 
     * @param bizHealthReport 健康评估报告
     * @return 结果
     */
    public int insertBizHealthReport(BizHealthReport bizHealthReport);

    /**
     * 修改健康评估报告
     * 
     * @param bizHealthReport 健康评估报告
     * @return 结果
     */
    public int updateBizHealthReport(BizHealthReport bizHealthReport);

    /**
     * 批量删除健康评估报告
     * 
     * @param reportIds 需要删除的健康评估报告主键集合
     * @return 结果
     */
    public int deleteBizHealthReportByReportIds(Long[] reportIds);

    /**
     * 删除健康评估报告信息
     * 
     * @param reportId 健康评估报告主键
     * @return 结果
     */
    public int deleteBizHealthReportByReportId(Long reportId);
}
