USE `smart-care`;

-- SmartCare multi-elderly demo data
-- Re-runnable demo script:
-- 1. Fix Zhang San <-> Zhao Si family relation
-- 2. Add 3 more elderly profiles
-- 3. Add 3 family accounts (password: 123456)
-- 4. Add family links, devices, sensor logs, alerts, work orders, reports, visit records

SET NAMES utf8mb4;

-- Family demo users
INSERT INTO `sys_user`
(`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(110, 100, 'liumin', '刘敏', '00', '', '13900010001', '1', '', '$2a$10$EZ7i/2HBGVVXDifKWiG.R..AxTnOaMjT11fUpHcpuzv.Zg9AkKvHe', '0', '0', '', NULL, NOW(), 'admin', NOW(), '', NULL, '智慧养老演示家属账号'),
(111, 100, 'wanglei', '王磊', '00', '', '13900010002', '0', '', '$2a$10$EZ7i/2HBGVVXDifKWiG.R..AxTnOaMjT11fUpHcpuzv.Zg9AkKvHe', '0', '0', '', NULL, NOW(), 'admin', NOW(), '', NULL, '智慧养老演示家属账号'),
(112, 100, 'chenzhi', '陈志', '00', '', '13900010003', '0', '', '$2a$10$EZ7i/2HBGVVXDifKWiG.R..AxTnOaMjT11fUpHcpuzv.Zg9AkKvHe', '0', '0', '', NULL, NOW(), 'admin', NOW(), '', NULL, '智慧养老演示家属账号')
ON DUPLICATE KEY UPDATE
`dept_id` = VALUES(`dept_id`),
`nick_name` = VALUES(`nick_name`),
`phonenumber` = VALUES(`phonenumber`),
`sex` = VALUES(`sex`),
`password` = VALUES(`password`),
`status` = VALUES(`status`),
`del_flag` = VALUES(`del_flag`),
`remark` = VALUES(`remark`);

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`)
VALUES
(110, 100),
(111, 100),
(112, 100);

-- Elderly profiles
INSERT INTO `biz_elderly`
(`elderly_id`, `elderly_name`, `age`, `gender`, `id_card`, `phone`, `address`, `health_status`, `status`, `management_status`, `responsible_person`, `next_visit_time`, `intervention_plan`, `follow_up_result`, `latest_handler`, `management_start_time`, `release_reason`, `create_by`, `create_time`, `remark`)
VALUES
(201, '李秀兰', 82, '1', '430111194310120126', '13800010001', 'XX社区松鹤苑1栋201', '高血压、夜间起夜偏多', '0', '1', '社区护士周敏', '2026-04-04 09:30:00', '保持晨间测压与晚间散步，持续观察夜间离床次数', '近3天睡眠趋于稳定，提醒减少晚间饮水', '周敏', '2026-03-20 09:00:00', NULL, 'admin', NOW(), '演示老人数据'),
(202, '王建国', 76, '0', '430111194911083217', '13800010002', 'XX社区康宁里2栋502', '冠心病术后、步态不稳', '0', '2', '社区医生李晨', '2026-04-03 14:00:00', '加强午后活动监测，必要时上门复诊', '近24小时心率波动偏大，已列入高风险跟踪', '李晨', '2026-03-18 10:00:00', NULL, 'admin', NOW(), '演示老人数据'),
(203, '陈桂芳', 80, '1', '430111194602220048', '13800010003', 'XX社区颐和家园3栋102', '糖尿病、独居', '0', '1', '网格员王敏', '2026-04-05 10:30:00', '跟进送药与居家巡访，关注午后活动情况', '居家环境安全，近期无严重异常', '王敏', '2026-03-22 11:00:00', NULL, 'admin', NOW(), '演示老人数据')
ON DUPLICATE KEY UPDATE
`elderly_name` = VALUES(`elderly_name`),
`age` = VALUES(`age`),
`gender` = VALUES(`gender`),
`id_card` = VALUES(`id_card`),
`phone` = VALUES(`phone`),
`address` = VALUES(`address`),
`health_status` = VALUES(`health_status`),
`status` = VALUES(`status`),
`management_status` = VALUES(`management_status`),
`responsible_person` = VALUES(`responsible_person`),
`next_visit_time` = VALUES(`next_visit_time`),
`intervention_plan` = VALUES(`intervention_plan`),
`follow_up_result` = VALUES(`follow_up_result`),
`latest_handler` = VALUES(`latest_handler`),
`management_start_time` = VALUES(`management_start_time`),
`release_reason` = VALUES(`release_reason`),
`remark` = VALUES(`remark`);

