SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

UPDATE `sc_affair_template`
SET `form_schema_json` = '[{"field":"termId","label":"申报学期","component":"select","required":true,"optionSource":"term"},{"field":"scholarshipType","label":"奖项类型","component":"select","required":true,"options":[{"label":"国家奖学金","value":"NATIONAL"},{"label":"校级奖学金","value":"SCHOOL"},{"label":"专项奖学金","value":"SPECIAL"}]},{"field":"deptId","label":"申报院系","component":"select","required":false,"optionSource":"dept"},{"field":"mentorTeacherId","label":"指导教师","component":"user-select","required":true,"optionSource":"teacher"},{"field":"honorLevel","label":"成果/荣誉","component":"textarea","required":true},{"field":"reason","label":"申请陈述","component":"textarea","required":true}]'
WHERE `template_code` = 'SCHOLARSHIP_APPLY';

UPDATE `sc_affair_template`
SET `form_schema_json` = '[{"field":"termId","label":"申请学期","component":"select","required":true,"optionSource":"term"},{"field":"grantType","label":"助学金类型","component":"select","required":true,"options":[{"label":"国家助学金","value":"NATIONAL"},{"label":"校级助学金","value":"SCHOOL"}]},{"field":"familyIncome","label":"家庭年收入（元）","component":"number","required":true},{"field":"hardshipLevel","label":"困难等级","component":"select","required":true,"options":[{"label":"特别困难","value":"LEVEL_A"},{"label":"困难","value":"LEVEL_B"},{"label":"一般困难","value":"LEVEL_C"}]},{"field":"reason","label":"申请说明","component":"textarea","required":true}]'
WHERE `template_code` = 'GRANT_APPLY';

UPDATE `sc_affair_template`
SET `form_schema_json` = '[{"field":"termId","label":"认定学期","component":"select","required":true,"optionSource":"term"},{"field":"familyIncome","label":"家庭年收入（元）","component":"number","required":true},{"field":"familyMembers","label":"家庭人口","component":"number","required":true},{"field":"hardshipLevel","label":"困难等级","component":"select","required":true,"options":[{"label":"特别困难","value":"LEVEL_A"},{"label":"困难","value":"LEVEL_B"},{"label":"一般困难","value":"LEVEL_C"}]},{"field":"reason","label":"认定说明","component":"textarea","required":true}]'
WHERE `template_code` = 'POVERTY_IDENTIFICATION_APPLY';

UPDATE `sc_affair_template`
SET `form_schema_json` = '[{"field":"termId","label":"申请学期","component":"select","required":true,"optionSource":"term"},{"field":"jobId","label":"岗位编号","component":"input","required":true},{"field":"jobPreference","label":"岗位意向","component":"input","required":true},{"field":"availableHours","label":"每周可投入时长","component":"number","required":true},{"field":"skillTags","label":"技能特长","component":"input","required":false},{"field":"reason","label":"申请说明","component":"textarea","required":true}]'
WHERE `template_code` = 'WORK_STUDY_APPLY';

SET FOREIGN_KEY_CHECKS = 1;
