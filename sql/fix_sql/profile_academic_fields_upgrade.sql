-- ============================================================
-- 学籍信息完善：为 sc_user_profile 补齐学籍管理所需字段
-- 包含：基本信息、录取信息、毕业信息、学位信息、联系信息、证件信息
-- ============================================================

-- ---------- 基本信息补充 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `former_name`       varchar(50)  DEFAULT NULL COMMENT '曾用名' AFTER `real_name`,
  ADD COLUMN IF NOT EXISTS `nation`            varchar(30)  DEFAULT NULL COMMENT '民族' AFTER `gender`,
  ADD COLUMN IF NOT EXISTS `birth_date`        date         DEFAULT NULL COMMENT '出生日期' AFTER `nation`,
  ADD COLUMN IF NOT EXISTS `political_status`  varchar(30)  DEFAULT NULL COMMENT '政治面貌' AFTER `birth_date`,
  ADD COLUMN IF NOT EXISTS `native_place`      varchar(100) DEFAULT NULL COMMENT '籍贯' AFTER `political_status`,
  ADD COLUMN IF NOT EXISTS `id_type`           varchar(30)  DEFAULT '身份证' COMMENT '证件类型' AFTER `native_place`,
  ADD COLUMN IF NOT EXISTS `id_number`         varchar(30)  DEFAULT NULL COMMENT '证件号码' AFTER `id_type`,
  ADD COLUMN IF NOT EXISTS `cultivation_type`  varchar(30)  DEFAULT NULL COMMENT '培养类型' AFTER `major`,
  ADD COLUMN IF NOT EXISTS `education_level`   varchar(30)  DEFAULT NULL COMMENT '学历层次' AFTER `cultivation_type`,
  ADD COLUMN IF NOT EXISTS `schooling_length`  varchar(10)  DEFAULT NULL COMMENT '学制(年)' AFTER `education_level`;

-- ---------- 录取信息 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `admission_type`      varchar(30)  DEFAULT NULL COMMENT '录取方式' AFTER `admission_year`,
  ADD COLUMN IF NOT EXISTS `exam_number`         varchar(30)  DEFAULT NULL COMMENT '考生号' AFTER `admission_type`,
  ADD COLUMN IF NOT EXISTS `admission_ticket_no` varchar(30)  DEFAULT NULL COMMENT '准考证号' AFTER `exam_number`,
  ADD COLUMN IF NOT EXISTS `admission_score`     decimal(6,2) DEFAULT NULL COMMENT '录取分数' AFTER `admission_ticket_no`,
  ADD COLUMN IF NOT EXISTS `admission_batch`     varchar(50)  DEFAULT NULL COMMENT '录取批次' AFTER `admission_score`,
  ADD COLUMN IF NOT EXISTS `source_region`       varchar(100) DEFAULT NULL COMMENT '生源地' AFTER `admission_batch`,
  ADD COLUMN IF NOT EXISTS `graduate_school`     varchar(100) DEFAULT NULL COMMENT '毕业中学' AFTER `source_region`;

-- ---------- 毕业信息 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `expected_graduation_date` date         DEFAULT NULL COMMENT '预计毕业时间' AFTER `graduate_school`,
  ADD COLUMN IF NOT EXISTS `actual_graduation_date`   date         DEFAULT NULL COMMENT '实际毕业时间' AFTER `expected_graduation_date`,
  ADD COLUMN IF NOT EXISTS `diploma_no`               varchar(50)  DEFAULT NULL COMMENT '毕业证书编号' AFTER `actual_graduation_date`,
  ADD COLUMN IF NOT EXISTS `completion_type`           varchar(20)  DEFAULT NULL COMMENT '结业/肄业标识(毕业/结业/肄业)' AFTER `diploma_no`,
  ADD COLUMN IF NOT EXISTS `graduation_destination`    varchar(200) DEFAULT NULL COMMENT '毕业去向' AFTER `completion_type`;

-- ---------- 学位信息 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `degree_type`        varchar(30)  DEFAULT NULL COMMENT '学位类别' AFTER `graduation_destination`,
  ADD COLUMN IF NOT EXISTS `degree_award_date`  date         DEFAULT NULL COMMENT '学位授予日期' AFTER `degree_type`,
  ADD COLUMN IF NOT EXISTS `degree_cert_no`     varchar(50)  DEFAULT NULL COMMENT '学位证书编号' AFTER `degree_award_date`,
  ADD COLUMN IF NOT EXISTS `thesis_title`       varchar(200) DEFAULT NULL COMMENT '学位论文题目' AFTER `degree_cert_no`;

-- ---------- 联系信息 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `contact_address`    varchar(300) DEFAULT NULL COMMENT '通讯地址' AFTER `thesis_title`,
  ADD COLUMN IF NOT EXISTS `home_address`       varchar(300) DEFAULT NULL COMMENT '家庭住址' AFTER `contact_address`,
  ADD COLUMN IF NOT EXISTS `emergency_contact`  varchar(50)  DEFAULT NULL COMMENT '紧急联系人' AFTER `home_address`,
  ADD COLUMN IF NOT EXISTS `emergency_phone`    varchar(20)  DEFAULT NULL COMMENT '紧急联系电话' AFTER `emergency_contact`,
  ADD COLUMN IF NOT EXISTS `household_address`  varchar(300) DEFAULT NULL COMMENT '户口所在地' AFTER `emergency_phone`,
  ADD COLUMN IF NOT EXISTS `bank_card_no`       varchar(30)  DEFAULT NULL COMMENT '银行卡号' AFTER `household_address`;

-- ---------- 毕业备注 ----------
ALTER TABLE `sc_user_profile`
  ADD COLUMN IF NOT EXISTS `graduation_remark`  varchar(500) DEFAULT NULL COMMENT '毕业备注' AFTER `bank_card_no`,
  ADD COLUMN IF NOT EXISTS `degree_remark`      varchar(500) DEFAULT NULL COMMENT '学位备注' AFTER `graduation_remark`;
