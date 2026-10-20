USE `smart-care`;

START TRANSACTION;

SET @status_exists := (
  SELECT COUNT(*)
  FROM `information_schema`.`COLUMNS`
  WHERE `TABLE_SCHEMA` = 'smart-care'
    AND `TABLE_NAME` = 'biz_alert_rule'
    AND `COLUMN_NAME` = 'status'
);
SET @status_sql := IF(
  @status_exists = 0,
  'ALTER TABLE `biz_alert_rule` ADD COLUMN `status` varchar(1) DEFAULT ''0'' COMMENT ''规则状态（0启用 1停用）'' AFTER `alert_level`',
  'SELECT 1'
);
PREPARE stmt FROM @status_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @alert_type_exists := (
  SELECT COUNT(*)
  FROM `information_schema`.`COLUMNS`
  WHERE `TABLE_SCHEMA` = 'smart-care'
    AND `TABLE_NAME` = 'biz_alert_rule'
    AND `COLUMN_NAME` = 'alert_type'
);
SET @alert_type_sql := IF(
  @alert_type_exists = 0,
  'ALTER TABLE `biz_alert_rule` ADD COLUMN `alert_type` varchar(50) DEFAULT NULL COMMENT ''预警类型'' AFTER `status`',
  'SELECT 1'
);
PREPARE stmt FROM @alert_type_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `biz_alert_rule`
SET `status` = '0'
WHERE `status` IS NULL;

INSERT INTO `biz_alert_rule`
(`rule_name`, `device_type`, `metric_type`, `condition_op`, `threshold_value`, `alert_level`, `status`, `alert_type`, `remark`)
SELECT '心率过高预警', 'bed_mat', 'heart_rate', '&gt;', '100', 1, '0', 'heart_rate', '心率超过100次/分钟触发'
WHERE NOT EXISTS (
  SELECT 1 FROM `biz_alert_rule`
  WHERE `rule_name` = '心率过高预警'
);

INSERT INTO `biz_alert_rule`
(`rule_name`, `device_type`, `metric_type`, `condition_op`, `threshold_value`, `alert_level`, `status`, `alert_type`, `remark`)
SELECT '心率过低预警', 'bed_mat', 'heart_rate', '&lt;', '50', 1, '0', 'heart_rate', '心率低于50次/分钟触发'
WHERE NOT EXISTS (
  SELECT 1 FROM `biz_alert_rule`
  WHERE `rule_name` = '心率过低预警'
);

INSERT INTO `biz_alert_rule`
(`rule_name`, `device_type`, `metric_type`, `condition_op`, `threshold_value`, `alert_level`, `status`, `alert_type`, `remark`)
SELECT '夜间离床预警', 'bed_mat', 'leave_bed', '=', 'off_bed', 1, '0', 'leave_bed', '检测到夜间离床状态时触发'
WHERE NOT EXISTS (
  SELECT 1 FROM `biz_alert_rule`
  WHERE `rule_name` = '夜间离床预警'
);

INSERT INTO `biz_alert_rule`
(`rule_name`, `device_type`, `metric_type`, `condition_op`, `threshold_value`, `alert_level`, `status`, `alert_type`, `remark`)
SELECT '烟雾浓度预警', 'smoke', 'smoke_value', '&gt;', '0', 2, '0', 'smoke_value', '检测到烟雾浓度异常时触发'
WHERE NOT EXISTS (
  SELECT 1 FROM `biz_alert_rule`
  WHERE `rule_name` = '烟雾浓度预警'
);

COMMIT;
