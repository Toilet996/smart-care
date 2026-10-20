package com.ruoyi.smartcare.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.smartcare.mapper.BizHealthReportMapper;
import com.ruoyi.smartcare.domain.BizHealthReport;
import com.ruoyi.smartcare.service.IBizHealthReportService;

/**
 * 健康评估报告Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class BizHealthReportServiceImpl implements IBizHealthReportService 
{
    @Autowired
    private BizHealthReportMapper bizHealthReportMapper;

    @Autowired
    private SmartCareFamilyAccessService familyAccessService;

    /**
     * 查询健康评估报告
     * 
     * @param reportId 健康评估报告主键
     * @return 健康评估报告
     */
    @Override
    public BizHealthReport selectBizHealthReportByReportId(Long reportId)
    {
        BizHealthReport report = bizHealthReportMapper.selectBizHealthReportByReportId(reportId);
        if (report != null)
        {
            familyAccessService.assertCanAccessElderly(report.getElderlyId());
        }
        return report;
    }

    /**
     * 查询健康评估报告列表
     * 
     * @param bizHealthReport 健康评估报告
     * @return 健康评估报告
     */
    @Override
    public List<BizHealthReport> selectBizHealthReportList(BizHealthReport bizHealthReport)
    {
        familyAccessService.applyFamilyScope(bizHealthReport);
        return bizHealthReportMapper.selectBizHealthReportList(bizHealthReport);
    }

    /**
     * 新增健康评估报告
     * 
     * @param bizHealthReport 健康评估报告
     * @return 结果
     */
    @Override
    public int insertBizHealthReport(BizHealthReport bizHealthReport)
    {
        bizHealthReport.setCreateTime(DateUtils.getNowDate());
        return bizHealthReportMapper.insertBizHealthReport(bizHealthReport);
    }

    /**
     * 修改健康评估报告
     * 
     * @param bizHealthReport 健康评估报告
     * @return 结果
     */
    @Override
    public int updateBizHealthReport(BizHealthReport bizHealthReport)
    {
        return bizHealthReportMapper.updateBizHealthReport(bizHealthReport);
    }

    /**
     * 批量删除健康评估报告
     * 
     * @param reportIds 需要删除的健康评估报告主键
     * @return 结果
     */
    @Override
    public int deleteBizHealthReportByReportIds(Long[] reportIds)
    {
        return bizHealthReportMapper.deleteBizHealthReportByReportIds(reportIds);
    }

    /**
     * 删除健康评估报告信息
     * 
     * @param reportId 健康评估报告主键
     * @return 结果
     */
    @Override
    public int deleteBizHealthReportByReportId(Long reportId)
    {
        return bizHealthReportMapper.deleteBizHealthReportByReportId(reportId);
    }
}
