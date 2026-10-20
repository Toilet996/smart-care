# 智慧养老系统 SQL 交付说明

本目录为智慧养老系统数据库交付包，已按“基线库、升级补丁、菜单权限、演示数据”进行整理。

## 一、目录结构

### 1. `01_baseline`

用于新环境初始化的基线数据库脚本。

- [01_smart-care_baseline.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/01_baseline/01_smart-care_baseline.sql)

说明：

1. 该脚本为当前 `smart-care` 数据库导出基线。
2. 适用于全新环境初始化。
3. 如果使用该基线脚本初始化，后续仍建议继续执行升级补丁和菜单权限脚本，以确保最新功能全部落库。

### 2. `02_upgrade`

用于在已有智慧养老数据库基础上执行增量升级。

包含：

1. [01_fix_smartcare_dict_consistency.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/01_fix_smartcare_dict_consistency.sql)
2. [02_update_alert_rule.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/02_update_alert_rule.sql)
3. [03_add_alert_type_dict.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/03_add_alert_type_dict.sql)
4. [04_alter_biz_elderly_focus_fields.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/04_alter_biz_elderly_focus_fields.sql)
5. [05_add_smartcare_medication_module.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/05_add_smartcare_medication_module.sql)
6. [06_add_smartcare_message_center.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/06_add_smartcare_message_center.sql)
7. [07_backfill_missing_work_orders.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/02_upgrade/07_backfill_missing_work_orders.sql)

说明：

1. 本目录主要用于功能升级和结构补丁。
2. `05_add_smartcare_medication_module.sql` 同时包含服药模块表结构、菜单和部分演示数据。
3. `06_add_smartcare_message_center.sql` 同时包含消息中心表、菜单和历史消息回填。

### 3. `03_menu_permission`

用于功能菜单、角色、权限初始化。

包含：

1. [01_add_smartcare_overview_menu.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/01_add_smartcare_overview_menu.sql)
2. [02_add_smartcare_overview_screen_menu.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/02_add_smartcare_overview_screen_menu.sql)
3. [03_add_smartcare_focus_management_menu.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/03_add_smartcare_focus_management_menu.sql)
4. [04_add_smartcare_family_portal_menu.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/04_add_smartcare_family_portal_menu.sql)
5. [05_add_smartcare_ai_care_menu.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/05_add_smartcare_ai_care_menu.sql)
6. [06_smartcare_role_permission_setup.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/03_menu_permission/06_smartcare_role_permission_setup.sql)

说明：

1. 本目录主要用于新增菜单与角色权限划分。
2. 已包含家属、社区、管理员三类角色的第一轮菜单权限分配。

### 4. `04_demo_data`

用于演示环境初始化或补充业务数据。

包含：

1. [01_add_health_report_demo_data.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/04_demo_data/01_add_health_report_demo_data.sql)
2. [02_add_visit_record_demo_data.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/04_demo_data/02_add_visit_record_demo_data.sql)
3. [03_add_multi_elderly_demo_data.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/04_demo_data/03_add_multi_elderly_demo_data.sql)
4. [04_add_community_distribution_demo.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/04_demo_data/04_add_community_distribution_demo.sql)
5. [05_add_family_portal_door_pill_support.sql](E:/code/SmartCare/smart-care-backend/sql/delivery/04_demo_data/05_add_family_portal_door_pill_support.sql)

说明：

1. 本目录脚本主要用于补充演示数据。
2. 包含老人、家属、设备、传感器、预警、工单、健康报告、随访、门磁、药盒等演示数据。

## 二、推荐执行方式

## 方案 A：新环境部署

适用于空数据库或首次部署。

建议执行顺序：

1. 执行 `01_baseline/01_smart-care_baseline.sql`
2. 依次执行 `02_upgrade` 目录内全部脚本
3. 依次执行 `03_menu_permission` 目录内全部脚本
4. 根据是否需要演示环境，执行 `04_demo_data` 目录脚本

## 方案 B：已有环境升级

适用于原有智慧养老数据库继续升级。

建议执行顺序：

1. 依次执行 `02_upgrade` 目录内全部脚本
2. 依次执行 `03_menu_permission` 目录内全部脚本
3. 按需执行 `04_demo_data` 脚本

## 三、建议说明

1. 正式生产环境建议执行基线库和升级补丁，不建议直接导入演示数据。
2. 演示环境建议完整执行演示数据脚本，以便展示家属端、社区端、工单闭环、服药计划等功能。
3. 若甲方仅关注功能演示，可优先使用“已有环境升级 + 演示数据”方式。

## 四、当前交付覆盖内容

本 SQL 交付包已覆盖以下业务能力落库：

1. 老人档案扩展字段
2. 家属门户菜单
3. 社区总览与社区大屏菜单
4. 重点老人管理菜单
5. AI 健康评估菜单
6. 角色权限初始化
7. 预警类型和字典修正
8. 服药计划与服药执行模块
9. 消息通知中心
10. 工单历史补全
11. 健康报告、随访、多老人、多家属演示数据
12. 门磁与药盒演示数据

## 五、交付建议

提交甲方时，建议同时提供以下资料：

1. 本 SQL 交付目录
2. 技术功能文档  
   [smartcare-technical-functional-spec.md](E:/code/SmartCare/smart-care-backend/docs/smartcare-technical-functional-spec.md)
3. 项目源码
4. 系统演示账号说明