-- Fix Zhang San family relation and add more family links
UPDATE `biz_elderly_family`
SET
  `user_id` = 100,
  `family_name` = '赵四',
  `family_phone` = '18566668888',
  `relation` = '儿子',
  `is_emergency` = '1',
  `create_time` = IFNULL(`create_time`, NOW())
WHERE `id` = 100;

DELETE FROM `biz_elderly_family`
WHERE `id` BETWEEN 110 AND 112;

INSERT INTO `biz_elderly_family`
(`id`, `elderly_id`, `elderly_name`, `user_id`, `family_name`, `family_phone`, `relation`, `is_emergency`, `create_time`)
VALUES
(110, 201, '李秀兰', 110, '刘敏', '13900010001', '女儿', '1', NOW()),
(111, 202, '王建国', 111, '王磊', '13900010002', '儿子', '1', NOW()),
(112, 203, '陈桂芳', 112, '陈志', '13900010003', '侄子', '1', NOW());

-- Devices
DELETE FROM `iot_device`
WHERE `id` BETWEEN 201 AND 209;

INSERT INTO `iot_device`
(`id`, `device_id`, `device_name`, `device_type`, `elderly_id`, `elderly_name`, `status`, `install_time`, `remark`)
VALUES
(201, 'SC201-BED', '智能床垫-李秀兰', 'bed_mat', 201, '李秀兰', '0', '2026-03-20 09:20:00', '演示设备'),
(202, 'SC201-RAD', '跌倒雷达-李秀兰', 'fall_radar', 201, '李秀兰', '0', '2026-03-20 09:30:00', '演示设备'),
(203, 'SC201-SMK', '烟感探测器-李秀兰', 'smoke', 201, '李秀兰', '0', '2026-03-20 09:35:00', '演示设备'),
(204, 'SC202-BED', '智能床垫-王建国', 'bed_mat', 202, '王建国', '0', '2026-03-18 10:20:00', '演示设备'),
(205, 'SC202-RAD', '跌倒雷达-王建国', 'fall_radar', 202, '王建国', '0', '2026-03-18 10:25:00', '演示设备'),
(206, 'SC202-SOS', '紧急按键-王建国', 'sos_button', 202, '王建国', '0', '2026-03-18 10:30:00', '演示设备'),
(207, 'SC203-BED', '智能床垫-陈桂芳', 'bed_mat', 203, '陈桂芳', '0', '2026-03-22 11:20:00', '演示设备'),
(208, 'SC203-RAD', '跌倒雷达-陈桂芳', 'fall_radar', 203, '陈桂芳', '0', '2026-03-22 11:25:00', '演示设备'),
(209, 'SC203-SMK', '烟感探测器-陈桂芳', 'smoke', 203, '陈桂芳', '0', '2026-03-22 11:30:00', '演示设备');

-- Sensor logs
DELETE FROM `iot_sensor_log`
WHERE `log_id` BETWEEN 1100 AND 1125;

INSERT INTO `iot_sensor_log`
(`log_id`, `device_id`, `device_name`, `elderly_id`, `elderly_name`, `metric_type`, `metric_value`, `report_time`)
VALUES
(1100, 'SC201-BED', '智能床垫-李秀兰', 201, '李秀兰', 'leave_bed', 'on_bed', '2026-04-02 06:12:00'),
(1101, 'SC201-BED', '智能床垫-李秀兰', 201, '李秀兰', 'heart_rate', '72', '2026-04-02 08:10:00'),
(1102, 'SC201-BED', '智能床垫-李秀兰', 201, '李秀兰', 'breath_rate', '17', '2026-04-02 08:10:00'),
(1103, 'SC201-SMK', '烟感探测器-李秀兰', 201, '李秀兰', 'smoke_value', '12', '2026-04-02 08:12:00'),

(1104, 'SC202-BED', '智能床垫-王建国', 202, '王建国', 'leave_bed', 'off_bed', '2026-04-02 14:20:00'),
(1105, 'SC202-BED', '智能床垫-王建国', 202, '王建国', 'heart_rate', '108', '2026-04-02 14:22:00'),
(1106, 'SC202-BED', '智能床垫-王建国', 202, '王建国', 'breath_rate', '24', '2026-04-02 14:22:00'),
(1107, 'SC202-RAD', '跌倒雷达-王建国', 202, '王建国', 'smoke_value', '18', '2026-04-02 14:25:00'),

(1108, 'SC203-BED', '智能床垫-陈桂芳', 203, '陈桂芳', 'leave_bed', 'on_bed', '2026-04-02 05:48:00'),
(1109, 'SC203-BED', '智能床垫-陈桂芳', 203, '陈桂芳', 'heart_rate', '78', '2026-04-02 07:30:00'),
(1110, 'SC203-BED', '智能床垫-陈桂芳', 203, '陈桂芳', 'breath_rate', '18', '2026-04-02 07:30:00'),
(1111, 'SC203-SMK', '烟感探测器-陈桂芳', 203, '陈桂芳', 'smoke_value', '14', '2026-04-02 07:35:00'),

