package com.ruoyi.smartcare.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.smartcare.domain.IotSensorLog;
import com.ruoyi.smartcare.domain.SensorDataDTO;
import com.ruoyi.smartcare.service.IIotSensorLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 传感器流水Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/iot/sensorLog")
public class IotSensorLogController extends BaseController
{
    @Autowired
    private IIotSensorLogService iotSensorLogService;

    /**
     * 查询传感器流水列表
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotSensorLog iotSensorLog)
    {
        startPage();
        List<IotSensorLog> list = iotSensorLogService.selectIotSensorLogList(iotSensorLog);
        return getDataTable(list);
    }

    /**
     * 导出传感器流水列表
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:export')")
    @Log(title = "传感器流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotSensorLog iotSensorLog)
    {
        List<IotSensorLog> list = iotSensorLogService.selectIotSensorLogList(iotSensorLog);
        ExcelUtil<IotSensorLog> util = new ExcelUtil<IotSensorLog>(IotSensorLog.class);
        util.exportExcel(response, list, "传感器流水数据");
    }

    /**
     * 获取传感器流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(iotSensorLogService.selectIotSensorLogByLogId(logId));
    }

    /**
     * 新增传感器流水
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:add')")
    @Log(title = "传感器流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotSensorLog iotSensorLog)
    {
        return toAjax(iotSensorLogService.insertIotSensorLog(iotSensorLog));
    }

    /**
     * 修改传感器流水
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:edit')")
    @Log(title = "传感器流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotSensorLog iotSensorLog)
    {
        return toAjax(iotSensorLogService.updateIotSensorLog(iotSensorLog));
    }

    /**
     * 删除传感器流水
     */
    @PreAuthorize("@ss.hasPermi('iot:sensorLog:remove')")
    @Log(title = "传感器流水", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(iotSensorLogService.deleteIotSensorLogByLogIds(logIds));
    }

    /**
     * 传感器数据上报接口
     * 支持单条和批量上报
     */
    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody SensorDataDTO sensorData)
    {
        return toAjax(iotSensorLogService.uploadSensorData(sensorData));
    }

    /**
     * 批量传感器数据上报接口
     */
    @PostMapping("/uploadBatch")
    public AjaxResult uploadBatch(@RequestBody List<SensorDataDTO> sensorDataList)
    {
        return toAjax(iotSensorLogService.uploadSensorDataBatch(sensorDataList));
    }
}
