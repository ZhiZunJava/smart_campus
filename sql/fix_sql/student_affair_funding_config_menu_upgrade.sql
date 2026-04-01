SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `sys_menu`
  (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT 2341, '资助配置', 2300, 6, 'fundingConfig', 'campus/affair/funding/index', '', '', 1, 0, 'C', '0', '0', 'campus:affairTemplate:list', 'ri-hand-coin-line', 'admin', NOW(), '奖助学金、贫困认定、勤工助学配置'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2341);

SET FOREIGN_KEY_CHECKS = 1;