(1112, 'SC201-BED', '智能床垫-李秀兰', 201, '李秀兰', 'heart_rate', '74', '2026-04-02 15:00:00'),
(1113, 'SC201-BED', '智能床垫-李秀兰', 201, '李秀兰', 'breath_rate', '16', '2026-04-02 15:00:00'),
(1114, 'SC202-BED', '智能床垫-王建国', 202, '王建国', 'heart_rate', '105', '2026-04-02 15:05:00'),
(1115, 'SC202-BED', '智能床垫-王建国', 202, '王建国', 'breath_rate', '23', '2026-04-02 15:05:00'),
(1116, 'SC203-BED', '智能床垫-陈桂芳', 203, '陈桂芳', 'heart_rate', '80', '2026-04-02 15:10:00'),
(1117, 'SC203-BED', '智能床垫-陈桂芳', 203, '陈桂芳', 'breath_rate', '18', '2026-04-02 15:10:00');

-- Alerts
DELETE FROM `biz_alert_record`
WHERE `alert_id` BETWEEN 201 AND 205;

INSERT INTO `biz_alert_record`
(`alert_id`, `elderly_id`, `elderly_name`, `device_id`, `device_name`, `alert_level`, `alert_type`, `alert_desc`, `handle_status`, `create_time`, `remark`)
VALUES
(201, 202, '王建国', 'SC202-BED', '智能床垫-王建国', 2, 'heart_rate', '午后心率持续升高，超过个体近7日均值，请尽快电话核实。', '1', '2026-04-02 14:25:00', '已通知社区医生跟进'),
(202, 202, '王建国', 'SC202-BED', '智能床垫-王建国', 1, 'leave_bed', '午后离床后长时间未恢复规律活动，建议持续观察。', '0', '2026-04-02 14:40:00', NULL),
(203, 203, '陈桂芳', 'SC203-RAD', '跌倒雷达-陈桂芳', 1, 'fall', '午后活动量明显下降，系统已标记为重点关注。', '2', '2026-04-01 16:20:00', '社区已电话回访'),
(204, 201, '李秀兰', 'SC201-BED', '智能床垫-李秀兰', 1, 'leave_bed', '凌晨起夜次数较近一周略有增加，建议关注晚间饮水量。', '2', '2026-04-01 06:50:00', '家属已知晓'),
(205, 203, '陈桂芳', 'SC203-SMK', '烟感探测器-陈桂芳', 2, 'smoke_value', '厨房烟雾浓度短时升高，疑似烹饪时通风不足。', '0', '2026-04-02 11:15:00', NULL);

-- Work orders
DELETE FROM `biz_work_order`
WHERE `order_id` BETWEEN 201 AND 204;

INSERT INTO `biz_work_order`
(`order_id`, `alert_id`, `elderly_id`, `elderly_name`, `process_record`, `status`, `create_time`)
VALUES
(201, 201, 202, '王建国', '社区医生李晨已电话联系家属，建议今日复测心率并观察胸闷情况。', '1', '2026-04-02 14:30:00'),
(202, 202, 202, '王建国', '网格员待上门核实午后活动情况。', '0', '2026-04-02 14:45:00'),
(203, 203, 203, '陈桂芳', '社区已完成回访，确认老人午休较长，无跌倒情况。', '2', '2026-04-01 16:35:00'),
(204, 205, 203, '陈桂芳', '已生成现场核查工单，待社区处理。', '0', '2026-04-02 11:20:00');

-- Health reports (report_type: 0日报 1周报)
DELETE FROM `biz_health_report`
WHERE `report_id` BETWEEN 201 AND 212;

INSERT INTO `biz_health_report`
(`report_id`, `elderly_id`, `elderly_name`, `report_type`, `report_date`, `sleep_score`, `activity_score`, `ai_evaluation`, `create_time`)
VALUES
(201, 201, '李秀兰', '0', '2026-04-01', 85, 76, '日报结论：昨夜睡眠整体平稳，起夜2次，今日晨间心率与呼吸率正常。建议继续保持晚饭后轻度散步，并关注夜间饮水量。', '2026-04-01 08:10:00'),
(202, 201, '李秀兰', '0', '2026-04-02', 83, 74, '日报结论：今日在床时间较昨日略长，生命体征稳定。建议家属上午致电关怀，提醒按时活动。', '2026-04-02 08:10:00'),
(203, 201, '李秀兰', '1', '2026-04-02', 84, 75, '周报结论：本周睡眠质量总体平稳，夜间起夜次数略高但未形成连续高风险，建议延续当前作息管理。', '2026-04-02 18:00:00'),

