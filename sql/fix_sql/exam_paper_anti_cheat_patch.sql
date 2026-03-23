ALTER TABLE `sc_exam_paper`
  ADD COLUMN `anti_cheat_enabled` char(1) DEFAULT '1' COMMENT '是否启用防作弊(0否 1是)' AFTER `allow_review_analysis`,
  ADD COLUMN `max_focus_loss_count` int DEFAULT 5 COMMENT '允许切屏次数' AFTER `anti_cheat_enabled`,
  ADD COLUMN `auto_submit_on_focus_loss_limit` char(1) DEFAULT '0' COMMENT '切屏超限是否自动交卷(0否 1是)' AFTER `max_focus_loss_count`,
  ADD COLUMN `allow_copy_paste` char(1) DEFAULT '1' COMMENT '是否允许复制粘贴(0否 1是)' AFTER `auto_submit_on_focus_loss_limit`,
  ADD COLUMN `max_attempt_count` int DEFAULT 0 COMMENT '最多可考次数(0表示不限制)' AFTER `allow_copy_paste`,
  ADD COLUMN `allow_view_score` char(1) DEFAULT '1' COMMENT '考后是否允许学生查看成绩(0否 1是)' AFTER `max_attempt_count`,
  ADD COLUMN `question_navigation_mode` varchar(32) DEFAULT 'free' COMMENT '切题方式(free自由切题 strict_sequence严格顺序作答)' AFTER `question_order_mode`;
