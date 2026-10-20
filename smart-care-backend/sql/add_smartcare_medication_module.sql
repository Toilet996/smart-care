USE `smart-care`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `biz_medication_plan` (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `elderly_id` bigint NOT NULL COMMENT '老人ID',
  `elderly_name` varchar(50) NOT NULL COMMENT '老人姓名',
  `medication_name` varchar(100) NOT NULL COMMENT '药品名称',
  `dosage` varchar(50) DEFAULT NULL COMMENT '单次剂量',
  `usage_method` varchar(100) DEFAULT NULL COMMENT '用药方式',
  `period_code` varchar(20) NOT NULL COMMENT '服药时段',
  `plan_time` varchar(5) NOT NULL COMMENT '计划时间(HH:mm)',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` char(1) DEFAULT '0' COMMENT '计划状态(0执行中 1暂停)',
  `reminder_enabled` char(1) DEFAULT '1' COMMENT '提醒开关(0关闭 1开启)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服药计划表';

CREATE TABLE IF NOT EXISTS `biz_medication_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '执行记录ID',
  `plan_id` bigint DEFAULT NULL COMMENT '计划ID',
  `elderly_id` bigint NOT NULL COMMENT '老人ID',
  `elderly_name` varchar(50) NOT NULL COMMENT '老人姓名',
  `medication_name` varchar(100) NOT NULL COMMENT '药品名称',
  `plan_time` varchar(5) DEFAULT NULL COMMENT '计划时间',
  `execute_status` char(1) DEFAULT '0' COMMENT '执行状态(0待服 1已服 2漏服)',
  `source_type` varchar(20) DEFAULT 'manual' COMMENT '来源(sensor/manual/system)',
  `metric_value` varchar(50) DEFAULT NULL COMMENT '原始上报值',
  `scheduled_date` date NOT NULL COMMENT '计划日期',
  `scheduled_time` datetime DEFAULT NULL COMMENT '计划时间点',
  `actual_time` datetime DEFAULT NULL COMMENT '实际执行时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_medication_record_elderly` (`elderly_id`,`scheduled_date`),
  KEY `idx_medication_record_plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服药执行记录表';

SET @parent_id := 2108;
SET @menu_id := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
  @menu_id,
  '服药计划',
  @parent_id,
  8,
  'medicationPlan',
  'care/medicationPlan/index',
  '',
  'SmartcareMedicationPlan',
  1,
  0,
  'C',
  '0',
  '0',
  'care:medicationPlan:list',
  'clock',
  'admin',
  NOW(),
  '',
  NULL,
  '智慧养老服药计划'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'medicationPlan');

SET @menu_id2 := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
  @menu_id2,
  '服药执行',
  @parent_id,
  9,
  'medicationRecord',
  'care/medicationRecord/index',
  '',
  'SmartcareMedicationRecord',
  1,
  0,
  'C',
  '0',
  '0',
  'care:medicationRecord:list',
  'list',
  'admin',
  NOW(),
  '',
  NULL,
  '智慧养老服药执行记录'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'medicationRecord');

SET @plan_menu_id := (SELECT menu_id FROM sys_menu WHERE parent_id = @parent_id AND path = 'medicationPlan' LIMIT 1);
SET @record_menu_id := (SELECT menu_id FROM sys_menu WHERE parent_id = @parent_id AND path = 'medicationRecord' LIMIT 1);
SET @family_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_family' LIMIT 1);
SET @community_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_community' LIMIT 1);
SET @admin_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_admin' LIMIT 1);

INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@community_role_id, @plan_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@admin_role_id, @plan_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@family_role_id, @record_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@community_role_id, @record_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@admin_role_id, @record_menu_id);

DELETE FROM biz_medication_plan WHERE plan_id BETWEEN 200 AND 215;
INSERT INTO biz_medication_plan
(plan_id, elderly_id, elderly_name, medication_name, dosage, usage_method, period_code, plan_time, start_date, end_date, status, reminder_enabled, remark, create_by, create_time)
VALUES
(200, 1, '张三', '降压药', '1片', '早餐后口服', 'morning', '08:00', '2026-04-01', NULL, '0', '1', '配合药盒提醒', 'admin', NOW()),
(201, 1, '张三', '胃药', '1袋', '晚餐后口服', 'evening', '19:00', '2026-04-01', NULL, '0', '1', '饭后30分钟内服用', 'admin', NOW()),
(202, 201, '李秀兰', '降压药', '1片', '早餐后口服', 'morning', '07:30', '2026-04-01', NULL, '0', '1', '晨起测压后服用', 'admin', NOW()),
(203, 202, '王建国', '稳心颗粒', '1袋', '午餐后口服', 'noon', '12:30', '2026-04-01', NULL, '0', '1', '注意午后心率波动', 'admin', NOW()),
(204, 203, '陈桂芳', '降糖药', '1片', '早餐前口服', 'morning', '07:00', '2026-04-01', NULL, '0', '1', '空腹服用', 'admin', NOW()),
(205, 205, '周庆山', '止咳平喘药', '2粒', '睡前口服', 'bedtime', '21:00', '2026-04-01', NULL, '0', '1', '夜间重点关注', 'admin', NOW());

DELETE FROM biz_medication_record WHERE record_id BETWEEN 300 AND 320;
INSERT INTO biz_medication_record
(record_id, plan_id, elderly_id, elderly_name, medication_name, plan_time, execute_status, source_type, metric_value, scheduled_date, scheduled_time, actual_time, remark, create_by, create_time)
VALUES
(300, 200, 1, '张三', '降压药', '08:00', '1', 'sensor', 'taken', '2026-04-02', '2026-04-02 08:00:00', '2026-04-02 08:03:00', '来自智能药盒上报：已服药', 'system', NOW()),
(301, 201, 1, '张三', '胃药', '19:00', '0', 'system', 'waiting', '2026-04-02', '2026-04-02 19:00:00', NULL, '待晚间服药', 'system', NOW()),
(302, 202, 201, '李秀兰', '降压药', '07:30', '1', 'manual', 'taken', '2026-04-02', '2026-04-02 07:30:00', '2026-04-02 07:36:00', '社区随访确认已服药', 'admin', NOW()),
(303, 203, 202, '王建国', '稳心颗粒', '12:30', '2', 'sensor', 'missed', '2026-04-02', '2026-04-02 12:30:00', '2026-04-02 13:05:00', '来自智能药盒上报：漏服', 'system', NOW()),
(304, 204, 203, '陈桂芳', '降糖药', '07:00', '1', 'sensor', 'taken', '2026-04-02', '2026-04-02 07:00:00', '2026-04-02 07:02:00', '来自智能药盒上报：已服药', 'system', NOW()),
(305, 205, 205, '周庆山', '止咳平喘药', '21:00', '0', 'system', 'waiting', '2026-04-02', '2026-04-02 21:00:00', NULL, '夜间待服药计划', 'system', NOW());
