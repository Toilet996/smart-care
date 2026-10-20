-- smart-care 字典与业务数据一致性修复脚本
-- 执行库：smart-care
-- 建议先在 Navicat 中备份后再执行

USE `smart-care`;

START TRANSACTION;

-- 1. 统一设备类型编码：
-- iot_device 中存在 smart_bed，但系统字典 biz_device_type 使用 bed_mat 表示智能床垫
UPDATE `iot_device`
SET `device_type` = 'bed_mat'
WHERE `device_type` = 'smart_bed';

-- 2. 统一传感器指标编码：
-- iot_sensor_log 中存在 bed_status，但系统字典 biz_metric_type 使用 leave_bed 表示离床状态
UPDATE `iot_sensor_log`
SET `metric_type` = 'leave_bed'
WHERE `metric_type` = 'bed_status';

COMMIT;

-- =========================
-- 以下为人工确认项，默认不自动执行
-- =========================

-- A. 这条流水明显是测试脏数据：
-- log_id=1000, device_id=1, device_name=1
-- 如确认无用，可执行：
-- DELETE FROM `iot_sensor_log` WHERE `log_id` = 1000;

-- B. 这条预警记录是明显占位测试数据：
-- alert_id=100, elderly_id/device_id/device_name 均为 11
-- 如确认无用，可执行：
-- DELETE FROM `biz_alert_record` WHERE `alert_id` = 100;

-- 如果你希望保留，也可以改成真实设备/老人：
-- UPDATE `biz_alert_record`
-- SET `elderly_id` = 1,
--     `elderly_name` = '张三',
--     `device_id` = 'D002',
--     `device_name` = '跌倒雷达张三'
-- WHERE `alert_id` = 100;

-- C. 规则表存在测试名称：
-- rule_id=11 的 rule_name='1'
-- rule_id=13 的 rule_name='13'
-- 可按真实业务名手工修改，例如：
-- UPDATE `biz_alert_rule` SET `rule_name` = '心率异常' WHERE `rule_id` = 11;
-- UPDATE `biz_alert_rule` SET `rule_name` = '烟雾浓度异常' WHERE `rule_id` = 13;

-- D. 以下表中示例数据使用了 0 值，但字段注释是从 1 开始：
-- biz_health_report.report_type: 当前有 0，注释为 1日报 2周报
-- biz_visit_record.visit_type: 当前有 0，注释为 1例行 2健康 3送药
-- 建议你先确认这两张表是否只是测试数据，再决定是否修正。
