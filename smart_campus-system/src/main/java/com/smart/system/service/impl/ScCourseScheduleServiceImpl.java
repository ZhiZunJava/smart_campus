package com.smart.system.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.campusvo.CourseScheduleAutoArrangeVo;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto;
import com.smart.system.mapper.ScClassCourseMapper;
import com.smart.system.mapper.ScClassroomMapper;
import com.smart.system.mapper.ScCourseScheduleMapper;
import com.smart.system.mapper.ScSchoolTermMapper;
import com.smart.system.service.IScCourseScheduleService;

@Service
public class ScCourseScheduleServiceImpl implements IScCourseScheduleService {
    @Autowired
    private ScCourseScheduleMapper scCourseScheduleMapper;
    @Autowired
    private ScClassroomMapper scClassroomMapper;
    @Autowired
    private ScClassCourseMapper scClassCourseMapper;
    @Autowired
    private ScSchoolTermMapper scSchoolTermMapper;
    @Autowired
    private CourseScheduleAutoArrangeService courseScheduleAutoArrangeService;
    @Autowired
    private ObjectMapper objectMapper;

    public ScCourseSchedule selectScCourseScheduleByScheduleId(Long scheduleId) {
        return scCourseScheduleMapper.selectScCourseScheduleByScheduleId(scheduleId);
    }

    public List<ScCourseSchedule> selectScCourseScheduleList(ScCourseSchedule scCourseSchedule) {
        return scCourseScheduleMapper.selectScCourseScheduleList(scCourseSchedule);
    }

    public java.util.Map<String, Object> checkClassroomConflict(ScCourseSchedule scCourseSchedule) {
        fillClassroomSnapshot(scCourseSchedule);
        syncWeeksJson(scCourseSchedule);
        return findClassroomConflict(scCourseSchedule);
    }

    public java.util.Map<String, Object> checkScheduleConflict(ScCourseSchedule scCourseSchedule) {
        fillClassroomSnapshot(scCourseSchedule);
        syncWeeksJson(scCourseSchedule);
        return findScheduleConflict(scCourseSchedule);
    }

    @Transactional(rollbackFor = Exception.class)
    public CourseScheduleAutoArrangeVo autoArrangeCourseSchedule(CourseScheduleAutoArrangeDto request,
            String operator) {
        if (request == null || request.getTermId() == null) {
            throw new ServiceException("学期不能为空");
        }
        ScSchoolTerm term = scSchoolTermMapper.selectScSchoolTermByTermId(request.getTermId());
        if (term == null) {
            throw new ServiceException("未找到对应学期");
        }

        ScClassCourse classCourseQuery = new ScClassCourse();
        classCourseQuery.setTermId(request.getTermId());
        classCourseQuery.setStatus("0");
        List<ScClassCourse> classCourses = scClassCourseMapper.selectScClassCourseList(classCourseQuery);

        ScClassroom classroomQuery = new ScClassroom();
        classroomQuery.setStatus("0");
        List<ScClassroom> classrooms = scClassroomMapper.selectScClassroomList(classroomQuery);

        ScCourseSchedule existingQuery = new ScCourseSchedule();
        existingQuery.setTermId(request.getTermId());
        existingQuery.setStatus("0");
        List<ScCourseSchedule> existingSchedules = scCourseScheduleMapper.selectScCourseScheduleList(existingQuery);
        Set<Long> targetClassCourseIds = resolveTargetClassCourseIds(request, classCourses);
        List<ScCourseSchedule> targetExistingSchedules = existingSchedules.stream()
                .filter(item -> item != null && item.getClassCourseId() != null)
                .filter(item -> targetClassCourseIds.isEmpty() || targetClassCourseIds.contains(item.getClassCourseId()))
                .collect(Collectors.toList());

        CourseScheduleAutoArrangeVo result = courseScheduleAutoArrangeService.autoArrange(request, classCourses,
                classrooms, existingSchedules, term, operator);

        if (Boolean.TRUE.equals(request.getClearExistingSchedules()) && !targetExistingSchedules.isEmpty()) {
            Long[] scheduleIds = targetExistingSchedules.stream()
                    .map(ScCourseSchedule::getScheduleId)
                    .filter(java.util.Objects::nonNull)
                    .toArray(Long[]::new);
            if (scheduleIds.length > 0) {
                scCourseScheduleMapper.deleteScCourseScheduleByScheduleIds(scheduleIds);
            }
            result.setClearedSchedules(scheduleIds.length);
        }

        List<ScCourseSchedule> schedulesToInsert = courseScheduleAutoArrangeService
                .toSchedules(result.getArrangedLessons(), classCourses, classrooms, term, operator);
        for (ScCourseSchedule schedule : schedulesToInsert) {
            fillClassroomSnapshot(schedule);
            syncWeeksJson(schedule);
            validateScheduleConflict(schedule);
            scCourseScheduleMapper.insertScCourseSchedule(schedule);
        }
        result.setInsertedSchedules(schedulesToInsert.size());
        return result;
    }

