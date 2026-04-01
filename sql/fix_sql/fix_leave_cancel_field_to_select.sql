-- ============================================================
-- 销假模板 leaveRequestNo 字段由 input 改为 select + optionSource
-- 适用于已部署环境的在线更新
-- ============================================================

UPDATE `sc_affair_template`
SET `form_schema_json` = '[{"field":"leaveRequestNo","label":"原请假编号","component":"select","required":true,"optionSource":"myLeaveRequests","hintText":"仅显示已审批通过且尚未销假的请假记录"},{"field":"actualReturnDate","label":"实际返校日期","component":"date","required":true},{"field":"cancelRemark","label":"销假说明","component":"textarea","required":true,"hintText":"如提前返校或返校后补登，请在此说明。"}]',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `template_code` = 'ASK_LEAVE_CANCEL';
