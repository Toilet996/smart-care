USE `smart-care`;

SET @parent_id := 2108;
SET @menu_id := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
    @menu_id,
    '重点老人管理',
    @parent_id,
    2,
    'focusManagement',
    'care/focusManagement/index',
    '',
    'SmartcareFocusManagement',
    1,
    0,
    'C',
    '0',
    '0',
    'care:elderly:list',
    'peoples',
    'admin',
    NOW(),
    '',
    NULL,
    '智慧养老重点老人和高风险人群专项管理'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'focusManagement'
);