    public int insertScCourseSchedule(ScCourseSchedule scCourseSchedule) {
        fillClassroomSnapshot(scCourseSchedule);
        syncWeeksJson(scCourseSchedule);
        validateScheduleConflict(scCourseSchedule);
        return scCourseScheduleMapper.insertScCourseSchedule(scCourseSchedule);
    }

    public int updateScCourseSchedule(ScCourseSchedule scCourseSchedule) {
        fillClassroomSnapshot(scCourseSchedule);
        syncWeeksJson(scCourseSchedule);
        validateScheduleConflict(scCourseSchedule);
        return scCourseScheduleMapper.updateScCourseSchedule(scCourseSchedule);
    }

    public int deleteScCourseScheduleByScheduleIds(Long[] scheduleIds) {
        return scCourseScheduleMapper.deleteScCourseScheduleByScheduleIds(scheduleIds);
    }

    public int deleteScCourseScheduleByScheduleId(Long scheduleId) {
        return scCourseScheduleMapper.deleteScCourseScheduleByScheduleId(scheduleId);
    }

    private void fillClassroomSnapshot(ScCourseSchedule scCourseSchedule) {
        if (scCourseSchedule.getClassroomId() == null) {
            if (StringUtils.isEmpty(scCourseSchedule.getClassroom())) {
                scCourseSchedule.setClassroom(null);
            }
            return;
        }
        ScClassroom classroom = scClassroomMapper.selectScClassroomByClassroomId(scCourseSchedule.getClassroomId());
        if (classroom != null) {
            scCourseSchedule.setClassroom(classroom.getClassroomName());
            scCourseSchedule.setClassroomName(classroom.getClassroomName());
            scCourseSchedule.setBuildingName(classroom.getBuildingName());
            scCourseSchedule.setCampusName(classroom.getCampusName());
        }
    }

    private void syncWeeksJson(ScCourseSchedule scCourseSchedule) {
        if (scCourseSchedule == null || StringUtils.isNotEmpty(scCourseSchedule.getWeeksJson())) {
            return;
        }
        Set<Integer> weeks = extractWeeks(scCourseSchedule.getWeeksText(), null);
        if (weeks.isEmpty()) {
            return;
        }
        try {
            scCourseSchedule.setWeeksJson(objectMapper.writeValueAsString(weeks));
        } catch (Exception ignored) {
        }
    }

    private void validateScheduleConflict(ScCourseSchedule scCourseSchedule) {
        java.util.Map<String, Object> conflict = findScheduleConflict(scCourseSchedule);
        if (Boolean.TRUE.equals(conflict.get("hasConflict"))) {
            String conflictType = String.valueOf(conflict.getOrDefault("conflictType", "classroom"));
            if ("teacher".equals(conflictType)) {
                throw new ServiceException("教师时间冲突：同一教师在相同星期、相同节次已存在排课");
            }
            if ("class".equals(conflictType)) {
                throw new ServiceException("班级时间冲突：同一班级在相同星期、相同节次已存在排课");
            }
            throw new ServiceException("教室时间冲突：同一教室在相同星期、相同节次已存在排课");
        }
    }

    private java.util.Map<String, Object> findScheduleConflict(ScCourseSchedule scCourseSchedule) {
        java.util.Map<String, Object> classroomConflict = findClassroomConflict(scCourseSchedule);
        if (Boolean.TRUE.equals(classroomConflict.get("hasConflict"))) {
            return classroomConflict;
        }
        java.util.Map<String, Object> teacherConflict = findTeacherConflict(scCourseSchedule);
        if (Boolean.TRUE.equals(teacherConflict.get("hasConflict"))) {
            return teacherConflict;
        }
        return findClassConflict(scCourseSchedule);
    }

