package com.smart.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.ScVerificationBatch;
import com.smart.system.domain.ScVerificationChange;
import com.smart.system.domain.ScVerificationRecord;
import com.smart.system.domain.dto.VerificationBatchCreateDto;
import com.smart.system.domain.dto.VerificationBatchModifyDto;
import com.smart.system.domain.dto.VerificationBatchReviewDto;
import com.smart.system.domain.dto.VerificationReviewDto;
import com.smart.system.domain.dto.VerificationSubmitDto;
import com.smart.system.mapper.ScUserProfileMapper;
import com.smart.system.mapper.ScVerificationBatchMapper;
import com.smart.system.mapper.ScVerificationChangeMapper;
import com.smart.system.mapper.ScVerificationRecordMapper;
import com.smart.system.service.IScUserProfileService;
import com.smart.system.service.IScVerificationBatchService;

/**
 * 学籍核对批次 Service 实现
 *
 * @author can
 */
@Service
public class ScVerificationBatchServiceImpl implements IScVerificationBatchService {

    /** 字段名 -> 中文标签 映射 */
    private static final Map<String, String> FIELD_LABEL_MAP = new HashMap<>();
    static {
        FIELD_LABEL_MAP.put("realName", "姓名");
        FIELD_LABEL_MAP.put("gender", "性别");
        FIELD_LABEL_MAP.put("avatarUrl", "照片");
        FIELD_LABEL_MAP.put("major", "专业");
        FIELD_LABEL_MAP.put("admissionYear", "入学年份");
        FIELD_LABEL_MAP.put("interestTags", "兴趣标签");
        FIELD_LABEL_MAP.put("learningGoal", "学习目标");
        FIELD_LABEL_MAP.put("learningStyle", "学习风格");
        FIELD_LABEL_MAP.put("studentNo", "学号");
    }

    @Autowired
    private ScVerificationBatchMapper batchMapper;

    @Autowired
    private ScVerificationRecordMapper recordMapper;

    @Autowired
    private ScVerificationChangeMapper changeMapper;

    @Autowired
    private ScUserProfileMapper userProfileMapper;

