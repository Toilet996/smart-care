USE `smart-care`;

SET @parent_id := 2108;
SET @menu_id := (SELECT IFNULL(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT
    @menu_id,
    '家属门户',
    @parent_id,
    4,
    'familyPortal',
    'care/familyPortal/index',
    '',
    'SmartcareFamilyPortal',
    1,
    0,
    'C',
    '0',
    '0',
    'care:family:portal',
    'user',
    'admin',
    NOW(),
    '',
    NULL,
    '智慧养老家属端门户页面'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @parent_id AND path = 'familyPortal'
);
