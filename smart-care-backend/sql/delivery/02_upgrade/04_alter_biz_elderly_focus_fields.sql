USE `smart-care`;

SET @db_name := 'smart-care';

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'management_status'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `management_status` varchar(1) DEFAULT ''0'' COMMENT ''纳管状态（0未纳管 1重点纳管 2高风险跟踪 3已解除）'' AFTER `status`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'responsible_person'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `responsible_person` varchar(64) DEFAULT NULL COMMENT ''责任人'' AFTER `management_status`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'next_visit_time'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `next_visit_time` datetime DEFAULT NULL COMMENT ''下次随访时间'' AFTER `responsible_person`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'intervention_plan'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `intervention_plan` varchar(500) DEFAULT NULL COMMENT ''干预措施'' AFTER `next_visit_time`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'follow_up_result'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `follow_up_result` varchar(500) DEFAULT NULL COMMENT ''跟进结果'' AFTER `intervention_plan`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'latest_handler'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `latest_handler` varchar(64) DEFAULT NULL COMMENT ''最近处置人'' AFTER `follow_up_result`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'management_start_time'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `management_start_time` datetime DEFAULT NULL COMMENT ''纳管开始时间'' AFTER `latest_handler`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db_name AND TABLE_NAME = 'biz_elderly' AND COLUMN_NAME = 'release_reason'),
  'SELECT 1',
  'ALTER TABLE `biz_elderly` ADD COLUMN `release_reason` varchar(500) DEFAULT NULL COMMENT ''解除原因'' AFTER `management_start_time`'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