(204, 202, '王建国', '0', '2026-04-01', 73, 68, '日报结论：白天活动量偏低，午后静止时段偏长，建议社区持续关注步态稳定性。', '2026-04-01 08:20:00'),
(205, 202, '王建国', '0', '2026-04-02', 70, 62, '日报结论：今日午后出现心率偏高与离床后活动不足的情况，系统建议尽快完成电话核实与上门评估。', '2026-04-02 15:20:00'),
(206, 202, '王建国', '1', '2026-04-02', 72, 65, '周报结论：本周整体处于高风险跟踪状态，建议家属与社区共同关注午后活动减少和心率波动。', '2026-04-02 18:10:00'),

(207, 203, '陈桂芳', '0', '2026-04-01', 80, 72, '日报结论：老人居家状态平稳，早间生命体征正常。建议继续保持规律服药与居家巡查。', '2026-04-01 08:30:00'),
(208, 203, '陈桂芳', '0', '2026-04-02', 81, 70, '日报结论：今日烟雾浓度出现短时波动，已建议注意开窗通风。其余指标总体平稳。', '2026-04-02 11:40:00'),
(209, 203, '陈桂芳', '1', '2026-04-02', 80, 71, '周报结论：本周风险总体可控，主要问题集中在居家环境安全提醒，建议继续保持社区送药与巡访。', '2026-04-02 18:20:00'),

(210, 1, '张三', '0', '2026-04-02', 82, 77, '日报结论：今日在床、活动与烟感数据总体平稳，需继续关注夜间离床频次。', '2026-04-02 08:00:00'),
(211, 1, '张三', '1', '2026-04-02', 81, 76, '周报结论：近一周总体稳定，偶发烟雾与离床预警，建议家属持续关注厨房与夜间活动。', '2026-04-02 18:30:00'),
(212, 203, '陈桂芳', '0', '2026-03-31', 79, 73, '日报结论：午后活动稍少，建议次日增加短时室内步行。', '2026-03-31 08:20:00');

-- Visit records (visit_type: 0例行 1健康 2送药)
DELETE FROM `biz_visit_record`
WHERE `record_id` BETWEEN 201 AND 209;

INSERT INTO `biz_visit_record`
(`record_id`, `elderly_id`, `elderly_name`, `visit_type`, `visit_time`, `visit_content`, `create_by`)
VALUES
(201, 201, '李秀兰', '0', '2026-04-01 09:20:00', '<p>网格员上门查看居家环境与夜间起夜情况，老人精神状态平稳。</p><p>已提醒晚间减少大量饮水，并保持饭后短时活动。</p>', '社区网格员王敏'),
(202, 201, '李秀兰', '1', '2026-04-02 10:00:00', '<p>社区护士复核血压与睡眠情况，生命体征稳定。</p><p>建议继续坚持晨起测压和午后补水。</p>', '社区护士周敏'),
(203, 202, '王建国', '1', '2026-04-02 15:00:00', '<p>社区医生重点回访午后心率偏高问题，已建议家属协助复测。</p><p>若晚间仍持续升高，将安排进一步就诊评估。</p>', '社区医生李晨'),
(204, 202, '王建国', '0', '2026-04-01 16:10:00', '<p>例行巡访发现午后活动减少、步态偏慢。</p><p>已纳入高风险跟踪对象，安排次日再次回访。</p>', '社区网格员王敏'),
(205, 202, '王建国', '2', '2026-03-31 11:20:00', '<p>社区药师送药上门，核对本周药量并再次说明早晚服药时点。</p><p>老人可独立完成服药，但需家属电话提醒。</p>', '社区药师周婷'),
(206, 203, '陈桂芳', '0', '2026-04-02 11:40:00', '<p>上门核查厨房烟雾短时报警，确认系做饭时通风不足。</p><p>已协助老人检查油烟机并提醒开窗通风。</p>', '社区网格员王敏'),
(207, 203, '陈桂芳', '2', '2026-04-01 10:30:00', '<p>完成送药并检查药品余量，老人服药依从性较好。</p><p>已提醒按时测量空腹血糖。</p>', '社区药师周婷'),
(208, 203, '陈桂芳', '1', '2026-03-31 09:40:00', '<p>健康随访显示睡眠与心率总体平稳，午后活动略少。</p><p>建议增加室内轻活动并继续观察一周。</p>', '社区医生李晨'),
(209, 1, '张三', '0', '2026-04-02 09:00:00', '<p>例行随访确认居家环境安全，家属反馈近期状态总体稳定。</p><p>建议继续关注夜间离床与厨房烟感提醒。</p>', '社区网格员王敏');