    private java.util.Map<String, Object> findClassroomConflict(ScCourseSchedule scCourseSchedule) {
        java.util.Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasConflict", false);
        if (scCourseSchedule.getClassroomId() == null || scCourseSchedule.getTermId() == null
                || scCourseSchedule.getWeekDay() == null) {
            return result;
        }
        if (!"0".equals(StringUtils.isEmpty(scCourseSchedule.getStatus()) ? "0" : scCourseSchedule.getStatus())) {
            return result;
        }
        ScCourseSchedule query = new ScCourseSchedule();
        query.setClassroomId(scCourseSchedule.getClassroomId());
        query.setTermId(scCourseSchedule.getTermId());
        query.setWeekDay(scCourseSchedule.getWeekDay());
        query.setStatus("0");
        List<ScCourseSchedule> existingList = scCourseScheduleMapper.selectScCourseScheduleList(query);
        for (ScCourseSchedule existing : existingList) {
            if (scCourseSchedule.getScheduleId() != null
                    && scCourseSchedule.getScheduleId().equals(existing.getScheduleId())) {
                continue;
            }
            if (!isSectionOverlap(scCourseSchedule, existing)) {
                continue;
            }
            if (!isWeekOverlap(scCourseSchedule.getWeeksText(), scCourseSchedule.getWeeksJson(),
                    existing.getWeeksText(), existing.getWeeksJson())) {
                continue;
            }
            result.put("hasConflict", true);
            result.put("conflictType", "classroom");
            result.put("conflictScheduleId", existing.getScheduleId());
            result.put("classroomId", existing.getClassroomId());
            result.put("classroomName",
                    StringUtils.defaultIfEmpty(existing.getClassroomName(), existing.getClassroom()));
            result.put("buildingName", existing.getBuildingName());
            result.put("campusName", existing.getCampusName());
            result.put("weekDay", existing.getWeekDay());
            result.put("startSection", existing.getStartSection());
            result.put("endSection", existing.getEndSection());
            result.put("weeksText", existing.getWeeksText());
            break;
        }
        return result;
    }

    private java.util.Map<String, Object> findTeacherConflict(ScCourseSchedule scCourseSchedule) {
        java.util.Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasConflict", false);
        if (scCourseSchedule.getClassCourseId() == null || scCourseSchedule.getTermId() == null
                || scCourseSchedule.getWeekDay() == null) {
            return result;
        }
        if (!"0".equals(StringUtils.isEmpty(scCourseSchedule.getStatus()) ? "0" : scCourseSchedule.getStatus())) {
            return result;
        }
        ScClassCourse currentClassCourse = scClassCourseMapper
                .selectScClassCourseById(scCourseSchedule.getClassCourseId());
        if (currentClassCourse == null || currentClassCourse.getTeacherId() == null) {
            return result;
        }
        ScCourseSchedule query = new ScCourseSchedule();
        query.setTermId(scCourseSchedule.getTermId());
        query.setWeekDay(scCourseSchedule.getWeekDay());
        query.setStatus("0");
        List<ScCourseSchedule> existingList = scCourseScheduleMapper.selectScCourseScheduleList(query);
        for (ScCourseSchedule existing : existingList) {
            if (scCourseSchedule.getScheduleId() != null
                    && scCourseSchedule.getScheduleId().equals(existing.getScheduleId())) {
                continue;
            }
            if (!isSectionOverlap(scCourseSchedule, existing)) {
                continue;
            }
            if (!isWeekOverlap(scCourseSchedule.getWeeksText(), scCourseSchedule.getWeeksJson(),
                    existing.getWeeksText(), existing.getWeeksJson())) {
                continue;
            }
            ScClassCourse existingClassCourse = scClassCourseMapper
                    .selectScClassCourseById(existing.getClassCourseId());
            if (existingClassCourse == null || existingClassCourse.getTeacherId() == null) {
                continue;
            }
            if (!currentClassCourse.getTeacherId().equals(existingClassCourse.getTeacherId())) {
                continue;
            }
            result.put("hasConflict", true);
            result.put("conflictType", "teacher");
            result.put("conflictScheduleId", existing.getScheduleId());
            result.put("teacherId", existingClassCourse.getTeacherId());
            result.put("teacherName", existingClassCourse.getTeacherName());
            result.put("classCourseId", existing.getClassCourseId());
            result.put("classCourseName", StringUtils.defaultIfEmpty(existingClassCourse.getCourseName(),
                    String.valueOf(existing.getClassCourseId())));
            result.put("className", existingClassCourse.getClassName());
            result.put("weekDay", existing.getWeekDay());
            result.put("startSection", existing.getStartSection());
            result.put("endSection", existing.getEndSection());
            result.put("weeksText", existing.getWeeksText());
            result.put("classroomName",
                    StringUtils.defaultIfEmpty(existing.getClassroomName(), existing.getClassroom()));
            result.put("buildingName", existing.getBuildingName());
            result.put("campusName", existing.getCampusName());
            break;
        }
        return result;
    }

