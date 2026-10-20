USE `smart-care`;

/*
  第一轮权限隔离：
  1. 新建三类角色：家属 / 社区 / 管理员
  2. 修正智慧养老关键菜单权限点
  3. 按角色分配智慧养老菜单
*/

SET @family_role_key := 'smartcare_family';
SET @community_role_key := 'smartcare_community';
SET @admin_role_key := 'smartcare_admin';
SET @parent_menu_id := 2108;

INSERT INTO sys_role
(
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '智慧养老家属', @family_role_key, 110, '1', 1, 1, '0', '0', 'admin', NOW(), '仅查看家属门户及本人关联老人信息'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = @family_role_key);

INSERT INTO sys_role
(
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '智慧养老社区', @community_role_key, 120, '1', 1, 1, '0', '0', 'admin', NOW(), '用于社区监控、预警处置、巡访随访'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = @community_role_key);

INSERT INTO sys_role
(
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '智慧养老管理员', @admin_role_key, 130, '1', 1, 1, '0', '0', 'admin', NOW(), '用于全量后台配置与管理'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = @admin_role_key);

SET @family_role_id := (SELECT role_id FROM sys_role WHERE role_key = @family_role_key LIMIT 1);
SET @community_role_id := (SELECT role_id FROM sys_role WHERE role_key = @community_role_key LIMIT 1);
SET @admin_role_id := (SELECT role_id FROM sys_role WHERE role_key = @admin_role_key LIMIT 1);

UPDATE sys_menu
SET perms = 'care:family:portal'
WHERE parent_id = @parent_menu_id
  AND path = 'familyPortal';

UPDATE sys_menu
SET perms = 'care:ai:generate'
WHERE parent_id = @parent_menu_id
  AND path = 'aiCare';

DELETE FROM sys_role_menu
WHERE role_id IN (@family_role_id, @community_role_id, @admin_role_id)
  AND menu_id IN (
    SELECT menu_id FROM sys_menu WHERE parent_id = @parent_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @family_role_id, m.menu_id
FROM sys_menu m
WHERE m.parent_id = @parent_menu_id
  AND m.path IN ('familyPortal');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, m.menu_id
FROM sys_menu m
WHERE m.parent_id = @parent_menu_id
  AND m.path IN ('overview', 'overviewScreen', 'focusManagement');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, m.menu_id
FROM sys_menu m
WHERE m.parent_id = @parent_menu_id;

/*
  如果你希望家属和社区还能直接进入这些业务页，需要把对应菜单也分配过去。
  下面按 path 做补充分配，避免因为菜单缺失导致前端跳转 404。
*/

SET @menu_alert_record := (SELECT menu_id FROM sys_menu WHERE path = 'alertRecord' LIMIT 1);
SET @menu_health_report := (SELECT menu_id FROM sys_menu WHERE path = 'healthReport' LIMIT 1);
SET @menu_work_order := (SELECT menu_id FROM sys_menu WHERE path = 'workOrder' LIMIT 1);
SET @menu_visit_record := (SELECT menu_id FROM sys_menu WHERE path = 'visitRecord' LIMIT 1);
SET @menu_elderly := (SELECT menu_id FROM sys_menu WHERE path = 'elderly' LIMIT 1);
SET @menu_family := (SELECT menu_id FROM sys_menu WHERE path = 'family' LIMIT 1);
SET @menu_ai_care := (SELECT menu_id FROM sys_menu WHERE parent_id = @parent_menu_id AND path = 'aiCare' LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @family_role_id, @menu_alert_record FROM dual
WHERE @menu_alert_record IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @family_role_id AND menu_id = @menu_alert_record);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @family_role_id, @menu_health_report FROM dual
WHERE @menu_health_report IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @family_role_id AND menu_id = @menu_health_report);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_alert_record FROM dual
WHERE @menu_alert_record IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_alert_record);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_health_report FROM dual
WHERE @menu_health_report IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_health_report);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_work_order FROM dual
WHERE @menu_work_order IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_work_order);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_visit_record FROM dual
WHERE @menu_visit_record IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_visit_record);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_elderly FROM dual
WHERE @menu_elderly IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_elderly);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @community_role_id, @menu_family FROM dual
WHERE @menu_family IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @community_role_id AND menu_id = @menu_family);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, @menu_ai_care FROM dual
WHERE @menu_ai_care IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = @admin_role_id AND menu_id = @menu_ai_care);
