USE `smart-care`;

SET @dict_max := (SELECT IFNULL(MAX(dict_code), 100) FROM sys_dict_data);

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 5, '智能门磁', 'door_sensor', 'biz_device_type', '', 'primary', 'N', '0', 'admin', NOW(), '家属门户扩展设备'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_device_type' AND dict_value = 'door_sensor');

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 6, '智能药盒', 'pill_box', 'biz_device_type', '', 'warning', 'N', '0', 'admin', NOW(), '家属门户扩展设备'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_device_type' AND dict_value = 'pill_box');

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 5, '门磁状态', 'door_status', 'biz_metric_type', '', 'primary', 'N', '0', 'admin', NOW(), '家属门户扩展指标'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_metric_type' AND dict_value = 'door_status');

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 6, '服药状态', 'pill_status', 'biz_metric_type', '', 'warning', 'N', '0', 'admin', NOW(), '家属门户扩展指标'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_metric_type' AND dict_value = 'pill_status');

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 4, '门未关', 'door_status', 'biz_alert_type', '', 'warning', 'N', '0', 'admin', NOW(), '家属门户扩展预警'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_alert_type' AND dict_value = 'door_status');

INSERT INTO sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT @dict_max := @dict_max + 1, 5, '漏服药', 'pill_status', 'biz_alert_type', '', 'danger', 'N', '0', 'admin', NOW(), '家属门户扩展预警'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'biz_alert_type' AND dict_value = 'pill_status');

INSERT INTO iot_device(id, device_id, device_name, device_type, elderly_id, elderly_name, status, install_time, remark)
SELECT 106, 'D005', '入户门磁-张三', 'door_sensor', 1, '张三', '0', CURDATE(), '家属门户演示设备：门磁'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_device WHERE id = 106 OR device_id = 'D005');

INSERT INTO iot_device(id, device_id, device_name, device_type, elderly_id, elderly_name, status, install_time, remark)
SELECT 107, 'D006', '智能药盒-张三', 'pill_box', 1, '张三', '0', CURDATE(), '家属门户演示设备：药盒'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_device WHERE id = 107 OR device_id = 'D006');

INSERT INTO iot_sensor_log(log_id, device_id, device_name, elderly_id, elderly_name, metric_type, metric_value, report_time)
SELECT 1022, 'D005', '入户门磁-张三', 1, '张三', 'door_status', 'closed', DATE_SUB(NOW(), INTERVAL 45 MINUTE)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_sensor_log WHERE log_id = 1022);

INSERT INTO iot_sensor_log(log_id, device_id, device_name, elderly_id, elderly_name, metric_type, metric_value, report_time)
SELECT 1023, 'D005', '入户门磁-张三', 1, '张三', 'door_status', 'open', DATE_SUB(NOW(), INTERVAL 12 MINUTE)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_sensor_log WHERE log_id = 1023);

INSERT INTO iot_sensor_log(log_id, device_id, device_name, elderly_id, elderly_name, metric_type, metric_value, report_time)
SELECT 1024, 'D006', '智能药盒-张三', 1, '张三', 'pill_status', 'waiting', DATE_SUB(NOW(), INTERVAL 6 HOUR)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_sensor_log WHERE log_id = 1024);

INSERT INTO iot_sensor_log(log_id, device_id, device_name, elderly_id, elderly_name, metric_type, metric_value, report_time)
SELECT 1025, 'D006', '智能药盒-张三', 1, '张三', 'pill_status', 'taken', DATE_SUB(NOW(), INTERVAL 2 HOUR)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_sensor_log WHERE log_id = 1025);

INSERT INTO iot_sensor_log(log_id, device_id, device_name, elderly_id, elderly_name, metric_type, metric_value, report_time)
SELECT 1026, 'D006', '智能药盒-张三', 1, '张三', 'pill_status', 'missed', DATE_SUB(NOW(), INTERVAL 30 MINUTE)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM iot_sensor_log WHERE log_id = 1026);

INSERT INTO biz_alert_record(alert_id, elderly_id, elderly_name, device_id, device_name, alert_type, alert_level, alert_desc, handle_status, create_time)
SELECT 106, 1, '张三', 'D005', '入户门磁-张三', 'door_status', 1, '门磁检测到外出后门未及时关闭', '0', DATE_SUB(NOW(), INTERVAL 10 MINUTE)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM biz_alert_record WHERE alert_id = 106);

INSERT INTO biz_alert_record(alert_id, elderly_id, elderly_name, device_id, device_name, alert_type, alert_level, alert_desc, handle_status, create_time)
SELECT 107, 1, '张三', 'D006', '智能药盒-张三', 'pill_status', 1, '药盒检测到本次服药提醒后未按时服药', '0', DATE_SUB(NOW(), INTERVAL 25 MINUTE)
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM biz_alert_record WHERE alert_id = 107);
