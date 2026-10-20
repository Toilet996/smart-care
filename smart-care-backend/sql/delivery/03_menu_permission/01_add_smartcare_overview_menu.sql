USE `smart-care`;

SET @parent_id := 2108;
SET @menu_id := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
    @menu_id,
    '社区总览',
    @parent_id,
    0,
    'overview',
    'care/overview/index',
    '',
    'SmartcareOverview',
    1,
    0,
    'C',
    '0',
    '0',
    'care:overview:list',
    'dashboard',
    'admin',
    NOW(),
    '',
    NULL,
    '智慧养老社区监控总览'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'overview'
);
