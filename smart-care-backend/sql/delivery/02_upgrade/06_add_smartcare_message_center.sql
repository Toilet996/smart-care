USE `smart-care`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `biz_message_notice` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `biz_key` varchar(100) NOT NULL COMMENT '业务唯一键',
  `user_id` bigint NOT NULL COMMENT '目标用户ID',
  `role_key` varchar(50) DEFAULT NULL COMMENT '目标角色',
  `message_title` varchar(100) NOT NULL COMMENT '消息标题',
  `message_content` varchar(1000) DEFAULT NULL COMMENT '消息内容',
  `message_type` varchar(30) NOT NULL COMMENT '消息类型(alert/workOrder)',
  `message_level` varchar(10) DEFAULT '0' COMMENT '消息等级',
  `elderly_id` bigint DEFAULT NULL COMMENT '老人ID',
  `elderly_name` varchar(50) DEFAULT NULL COMMENT '老人姓名',
  `alert_id` bigint DEFAULT NULL COMMENT '关联预警ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联工单ID',
  `related_route` varchar(255) DEFAULT NULL COMMENT '前端跳转路径',
  `read_status` char(1) DEFAULT '0' COMMENT '阅读状态(0未读 1已读)',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`message_id`),
  UNIQUE KEY `uk_biz_key` (`biz_key`),
  KEY `idx_message_user` (`user_id`,`read_status`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智慧养老消息中心';

SET @parent_id := 2108;
SET @menu_id := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
    @menu_id,
    '消息中心',
    @parent_id,
    3,
    'messageCenter',
    'care/messageCenter/index',
    '',
    'SmartcareMessageCenter',
    1,
    0,
    'C',
    '0',
    '0',
    'care:message:list',
    'message',
    'admin',
    NOW(),
    '',
    NULL,
    '智慧养老站内消息中心'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'messageCenter'
);

SET @message_menu_id := (SELECT menu_id FROM sys_menu WHERE parent_id = @parent_id AND path = 'messageCenter' LIMIT 1);
SET @family_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_family' LIMIT 1);
SET @community_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_community' LIMIT 1);
SET @admin_role_id := (SELECT role_id FROM sys_role WHERE role_key = 'smartcare_admin' LIMIT 1);

INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@family_role_id, @message_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@community_role_id, @message_menu_id);
INSERT IGNORE INTO sys_role_menu(role_id, menu_id) VALUES (@admin_role_id, @message_menu_id);

-- Backfill alert messages for family users
INSERT IGNORE INTO biz_message_notice
(biz_key, user_id, role_key, message_title, message_content, message_type, message_level, elderly_id, elderly_name, alert_id, order_id, related_route, read_status, create_by, create_time, remark)
SELECT
  CONCAT('ALERT_CREATE_', ar.alert_id, '_', ef.user_id),
  ef.user_id,
  'smartcare_family',
  CONCAT(CASE WHEN ar.alert_level >= 2 THEN '重要预警：' ELSE '关注预警：' END, ar.elderly_name, ' ', ar.alert_type),
  ar.alert_desc,
  'alert',
  CAST(ar.alert_level AS CHAR),
  ar.elderly_id,
  ar.elderly_name,
  ar.alert_id,
  NULL,
  CONCAT('/smartcare/alertRecord?alertId=', ar.alert_id),
  '0',
  'system',
  ar.create_time,
  '历史预警回填'
FROM biz_alert_record ar
INNER JOIN biz_elderly_family ef ON ar.elderly_id = ef.elderly_id;

-- Backfill alert messages for community/admin users
INSERT IGNORE INTO biz_message_notice
(biz_key, user_id, role_key, message_title, message_content, message_type, message_level, elderly_id, elderly_name, alert_id, order_id, related_route, read_status, create_by, create_time, remark)
SELECT
  CONCAT('ALERT_CREATE_', ar.alert_id, '_', ur.user_id),
  ur.user_id,
  r.role_key,
  CONCAT(CASE WHEN ar.alert_level >= 2 THEN '重要预警：' ELSE '关注预警：' END, ar.elderly_name, ' ', ar.alert_type),
  ar.alert_desc,
  'alert',
  CAST(ar.alert_level AS CHAR),
  ar.elderly_id,
  ar.elderly_name,
  ar.alert_id,
  NULL,
  CONCAT('/smartcare/alertRecord?alertId=', ar.alert_id),
  '0',
  'system',
  ar.create_time,
  '历史预警回填'
FROM biz_alert_record ar
INNER JOIN sys_user_role ur ON 1 = 1
INNER JOIN sys_role r ON ur.role_id = r.role_id
INNER JOIN sys_user u ON ur.user_id = u.user_id
WHERE r.role_key IN ('smartcare_community', 'smartcare_admin')
  AND u.status = '0'
  AND u.del_flag = '0';

-- Backfill work order messages
INSERT IGNORE INTO biz_message_notice
(biz_key, user_id, role_key, message_title, message_content, message_type, message_level, elderly_id, elderly_name, alert_id, order_id, related_route, read_status, create_by, create_time, remark)
SELECT
  CONCAT('ORDER_CREATE_', wo.order_id, '_', ur.user_id),
  ur.user_id,
  r.role_key,
  CONCAT('新处置工单：', wo.elderly_name),
  '系统已自动生成处置工单，请及时处理。',
  'workOrder',
  '1',
  wo.elderly_id,
  wo.elderly_name,
  wo.alert_id,
  wo.order_id,
  CONCAT('/smartcare/workOrder?orderId=', wo.order_id),
  '0',
  'system',
  wo.create_time,
  '历史工单回填'
FROM biz_work_order wo
INNER JOIN sys_user_role ur ON 1 = 1
INNER JOIN sys_role r ON ur.role_id = r.role_id
INNER JOIN sys_user u ON ur.user_id = u.user_id
WHERE r.role_key IN ('smartcare_community', 'smartcare_admin')
  AND u.status = '0'
  AND u.del_flag = '0';