    @Autowired
    private IScUserProfileService scUserProfileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createBatch(VerificationBatchCreateDto dto, String operator) {
        ScVerificationBatch batch = new ScVerificationBatch();
        batch.setBatchName(dto.getBatchName());
        batch.setDescription(dto.getDescription());
        batch.setEditableFields(JSON.toJSONString(dto.getEditableFields()));
        batch.setScopeType(dto.getScopeType());
        batch.setScopeGradeId(dto.getScopeGradeId());
        batch.setScopeClassId(dto.getScopeClassId());
        batch.setStartTime(dto.getStartTime());
        batch.setEndTime(dto.getEndTime());
        batch.setBatchStatus("DRAFT");
        batch.setCreateBy(operator);

        // 生成批次编号: VB + yyyyMMdd + 序号
        String prefix = "VB" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        Integer maxSeq = batchMapper.selectMaxBatchNoSeq(prefix);
        int seq = (maxSeq == null ? 0 : maxSeq) + 1;
        batch.setBatchNo(prefix + String.format("%03d", seq));

        return batchMapper.insertScVerificationBatch(batch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(VerificationBatchCreateDto dto, String operator) {
        if (dto.getBatchId() == null) {
            throw new ServiceException("批次ID不能为空");
        }
        ScVerificationBatch existing = batchMapper.selectScVerificationBatchByBatchId(dto.getBatchId());
        if (existing == null) {
            throw new ServiceException("批次不存在");
        }
        if (!"DRAFT".equals(existing.getBatchStatus())) {
            throw new ServiceException("只有草稿状态的批次可以编辑");
        }
        ScVerificationBatch batch = new ScVerificationBatch();
        batch.setBatchId(dto.getBatchId());
        batch.setBatchName(dto.getBatchName());
        batch.setDescription(dto.getDescription());
        batch.setEditableFields(JSON.toJSONString(dto.getEditableFields()));
        batch.setScopeType(dto.getScopeType());
        batch.setScopeGradeId(dto.getScopeGradeId());
        batch.setScopeClassId(dto.getScopeClassId());
        batch.setStartTime(dto.getStartTime());
        batch.setEndTime(dto.getEndTime());
        batch.setUpdateBy(operator);
        return batchMapper.updateScVerificationBatch(batch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int activateBatch(Long batchId, String operator) {
        ScVerificationBatch batch = batchMapper.selectScVerificationBatchByBatchId(batchId);
        if (batch == null) {
            throw new ServiceException("批次不存在");
        }
        if (!"DRAFT".equals(batch.getBatchStatus())) {
            throw new ServiceException("只有草稿状态的批次可以激活");
        }

        // 查询范围内学生
        ScUserProfile query = new ScUserProfile();
        query.setUserType("student");
        if ("GRADE".equals(batch.getScopeType())) {
            query.setGradeId(batch.getScopeGradeId());
        } else if ("CLASS".equals(batch.getScopeType())) {
            query.setClassId(batch.getScopeClassId());
        }
        List<ScUserProfile> students = userProfileMapper.selectScUserProfileList(query);
        if (students == null || students.isEmpty()) {
            throw new ServiceException("范围内没有找到学生，无法激活批次");
        }

        // 批量创建记录
        List<ScVerificationRecord> records = new ArrayList<>();
        for (ScUserProfile student : students) {
            ScVerificationRecord record = new ScVerificationRecord();
            record.setBatchId(batchId);
            record.setStudentUserId(student.getUserId());
            record.setStudentNo(student.getStudentNo());
            record.setStudentName(student.getRealName());
            record.setCreateBy(operator);
            records.add(record);
        }
        recordMapper.batchInsertRecords(records);

        // 更新批次状态
        ScVerificationBatch update = new ScVerificationBatch();
        update.setBatchId(batchId);
        update.setBatchStatus("ACTIVE");
        update.setUpdateBy(operator);
        return batchMapper.updateScVerificationBatch(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeBatch(Long batchId, String operator) {
        ScVerificationBatch batch = batchMapper.selectScVerificationBatchByBatchId(batchId);
        if (batch == null) {
            throw new ServiceException("批次不存在");
        }
        if (!"ACTIVE".equals(batch.getBatchStatus())) {
            throw new ServiceException("只有进行中的批次可以关闭");
        }
        ScVerificationBatch update = new ScVerificationBatch();
        update.setBatchId(batchId);
        update.setBatchStatus("CLOSED");
        update.setUpdateBy(operator);
        return batchMapper.updateScVerificationBatch(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchByIds(Long[] batchIds) {
        for (Long batchId : batchIds) {
            // 级联删除变更和记录
            ScVerificationRecord qr = new ScVerificationRecord();
            qr.setBatchId(batchId);
            List<ScVerificationRecord> records = recordMapper.selectScVerificationRecordList(qr);
            for (ScVerificationRecord r : records) {
                changeMapper.deleteScVerificationChangeByRecordId(r.getRecordId());
            }
            recordMapper.deleteScVerificationRecordByBatchId(batchId);
        }
        return batchMapper.deleteScVerificationBatchByBatchIds(batchIds);
    }

    @Override
    public List<ScVerificationBatch> selectBatchList(ScVerificationBatch query) {
        return batchMapper.selectScVerificationBatchList(query);
    }

    @Override
    public ScVerificationBatch selectBatchDetail(Long batchId) {
        ScVerificationBatch batch = batchMapper.selectScVerificationBatchByBatchId(batchId);
        if (batch == null) {
            return null;
        }
        // 填充统计
        List<Map<String, Object>> stats = recordMapper.countByBatchAndStatus(batchId);
        int total = 0, confirmed = 0, pendingReview = 0, approved = 0;
        for (Map<String, Object> row : stats) {
            String status = (String) row.get("status");
            int cnt = ((Number) row.get("cnt")).intValue();
            total += cnt;
            if ("CONFIRMED".equals(status)) {
                confirmed += cnt;
            } else if ("UNDER_REVIEW".equals(status) || "MODIFIED".equals(status)) {
                pendingReview += cnt;
            } else if ("APPROVED".equals(status)) {
                approved += cnt;
            }
        }
        batch.setTotalCount(total);
        batch.setConfirmedCount(confirmed);
        batch.setPendingReviewCount(pendingReview);
        batch.setApprovedCount(approved);
        return batch;
    }

    @Override
    public List<ScVerificationRecord> selectRecordList(ScVerificationRecord query) {
        return recordMapper.selectScVerificationRecordList(query);
    }

    @Override
    public ScVerificationRecord selectRecordDetail(Long recordId) {
        ScVerificationRecord record = recordMapper.selectScVerificationRecordByRecordId(recordId);
        if (record != null) {
            record.setChanges(changeMapper.selectByRecordId(recordId));
        }
        return record;
    }

    /* ---- 学生端 ---- */

    @Override
    public List<ScVerificationBatch> selectActiveBatchesForStudent(Long userId) {
        ScUserProfile profile = userProfileMapper.selectScUserProfileByUserId(userId);
        Long gradeId = profile != null ? profile.getGradeId() : null;
        Long classId = profile != null ? profile.getClassId() : null;
        return batchMapper.selectActiveBatchesForStudent(userId, gradeId, classId);
    }

    @Override
    public ScVerificationRecord getStudentVerificationInfo(Long batchId, Long userId) {
        ScVerificationRecord record = recordMapper.selectByBatchAndStudent(batchId, userId);
        if (record == null) {
            throw new ServiceException("您不在此批次的核对范围内");
        }
        // 填充已提交的变更
        record.setChanges(changeMapper.selectByRecordId(record.getRecordId()));
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmNoChange(Long recordId, Long userId) {
        ScVerificationRecord record = recordMapper.selectScVerificationRecordByRecordId(recordId);
        if (record == null) {
            throw new ServiceException("记录不存在");
        }
        if (!record.getStudentUserId().equals(userId)) {
            throw new ServiceException("无权操作此记录");
        }
        if (!"PENDING".equals(record.getRecordStatus())) {
            throw new ServiceException("当前状态不允许确认");
        }
        ScVerificationRecord update = new ScVerificationRecord();
        update.setRecordId(recordId);
        update.setRecordStatus("CONFIRMED");
        update.setSubmitTime(new Date());
        update.setUpdateBy(String.valueOf(userId));
        return recordMapper.updateScVerificationRecord(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitChanges(VerificationSubmitDto dto, Long userId) {
        ScVerificationRecord record = recordMapper.selectScVerificationRecordByRecordId(dto.getRecordId());
        if (record == null) {
            throw new ServiceException("记录不存在");
        }
        if (!record.getStudentUserId().equals(userId)) {
            throw new ServiceException("无权操作此记录");
        }
        if (!"PENDING".equals(record.getRecordStatus()) && !"REJECTED".equals(record.getRecordStatus())) {
            throw new ServiceException("当前状态不允许提交修改");
        }

        ScVerificationBatch batch = batchMapper.selectScVerificationBatchByBatchId(dto.getBatchId());
        if (batch == null || !"ACTIVE".equals(batch.getBatchStatus())) {
            throw new ServiceException("批次不存在或未激活");
        }

        // 获取可编辑字段
        List<String> editableFields = JSON.parseArray(batch.getEditableFields(), String.class);

        // 获取当前档案并生成快照
        ScUserProfile profile = userProfileMapper.selectScUserProfileByUserId(userId);
        if (profile == null) {
            throw new ServiceException("学生档案不存在");
        }

        // 删除之前的变更（如重新提交的情况）
        changeMapper.deleteScVerificationChangeByRecordId(dto.getRecordId());

        // 创建变更记录
        List<ScVerificationChange> changes = new ArrayList<>();
        for (VerificationSubmitDto.ChangeItem item : dto.getChanges()) {
            if (!editableFields.contains(item.getFieldName())) {
                throw new ServiceException("字段 " + item.getFieldName() + " 不在可编辑范围内");
            }
            String oldValue = getProfileFieldValue(profile, item.getFieldName());
            ScVerificationChange change = new ScVerificationChange();
            change.setRecordId(dto.getRecordId());
            change.setBatchId(dto.getBatchId());
            change.setStudentUserId(userId);
            change.setFieldName(item.getFieldName());
            change.setFieldLabel(FIELD_LABEL_MAP.getOrDefault(item.getFieldName(), item.getFieldName()));
            change.setOldValue(oldValue);
            change.setNewValue(item.getNewValue());
            change.setChangeStatus("PENDING");
            changes.add(change);
        }

        if (!changes.isEmpty()) {
            changeMapper.batchInsertChanges(changes);
        }

        // 更新记录状态
        ScVerificationRecord update = new ScVerificationRecord();
        update.setRecordId(dto.getRecordId());
        update.setRecordStatus("UNDER_REVIEW");
        update.setSubmitTime(new Date());
        update.setSnapshotJson(JSON.toJSONString(profile));
        update.setUpdateBy(String.valueOf(userId));
        return recordMapper.updateScVerificationRecord(update);
    }

    @Override
    public List<ScVerificationRecord> selectStudentRecords(Long userId) {
        ScVerificationRecord query = new ScVerificationRecord();
        query.setStudentUserId(userId);
        return recordMapper.selectScVerificationRecordList(query);
    }

    /* ---- 审核 ---- */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reviewRecord(VerificationReviewDto dto, String operator) {
        ScVerificationRecord record = recordMapper.selectScVerificationRecordByRecordId(dto.getRecordId());
        if (record == null) {
            throw new ServiceException("记录不存在");
        }
        if (!"UNDER_REVIEW".equals(record.getRecordStatus()) && !"MODIFIED".equals(record.getRecordStatus())) {
            throw new ServiceException("当前状态不允许审核");
        }

        boolean approve = "APPROVE".equalsIgnoreCase(dto.getActionType());

        // 更新所有变更明细状态
        String changeStatus = approve ? "APPROVED" : "REJECTED";
        changeMapper.batchUpdateChangeStatus(record.getRecordId(), changeStatus, dto.getReviewComment());

        // 如果通过，将变更写入学籍档案
        if (approve) {
            List<ScVerificationChange> changes = changeMapper.selectByRecordId(record.getRecordId());
            ScUserProfile profileUpdate = new ScUserProfile();
            profileUpdate.setUserId(record.getStudentUserId());
            profileUpdate.setUpdateBy(operator);
            for (ScVerificationChange change : changes) {
                setProfileFieldValue(profileUpdate, change.getFieldName(), change.getNewValue());
            }
            scUserProfileService.updateScUserProfile(profileUpdate);
        }

        // 更新记录状态
        ScVerificationRecord update = new ScVerificationRecord();
        update.setRecordId(dto.getRecordId());
        update.setRecordStatus(approve ? "APPROVED" : "REJECTED");
        update.setReviewUserId(SecurityUtils.getUserId());
        update.setReviewTime(new Date());
        update.setReviewComment(dto.getReviewComment());
        update.setUpdateBy(operator);
        return recordMapper.updateScVerificationRecord(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchReviewRecords(VerificationBatchReviewDto dto, String operator) {
        int rows = 0;
        for (Long recordId : dto.getRecordIds()) {
            VerificationReviewDto reviewDto = new VerificationReviewDto();
            reviewDto.setRecordId(recordId);
            reviewDto.setActionType(dto.getActionType());
            reviewDto.setReviewComment(dto.getReviewComment());
            try {
                rows += reviewRecord(reviewDto, operator);
            } catch (ServiceException e) {
                // 跳过不符合审核条件的记录，继续处理其他记录
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int adminBatchModify(VerificationBatchModifyDto dto, String operator) {
        ScVerificationBatch batch = batchMapper.selectScVerificationBatchByBatchId(dto.getBatchId());
        if (batch == null) {
            throw new ServiceException("批次不存在");
        }

        List<String> editableFields = JSON.parseArray(batch.getEditableFields(), String.class);
        if (!editableFields.contains(dto.getFieldName())) {
            throw new ServiceException("字段 " + dto.getFieldName() + " 不在可编辑范围内");
        }

        int rows = 0;
        for (Long studentUserId : dto.getStudentUserIds()) {
            ScUserProfile profile = userProfileMapper.selectScUserProfileByUserId(studentUserId);
            if (profile == null) {
                continue;
            }
            String oldValue = getProfileFieldValue(profile, dto.getFieldName());

            // 直接更新档案
            ScUserProfile profileUpdate = new ScUserProfile();
            profileUpdate.setUserId(studentUserId);
            profileUpdate.setUpdateBy(operator);
            setProfileFieldValue(profileUpdate, dto.getFieldName(), dto.getNewValue());
            scUserProfileService.updateScUserProfile(profileUpdate);

            // 记录变更（管理员直接修改，状态直接APPROVED）
            ScVerificationRecord record = recordMapper.selectByBatchAndStudent(dto.getBatchId(), studentUserId);
            if (record != null) {
                ScVerificationChange change = new ScVerificationChange();
                change.setRecordId(record.getRecordId());
                change.setBatchId(dto.getBatchId());
                change.setStudentUserId(studentUserId);
                change.setFieldName(dto.getFieldName());
                change.setFieldLabel(FIELD_LABEL_MAP.getOrDefault(dto.getFieldName(), dto.getFieldName()));
                change.setOldValue(oldValue);
                change.setNewValue(dto.getNewValue());
                change.setChangeStatus("APPROVED");
                changeMapper.insertScVerificationChange(change);

                // 更新记录状态为已通过
                ScVerificationRecord recordUpdate = new ScVerificationRecord();
                recordUpdate.setRecordId(record.getRecordId());
                recordUpdate.setRecordStatus("APPROVED");
                recordUpdate.setReviewUserId(SecurityUtils.getUserId());
                recordUpdate.setReviewTime(new Date());
                recordUpdate.setReviewComment("管理员批量修改");
                recordUpdate.setUpdateBy(operator);
                recordMapper.updateScVerificationRecord(recordUpdate);
            }
            rows++;
        }
        return rows;
    }

    /* ---- 辅助方法 ---- */

    /**
     * 从学生档案中获取指定字段的值
     */
    private String getProfileFieldValue(ScUserProfile profile, String fieldName) {
        if (profile == null || fieldName == null) {
            return "";
        }
        switch (fieldName) {
            case "realName":       return StringUtils.defaultString(profile.getRealName());
            case "gender":         return StringUtils.defaultString(profile.getGender());
            case "avatarUrl":      return StringUtils.defaultString(profile.getAvatarUrl());
            case "major":          return StringUtils.defaultString(profile.getMajor());
            case "admissionYear":  return profile.getAdmissionYear() != null ? String.valueOf(profile.getAdmissionYear()) : "";
            case "interestTags":   return StringUtils.defaultString(profile.getInterestTags());
            case "learningGoal":   return StringUtils.defaultString(profile.getLearningGoal());
            case "learningStyle":  return StringUtils.defaultString(profile.getLearningStyle());
            case "studentNo":      return StringUtils.defaultString(profile.getStudentNo());
            default:               return "";
        }
    }

    /**
     * 设置学生档案中指定字段的值
     */
    private void setProfileFieldValue(ScUserProfile profile, String fieldName, String value) {
        if (profile == null || fieldName == null) {
            return;
        }
        switch (fieldName) {
            case "realName":       profile.setRealName(value); break;
            case "gender":         profile.setGender(value); break;
            case "avatarUrl":      profile.setAvatarUrl(value); break;
            case "major":          profile.setMajor(value); break;
            case "admissionYear":  if (StringUtils.isNotEmpty(value)) { profile.setAdmissionYear(Integer.parseInt(value)); } break;
            case "interestTags":   profile.setInterestTags(value); break;
            case "learningGoal":   profile.setLearningGoal(value); break;
            case "learningStyle":  profile.setLearningStyle(value); break;
            case "studentNo":      profile.setStudentNo(value); break;
            default: break;
        }
    }
}
