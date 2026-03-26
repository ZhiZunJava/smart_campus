package com.smart.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.CourseStudentBatchAddDto;
import com.smart.system.mapper.ScCourseStudentMapper;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScCourseStudentService;
import com.smart.system.service.IScUserProfileService;

@Service
public class ScCourseStudentServiceImpl implements IScCourseStudentService {
    @Autowired
    private ScCourseStudentMapper scCourseStudentMapper;

    @Autowired
    private IScUserProfileService scUserProfileService;

    @Autowired
    private IScClassCourseService scClassCourseService;

    @Override
    public ScCourseStudent selectScCourseStudentById(Long id) {
        return scCourseStudentMapper.selectScCourseStudentById(id);
    }

    @Override
    public List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent) {
        return scCourseStudentMapper.selectScCourseStudentList(scCourseStudent);
    }

    @Override
    public Map<String, Object> checkDuplicate(ScCourseStudent scCourseStudent) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("duplicate", false);
        if (scCourseStudent == null || scCourseStudent.getStudentUserId() == null) {
            return result;
        }
        fillClassCourseSnapshot(scCourseStudent);
        if (scCourseStudent.getClassCourseId() == null && scCourseStudent.getCourseId() == null) {
            return result;
        }
        ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(scCourseStudent);
        if (duplicate != null) {
            result.put("duplicate", true);
            result.put("id", duplicate.getId());
            result.put("classCourseId", duplicate.getClassCourseId());
            result.put("teachingClassCode", duplicate.getTeachingClassCode());
            result.put("courseId", duplicate.getCourseId());
            result.put("courseName", duplicate.getCourseName());
            result.put("studentUserId", duplicate.getStudentUserId());
            result.put("studentName", duplicate.getStudentName());
            result.put("studentNo", duplicate.getStudentNo());
            result.put("classId", duplicate.getClassId());
            result.put("className", duplicate.getClassName());
            result.put("termId", duplicate.getTermId());
            result.put("termName", duplicate.getTermName());
            result.put("teacherId", duplicate.getTeacherId());
            result.put("teacherName", duplicate.getTeacherName());
        }
        return result;
    }

    @Override
    public Map<String, Object> batchAddCourseStudents(CourseStudentBatchAddDto batchDto) {
        if (batchDto == null || batchDto.getStudentUserIds() == null || batchDto.getStudentUserIds().length == 0) {
            throw new ServiceException("请先选择学生");
        }
        ScClassCourse classCourse = requireBatchClassCourse(batchDto);
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> skipped = new ArrayList<>();
        int successCount = 0;
        for (Long studentUserId : Arrays.stream(batchDto.getStudentUserIds()).filter(Objects::nonNull).toList()) {
            ScCourseStudent item = new ScCourseStudent();
            item.setClassCourseId(classCourse.getId());
            item.setCourseId(classCourse.getCourseId());
            item.setStudentUserId(studentUserId);
            item.setClassId(classCourse.getClassId());
            item.setStatus(StringUtils.defaultIfEmpty(batchDto.getStatus(), "0"));
            item.setRemark(batchDto.getRemark());
            fillDefaultClass(item);
            ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(item);
            if (duplicate != null) {
                Map<String, Object> duplicateItem = new LinkedHashMap<>();
                duplicateItem.put("studentUserId", duplicate.getStudentUserId());
                duplicateItem.put("studentName", duplicate.getStudentName());
                duplicateItem.put("studentNo", duplicate.getStudentNo());
                duplicateItem.put("courseName", duplicate.getCourseName());
                duplicateItem.put("className", duplicate.getClassName());
                duplicateItem.put("teachingClassCode", duplicate.getTeachingClassCode());
                duplicateItem.put("termName", duplicate.getTermName());
                skipped.add(duplicateItem);
                continue;
            }
            validateSelectionGroupLimit(studentUserId, classCourse, null);
            validateCapacity(classCourse, null);
            if (item.getJoinTime() == null) {
                item.setJoinTime(DateUtils.getNowDate());
            }
            scCourseStudentMapper.insertScCourseStudent(item);
            successCount++;
        }
        result.put("successCount", successCount);
        result.put("skippedCount", skipped.size());
        result.put("skippedStudents", skipped);
        return result;
    }

    @Override
    public int countActiveByClassCourseId(Long classCourseId) {
        if (classCourseId == null) {
            return 0;
        }
        return scCourseStudentMapper.countActiveByClassCourseId(classCourseId);
    }

    @Override
    public ScCourseStudent findActiveSelection(Long studentUserId, Long classCourseId) {
        if (studentUserId == null || classCourseId == null) {
            return null;
        }
        ScCourseStudent query = new ScCourseStudent();
        query.setStudentUserId(studentUserId);
        query.setClassCourseId(classCourseId);
        query.setStatus("0");
        return scCourseStudentMapper.selectDuplicateScCourseStudent(query);
    }

    @Override
    public List<ScCourseStudent> listStudentCourseSelections(Long studentUserId, Long termId, boolean autoFillRequired) {
        if (studentUserId == null) {
            return Collections.emptyList();
        }
        if (autoFillRequired) {
            ensureRequiredSelections(studentUserId, termId, "system");
        }
        ScCourseStudent query = new ScCourseStudent();
        query.setStudentUserId(studentUserId);
        query.setTermId(termId);
        query.setStatus("0");
        return scCourseStudentMapper.selectScCourseStudentList(query);
    }

    @Override
    public List<ScClassCourse> listAvailableClassCoursesForStudent(Long studentUserId, Long termId) {
        ScUserProfile profile = requireStudentProfile(studentUserId);
        ScClassCourse query = new ScClassCourse();
        query.setTermId(termId);
        query.setStatus("0");
        return scClassCourseService.selectScClassCourseList(query).stream()
                .filter(item -> isSelectableForStudent(profile, item))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> selectCourse(Long studentUserId, Long classCourseId, String operator) {
        ScUserProfile profile = requireStudentProfile(studentUserId);
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        if (!isSelectableForStudent(profile, classCourse)) {
            throw new ServiceException("当前教学班不在可选范围内");
        }
        ScCourseStudent existing = findActiveSelection(studentUserId, classCourseId);
        if (existing != null) {
            return buildSelectionResult(existing, false, "已在选课名单中");
        }
        validateSelectionGroupLimit(studentUserId, classCourse, null);
        validateCapacity(classCourse, null);
        ScCourseStudent item = buildSelection(studentUserId, classCourse);
        item.setCreateBy(operator);
        scCourseStudentMapper.insertScCourseStudent(item);
        return buildSelectionResult(item, true, "选课成功");
    }

    @Override
    public Map<String, Object> cancelCourse(Long studentUserId, Long classCourseId, String operator) {
        ScUserProfile profile = requireStudentProfile(studentUserId);
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        if (Objects.equals(profile.getClassId(), classCourse.getClassId())
                && "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"))) {
            throw new ServiceException("本班必修课不支持退选");
        }
        ScCourseStudent existing = findActiveSelection(studentUserId, classCourseId);
        if (existing == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("classCourseId", classCourseId);
            result.put("studentUserId", studentUserId);
            result.put("dropped", false);
            result.put("message", "当前课程未选，无需退课");
            result.put("updateBy", operator);
            return result;
        }
        scCourseStudentMapper.deleteScCourseStudentById(existing.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classCourseId", classCourseId);
        result.put("studentUserId", studentUserId);
        result.put("dropped", true);
        result.put("message", "退课成功");
        result.put("updateBy", operator);
        return result;
    }

    @Override
    public Map<String, Object> selectCourseByApproval(Long studentUserId, Long classCourseId, String operator) {
        requireStudentProfile(studentUserId);
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        ScCourseStudent existing = findActiveSelection(studentUserId, classCourseId);
        if (existing != null) {
            return buildSelectionResult(existing, false, "已在选课名单中");
        }
        validateSelectionGroupLimit(studentUserId, classCourse, null);
        validateCapacity(classCourse, null);
        ScCourseStudent item = buildSelection(studentUserId, classCourse);
        item.setCreateBy(operator);
        scCourseStudentMapper.insertScCourseStudent(item);
        return buildSelectionResult(item, true, "审核通过，已加入教学班");
    }

    @Override
    public Map<String, Object> cancelCourseByApproval(Long studentUserId, Long classCourseId, String operator) {
        ScCourseStudent existing = findActiveSelection(studentUserId, classCourseId);
        if (existing == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("classCourseId", classCourseId);
            result.put("studentUserId", studentUserId);
            result.put("dropped", false);
            result.put("message", "当前课程未选，无需退课");
            result.put("updateBy", operator);
            return result;
        }
        scCourseStudentMapper.deleteScCourseStudentById(existing.getId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classCourseId", classCourseId);
        result.put("studentUserId", studentUserId);
        result.put("dropped", true);
        result.put("message", "审核通过，已退课");
        result.put("updateBy", operator);
        return result;
    }

    @Override
    public List<ScCourseStudent> listTeacherCourseStudents(Long teacherId, Long classCourseId, Long termId) {
        if (teacherId == null) {
            return Collections.emptyList();
        }
        ScCourseStudent query = new ScCourseStudent();
        query.setTeacherId(teacherId);
        query.setClassCourseId(classCourseId);
        query.setTermId(termId);
        query.setStatus("0");
        return scCourseStudentMapper.selectScCourseStudentList(query);
    }

    @Override
    public int insertScCourseStudent(ScCourseStudent scCourseStudent) {
        ScClassCourse classCourse = fillClassCourseSnapshot(scCourseStudent);
        fillDefaultClass(scCourseStudent);
        validateDuplicate(scCourseStudent);
        validateSelectionGroupLimit(scCourseStudent.getStudentUserId(), classCourse, null);
        validateCapacity(classCourse, null);
        if (scCourseStudent.getJoinTime() == null) {
            scCourseStudent.setJoinTime(DateUtils.getNowDate());
        }
        return scCourseStudentMapper.insertScCourseStudent(scCourseStudent);
    }

    @Override
    public int updateScCourseStudent(ScCourseStudent scCourseStudent) {
        ScClassCourse classCourse = fillClassCourseSnapshot(scCourseStudent);
        fillDefaultClass(scCourseStudent);
        validateDuplicate(scCourseStudent);
        ScCourseStudent existing = scCourseStudent.getId() == null ? null : selectScCourseStudentById(scCourseStudent.getId());
        Long excludeClassCourseId = existing == null || !"0".equals(StringUtils.defaultIfEmpty(existing.getStatus(), "0"))
                ? null
                : existing.getClassCourseId();
        validateSelectionGroupLimit(scCourseStudent.getStudentUserId(), classCourse, excludeClassCourseId);
        validateCapacity(classCourse, scCourseStudent.getId());
        return scCourseStudentMapper.updateScCourseStudent(scCourseStudent);
    }

    @Override
    public int deleteScCourseStudentByIds(Long[] ids) {
        return scCourseStudentMapper.deleteScCourseStudentByIds(ids);
    }

    @Override
    public int deleteScCourseStudentById(Long id) {
        return scCourseStudentMapper.deleteScCourseStudentById(id);
    }

    private void ensureRequiredSelections(Long studentUserId, Long termId, String operator) {
        ScUserProfile profile = requireStudentProfile(studentUserId);
        if (profile.getClassId() == null) {
            return;
        }
        ScClassCourse query = new ScClassCourse();
        query.setClassId(profile.getClassId());
        query.setTermId(termId);
        query.setStatus("0");
        List<ScClassCourse> requiredCourses = scClassCourseService.selectScClassCourseList(query).stream()
                .filter(item -> "Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getRequiredFlag(), "N")))
                .collect(Collectors.toList());
        for (ScClassCourse classCourse : requiredCourses) {
            if (classCourse == null || classCourse.getId() == null) {
                continue;
            }
            if (findActiveSelection(studentUserId, classCourse.getId()) != null) {
                continue;
            }
            ScCourseStudent item = buildSelection(studentUserId, classCourse);
            item.setCreateBy(operator);
            scCourseStudentMapper.insertScCourseStudent(item);
        }
    }

    private Map<String, Object> buildSelectionResult(ScCourseStudent item, boolean created, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", item.getId());
        result.put("classCourseId", item.getClassCourseId());
        result.put("courseId", item.getCourseId());
        result.put("classId", item.getClassId());
        result.put("studentUserId", item.getStudentUserId());
        result.put("created", created);
        result.put("message", message);
        return result;
    }

    private ScCourseStudent buildSelection(Long studentUserId, ScClassCourse classCourse) {
        ScCourseStudent item = new ScCourseStudent();
        item.setStudentUserId(studentUserId);
        item.setClassCourseId(classCourse.getId());
        item.setCourseId(classCourse.getCourseId());
        item.setClassId(classCourse.getClassId());
        item.setStatus("0");
        item.setJoinTime(DateUtils.getNowDate());
        item.setRemark("");
        return item;
    }

    private ScClassCourse requireBatchClassCourse(CourseStudentBatchAddDto batchDto) {
        if (batchDto == null) {
            throw new ServiceException("批量选课参数不能为空");
        }
        if (batchDto.getClassCourseId() != null) {
            return requireClassCourse(batchDto.getClassCourseId());
        }
        if (batchDto.getCourseId() == null || batchDto.getClassId() == null) {
            throw new ServiceException("请先选择教学班");
        }
        ScClassCourse query = new ScClassCourse();
        query.setCourseId(batchDto.getCourseId());
        query.setClassId(batchDto.getClassId());
        query.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
        if (classCourses.isEmpty()) {
            throw new ServiceException("未找到匹配的教学班，请先维护班级课程");
        }
        if (classCourses.size() > 1) {
            throw new ServiceException("检测到多个学期的教学班，请改为按教学班操作选课");
        }
        return classCourses.get(0);
    }

    private ScClassCourse fillClassCourseSnapshot(ScCourseStudent scCourseStudent) {
        if (scCourseStudent == null) {
            return null;
        }
        ScClassCourse classCourse = null;
        if (scCourseStudent.getClassCourseId() != null) {
            classCourse = requireClassCourse(scCourseStudent.getClassCourseId());
        } else if (scCourseStudent.getCourseId() != null && scCourseStudent.getClassId() != null) {
            ScClassCourse query = new ScClassCourse();
            query.setCourseId(scCourseStudent.getCourseId());
            query.setClassId(scCourseStudent.getClassId());
            query.setStatus("0");
            List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(query);
            if (classCourses.size() > 1) {
                throw new ServiceException("当前课程在多个学期存在教学班，请改为选择具体教学班");
            }
            if (!classCourses.isEmpty()) {
                classCourse = classCourses.get(0);
            }
        }
        if (classCourse != null) {
            scCourseStudent.setClassCourseId(classCourse.getId());
            scCourseStudent.setCourseId(classCourse.getCourseId());
            scCourseStudent.setClassId(classCourse.getClassId());
        }
        return classCourse;
    }

    private void fillDefaultClass(ScCourseStudent scCourseStudent) {
        if (scCourseStudent == null || scCourseStudent.getStudentUserId() == null
                || scCourseStudent.getClassId() != null) {
            return;
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(scCourseStudent.getStudentUserId());
        if (profile != null && profile.getClassId() != null) {
            scCourseStudent.setClassId(profile.getClassId());
        }
    }

    private void validateCapacity(ScClassCourse classCourse, Long excludeId) {
        if (classCourse == null || classCourse.getId() == null || classCourse.getStudentLimit() == null
                || classCourse.getStudentLimit() <= 0) {
            return;
        }
        int currentCount = countActiveByClassCourseId(classCourse.getId());
        if (excludeId != null) {
            ScCourseStudent existing = selectScCourseStudentById(excludeId);
            if (existing != null && Objects.equals(existing.getClassCourseId(), classCourse.getId())
                    && "0".equals(existing.getStatus())) {
                currentCount = Math.max(0, currentCount - 1);
            }
        }
        if (currentCount >= classCourse.getStudentLimit()) {
            throw new ServiceException("当前教学班已满员，无法继续选课");
        }
    }

    private void validateSelectionGroupLimit(Long studentUserId, ScClassCourse classCourse, Long excludeClassCourseId) {
        if (studentUserId == null || classCourse == null || classCourse.getTermId() == null) {
            return;
        }
        String groupCode = StringUtils.trimToEmpty(classCourse.getSelectionGroupCode());
        if (StringUtils.isEmpty(groupCode)) {
            return;
        }
        int limit = classCourse.getSelectionGroupLimit() == null || classCourse.getSelectionGroupLimit() <= 0
                ? 1
                : classCourse.getSelectionGroupLimit();
        List<ScClassCourse> selectedGroupCourses = listStudentSelectedGroupCourses(studentUserId, classCourse.getTermId(),
                groupCode, excludeClassCourseId);
        if (selectedGroupCourses.size() < limit) {
            return;
        }
        String selectedNames = selectedGroupCourses.stream()
                .map(this::resolveClassCourseDisplayName)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.joining("、"));
        String groupName = StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(classCourse.getSelectionGroupName()), groupCode);
        throw new ServiceException("专项分组【" + groupName + "】最多可选 " + limit + " 门，当前已选《"
                + StringUtils.defaultIfEmpty(selectedNames, "同组课程") + "》");
    }

    private List<ScClassCourse> listStudentSelectedGroupCourses(Long studentUserId, Long termId, String groupCode,
            Long excludeClassCourseId) {
        if (studentUserId == null || termId == null || StringUtils.isEmpty(groupCode)) {
            return Collections.emptyList();
        }
        ScCourseStudent query = new ScCourseStudent();
        query.setStudentUserId(studentUserId);
        query.setTermId(termId);
        query.setStatus("0");
        List<ScCourseStudent> selections = scCourseStudentMapper.selectScCourseStudentList(query);
        List<ScClassCourse> result = new ArrayList<>();
        for (ScCourseStudent selection : selections) {
            if (selection == null || selection.getClassCourseId() == null
                    || Objects.equals(selection.getClassCourseId(), excludeClassCourseId)) {
                continue;
            }
            ScClassCourse selectedCourse = scClassCourseService.selectScClassCourseById(selection.getClassCourseId());
            if (selectedCourse == null || !Objects.equals(selectedCourse.getTermId(), termId)) {
                continue;
            }
            if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(selectedCourse.getSelectionGroupCode()), groupCode)) {
                continue;
            }
            result.add(selectedCourse);
        }
        return result;
    }

    private String resolveClassCourseDisplayName(ScClassCourse classCourse) {
        if (classCourse == null) {
            return "";
        }
        return StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(classCourse.getSelectionOptionName()),
                classCourse.getCourseName());
    }

    private void validateDuplicate(ScCourseStudent scCourseStudent) {
        ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(scCourseStudent);
        if (duplicate == null) {
            return;
        }
        String studentLabel = StringUtils.defaultIfEmpty(duplicate.getStudentName(),
                String.valueOf(duplicate.getStudentUserId()));
        String courseLabel = StringUtils.defaultIfEmpty(duplicate.getCourseName(),
                String.valueOf(duplicate.getCourseId()));
        throw new ServiceException("选课关系重复：" + studentLabel + " 已经选过课程《" + courseLabel + "》");
    }

    private ScUserProfile requireStudentProfile(Long studentUserId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(studentUserId);
        if (profile == null) {
            throw new ServiceException("学生档案不存在");
        }
        return profile;
    }

    private ScClassCourse requireClassCourse(Long classCourseId) {
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null) {
            throw new ServiceException("教学班不存在");
        }
        if (!"0".equals(StringUtils.defaultIfEmpty(classCourse.getStatus(), "0"))) {
            throw new ServiceException("教学班已停用，暂不支持选课");
        }
        return classCourse;
    }

    private boolean isSelectableForStudent(ScUserProfile profile, ScClassCourse classCourse) {
        if (profile == null || classCourse == null || classCourse.getId() == null) {
            return false;
        }
        if (Objects.equals(profile.getClassId(), classCourse.getClassId())) {
            return true;
        }
        if ("Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"))) {
            return false;
        }
        String classCourseMajor = StringUtils.trimToEmpty(classCourse.getMajor());
        String studentMajor = StringUtils.trimToEmpty(profile.getMajor());
        if (StringUtils.isEmpty(classCourseMajor) || StringUtils.isEmpty(studentMajor)) {
            return true;
        }
        return StringUtils.equalsIgnoreCase(classCourseMajor, studentMajor);
    }
}
