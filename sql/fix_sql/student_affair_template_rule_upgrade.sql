SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `sc_affair_template`
  ADD COLUMN IF NOT EXISTS `apply_scope_json` text COMMENT '申请范围配置JSON' AFTER `allow_revoke`,
  ADD COLUMN IF NOT EXISTS `business_rules_json` text COMMENT '业务规则配置JSON' AFTER `apply_scope_json`;

UPDATE `sc_affair_template`
SET
  `apply_scope_json` = COALESCE(NULLIF(`apply_scope_json`, ''), '{"scopeType":"ALL","deptIds":[],"gradeIds":[],"classIds":[],"userIds":[]}'),
  `business_rules_json` = COALESCE(NULLIF(`business_rules_json`, ''), '{"requireNoPending":true}')
WHERE `template_code` IN (
  'ASK_LEAVE_STANDARD', 'ASK_LEAVE_CANCEL', 'LEAVE_RETURN_STANDARD', 'LEAVE_RETURN_CONFIRM',
  'TEXTBOOK_APPLY', 'ACADEMIC_STATUS_SUSPEND', 'ACADEMIC_STATUS_RESUME', 'ACADEMIC_STATUS_TRANSFER_MAJOR',
  'ACTIVITY_APPLY', 'SUBJECT_COMPETITION_APPLY', 'INNOVATION_PROJECT_APPLY', 'COMPREHENSIVE_EVALUATION_APPLY'
);

UPDATE `sc_affair_template`
SET
  `apply_scope_json` = '{"scopeType":"ALL","deptIds":[],"gradeIds":[],"classIds":[],"userIds":[]}',
  `business_rules_json` = '{"requireNoPending":true,"prerequisiteCategoryCodes":["POVERTY_IDENTIFICATION"]}'
WHERE `template_code` = 'GRANT_APPLY';

UPDATE `sc_affair_template`
SET
  `apply_scope_json` = '{"scopeType":"ALL","deptIds":[],"gradeIds":[],"classIds":[],"userIds":[]}',
  `business_rules_json` = '{"requireNoPending":true,"termId":null,"openWindowEnabled":true,"openStartTime":"","openEndTime":"","notice":"请在认定窗口期内提交家庭经济困难认定申请。"}'
WHERE `template_code` = 'POVERTY_IDENTIFICATION_APPLY';

UPDATE `sc_affair_template`
SET
  `apply_scope_json` = '{"scopeType":"ALL","deptIds":[],"gradeIds":[],"classIds":[],"userIds":[]}',
  `business_rules_json` = '{"requireNoPending":true,"termId":null,"openWindowEnabled":false,"openStartTime":"","openEndTime":"","notice":"请先由学院、辅导员或资助管理老师配置可申请学生范围。"}'
WHERE `template_code` = 'SCHOLARSHIP_APPLY';

UPDATE `sc_affair_template`
SET
  `apply_scope_json` = '{"scopeType":"ALL","deptIds":[],"gradeIds":[],"classIds":[],"userIds":[]}',
  `business_rules_json` = '{"requireNoPending":true,"termId":null,"openWindowEnabled":false,"openStartTime":"","openEndTime":"","requirePublishedJob":true,"notice":"勤工助学需先发布岗位，学生按岗位进行申请。"}'
WHERE `template_code` = 'WORK_STUDY_APPLY';

SET FOREIGN_KEY_CHECKS = 1;