    private java.util.Map<String, Object> findClassConflict(ScCourseSchedule scCourseSchedule) {
        java.util.Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasConflict", false);
        if (scCourseSchedule.getClassCourseId() == null || scCourseSchedule.getTermId() == null
                || scCourseSchedule.getWeekDay() == null) {
            return result;
        }
        if (!"0".equals(StringUtils.isEmpty(scCourseSchedule.getStatus()) ? "0" : scCourseSchedule.getStatus())) {
            return result;
        }
        ScClassCourse currentClassCourse = scClassCourseMapper
                .selectScClassCourseById(scCourseSchedule.getClassCourseId());
        if (currentClassCourse == null || currentClassCourse.getClassId() == null) {
            return result;
        }
        ScCourseSchedule query = new ScCourseSchedule();
        query.setTermId(scCourseSchedule.getTermId());
        query.setWeekDay(scCourseSchedule.getWeekDay());
        query.setStatus("0");
        List<ScCourseSchedule> existingList = scCourseScheduleMapper.selectScCourseScheduleList(query);
        for (ScCourseSchedule existing : existingList) {
            if (scCourseSchedule.getScheduleId() != null
                    && scCourseSchedule.getScheduleId().equals(existing.getScheduleId())) {
                continue;
            }
            if (!isSectionOverlap(scCourseSchedule, existing)) {
                continue;
            }
            if (!isWeekOverlap(scCourseSchedule.getWeeksText(), scCourseSchedule.getWeeksJson(),
                    existing.getWeeksText(), existing.getWeeksJson())) {
                continue;
            }
            ScClassCourse existingClassCourse = scClassCourseMapper
                    .selectScClassCourseById(existing.getClassCourseId());
            if (existingClassCourse == null || existingClassCourse.getClassId() == null) {
                continue;
            }
            if (!currentClassCourse.getClassId().equals(existingClassCourse.getClassId())) {
                continue;
            }
            // 合班豁免：同 combinedClassCode + 同 courseId + 同 termId 的不同班级不视为班级冲突
            if (isSameCombinedClassGroup(currentClassCourse, existingClassCourse)) {
                continue;
            }
            // 选课组/专项豁免：同 selectionGroupCode + selectionGroupLimit=1 的课程可并行
            if (canParallelSelectionGroup(currentClassCourse, existingClassCourse)) {
                continue;
            }
            result.put("hasConflict", true);
            result.put("conflictType", "class");
            result.put("conflictScheduleId", existing.getScheduleId());
            result.put("classId", existingClassCourse.getClassId());
            result.put("className", existingClassCourse.getClassName());
            result.put("classCourseId", existing.getClassCourseId());
            result.put("classCourseName", StringUtils.defaultIfEmpty(existingClassCourse.getCourseName(),
                    String.valueOf(existing.getClassCourseId())));
            result.put("teacherName", existingClassCourse.getTeacherName());
            result.put("weekDay", existing.getWeekDay());
            result.put("startSection", existing.getStartSection());
            result.put("endSection", existing.getEndSection());
            result.put("weeksText", existing.getWeeksText());
            result.put("classroomName",
                    StringUtils.defaultIfEmpty(existing.getClassroomName(), existing.getClassroom()));
            result.put("buildingName", existing.getBuildingName());
            result.put("campusName", existing.getCampusName());
            break;
        }
        return result;
    }

    private boolean canParallelSelectionGroup(ScClassCourse a, ScClassCourse b) {
        if (a == null || b == null) {
            return false;
        }
        if (!java.util.Objects.equals(a.getClassId(), b.getClassId())
                || !java.util.Objects.equals(a.getCourseId(), b.getCourseId())) {
            return false;
        }
        String codeA = StringUtils.trimToEmpty(a.getSelectionGroupCode());
        String codeB = StringUtils.trimToEmpty(b.getSelectionGroupCode());
        if (StringUtils.isEmpty(codeA) || !codeA.equals(codeB)) {
            return false;
        }
        int limitA = a.getSelectionGroupLimit() == null || a.getSelectionGroupLimit() <= 0 ? 1 : a.getSelectionGroupLimit();
        int limitB = b.getSelectionGroupLimit() == null || b.getSelectionGroupLimit() <= 0 ? 1 : b.getSelectionGroupLimit();
        return limitA == 1 && limitB == 1;
    }

    private boolean isSameCombinedClassGroup(ScClassCourse a, ScClassCourse b) {
        if (a == null || b == null) {
            return false;
        }
        String codeA = StringUtils.trimToEmpty(a.getCombinedClassCode());
        String codeB = StringUtils.trimToEmpty(b.getCombinedClassCode());
        if (StringUtils.isEmpty(codeA) || StringUtils.isEmpty(codeB)) {
            return false;
        }
        return codeA.equals(codeB)
                && java.util.Objects.equals(a.getCourseId(), b.getCourseId())
                && java.util.Objects.equals(a.getTermId(), b.getTermId());
    }

