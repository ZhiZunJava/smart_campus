SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'ASK_LEAVE_CANCEL',
  '销假登记',
  'ASK_LEAVE_CANCEL',
  '销假登记',
  'student',
  '[{"field":"leaveRequestNo","label":"原请假编号","component":"select","required":true,"optionSource":"myLeaveRequests","hintText":"仅显示已审批通过且尚未销假的请假记录"},{"field":"actualReturnDate","label":"实际返校日期","component":"date","required":true},{"field":"cancelRemark","label":"销假说明","component":"textarea","required":true,"hintText":"如提前返校或返校后补登，请在此说明。"}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员确认","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '0',
  '1',
  '',
  '',
  2,
  '0',
  'admin',
  NOW(),
  '请销假场景下的销假登记模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'ASK_LEAVE'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

INSERT INTO `sc_affair_template`
  (`category_id`, `template_code`, `template_name`, `business_code`, `business_name`, `audience_roles`, `form_schema_json`, `workflow_schema_json`, `allow_attachment`, `allow_revoke`, `target_status_code`, `target_status_name`, `sort_order`, `status`, `create_by`, `create_time`, `remark`)
SELECT
  c.category_id,
  'LEAVE_RETURN_CONFIRM',
  '返校登记',
  'LEAVE_RETURN_CONFIRM',
  '返校登记',
  'student',
  '[{"field":"leaveRequestNo","label":"离校申请编号","component":"input","required":false},{"field":"returnDate","label":"返校日期","component":"date","required":true},{"field":"travelSummary","label":"行程说明","component":"textarea","required":true,"hintText":"请填写返校交通方式、返校时间及需要说明的信息。"}]',
  '[{"stepCode":"advisor_review","stepName":"辅导员确认","assignmentType":"ROLE","reviewerRole":"advisor"}]',
  '0',
  '1',
  '',
  '',
  3,
  '0',
  'admin',
  NOW(),
  '离返校场景下的返校登记模板'
FROM `sc_affair_category` c
WHERE c.category_code = 'LEAVE_RETURN_SCHOOL'
ON DUPLICATE KEY UPDATE
  `template_name` = VALUES(`template_name`),
  `business_name` = VALUES(`business_name`),
  `form_schema_json` = VALUES(`form_schema_json`),
  `workflow_schema_json` = VALUES(`workflow_schema_json`),
  `allow_attachment` = VALUES(`allow_attachment`),
  `allow_revoke` = VALUES(`allow_revoke`),
  `sort_order` = VALUES(`sort_order`),
  `status` = VALUES(`status`),
  `remark` = VALUES(`remark`),
  `update_by` = 'admin',
  `update_time` = NOW();

SET FOREIGN_KEY_CHECKS = 1;
