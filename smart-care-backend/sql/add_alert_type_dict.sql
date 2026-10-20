USE `smart-care`;

START TRANSACTION;

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `remark`)
SELECT 100, '预警类型', 'biz_alert_type', '0', 'admin', NOW(), '智慧养老预警类型字典'
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'biz_alert_type'
);

SET @next_dict_code := (SELECT IFNULL(MAX(`dict_code`), 0) + 1 FROM `sys_dict_data`);

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT @next_dict_code, 4, '呼吸异常', 'breath_rate', 'biz_alert_type', '', 'warning', 'N', '0', 'admin', NOW(), '呼吸率异常'
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'biz_alert_type' AND `dict_value` = 'breath_rate'
);
SET @next_dict_code := @next_dict_code + 1;

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT @next_dict_code, 5, '离床异常', 'leave_bed', 'biz_alert_type', '', 'warning', 'N', '0', 'admin', NOW(), '长时间离床或夜间离床'
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'biz_alert_type' AND `dict_value` = 'leave_bed'
);
SET @next_dict_code := @next_dict_code + 1;

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT @next_dict_code, 6, '烟雾异常', 'smoke_value', 'biz_alert_type', '', 'danger', 'N', '0', 'admin', NOW(), '烟雾浓度异常'
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'biz_alert_type' AND `dict_value` = 'smoke_value'
);
SET @next_dict_code := @next_dict_code + 1;

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
SELECT @next_dict_code, 7, '长时间未活动', 'inactivity', 'biz_alert_type', '', 'warning', 'N', '0', 'admin', NOW(), '长时间未活动预警'
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'biz_alert_type' AND `dict_value` = 'inactivity'
);

COMMIT;