    private boolean isSectionOverlap(ScCourseSchedule left, ScCourseSchedule right) {
        int leftStart = left.getStartSection() == null ? 0 : left.getStartSection();
        int leftEnd = left.getEndSection() == null ? leftStart : left.getEndSection();
        int rightStart = right.getStartSection() == null ? 0 : right.getStartSection();
        int rightEnd = right.getEndSection() == null ? rightStart : right.getEndSection();
        return Math.max(leftStart, rightStart) <= Math.min(leftEnd, rightEnd);
    }

    private boolean isWeekOverlap(String leftWeeks, String leftWeeksJson, String rightWeeks, String rightWeeksJson) {
        Set<Integer> leftSet = extractWeeks(leftWeeks, leftWeeksJson);
        Set<Integer> rightSet = extractWeeks(rightWeeks, rightWeeksJson);
        if (leftSet.isEmpty() || rightSet.isEmpty()) {
            return true;
        }
        for (Integer week : leftSet) {
            if (rightSet.contains(week)) {
                return true;
            }
        }
        return false;
    }

    private Set<Integer> extractWeeks(String weeksText, String weeksJson) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        if (StringUtils.isNotEmpty(weeksJson)) {
            try {
                List<Integer> values = objectMapper.readValue(weeksJson, new TypeReference<List<Integer>>() {
                });
                weekSet.addAll(values.stream().filter(java.util.Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
                if (!weekSet.isEmpty()) {
                    return weekSet;
                }
            } catch (Exception ignored) {
            }
        }
        for (String segment : normalizeWeekSegments(weeksText)) {
            weekSet.addAll(expandWeekSegment(segment));
        }
        return weekSet;
    }

    private List<String> normalizeWeekSegments(String weeksText) {
        if (StringUtils.isEmpty(weeksText)) {
            return Collections.emptyList();
        }
        String normalized = weeksText.trim()
                .replace("第", "")
                .replace("周次", "")
                .replace("，", ",")
                .replace("、", ",")
                .replace("；", ",")
                .replace(";", ",")
                .replace("（", "(")
                .replace("）", ")")
                .replace("～", "-")
                .replace("~", "-")
                .replace("至", "-")
                .replace("到", "-");
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replace("周", "");
        if (StringUtils.isEmpty(normalized)) {
            return Collections.emptyList();
        }
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private Set<Integer> expandWeekSegment(String segment) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        if (StringUtils.isEmpty(segment)) {
            return weekSet;
        }
        boolean oddOnly = segment.contains("单");
        boolean evenOnly = segment.contains("双");
        String cleaned = segment
                .replace("(单)", "")
                .replace("(双)", "")
                .replace("单周", "")
                .replace("双周", "")
                .replace("单", "")
                .replace("双", "")
                .replace("(", "")
                .replace(")", "");
        if (StringUtils.isEmpty(cleaned)) {
            return weekSet;
        }
        if (cleaned.contains("-")) {
            String[] range = cleaned.split("-");
            if (range.length == 2 && StringUtils.isNumeric(range[0].trim()) && StringUtils.isNumeric(range[1].trim())) {
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                if (start > end) {
                    int temp = start;
                    start = end;
                    end = temp;
                }
                int step = (oddOnly || evenOnly) ? 2 : 1;
                if (oddOnly && start % 2 == 0) {
                    start++;
                }
                if (evenOnly && start % 2 != 0) {
                    start++;
                }
                for (int week = start; week <= end; week += step) {
                    weekSet.add(week);
                }
            }
            return weekSet;
        }
        if (StringUtils.isNumeric(cleaned)) {
            int week = Integer.parseInt(cleaned);
            if ((!oddOnly && !evenOnly)
                    || (oddOnly && week % 2 != 0)
                    || (evenOnly && week % 2 == 0)) {
                weekSet.add(week);
            }
        }
        return weekSet;
    }

    private Set<Long> resolveTargetClassCourseIds(CourseScheduleAutoArrangeDto request, List<ScClassCourse> classCourses) {
        Set<Long> requestedIds = request == null || request.getClassCourseIds() == null
                ? Collections.emptySet()
                : Arrays.stream(request.getClassCourseIds())
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!requestedIds.isEmpty()) {
            return requestedIds;
        }
        if (classCourses == null) {
            return Collections.emptySet();
        }
        return classCourses.stream()
                .filter(item -> item != null && item.getId() != null)
                .map(ScClassCourse::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
