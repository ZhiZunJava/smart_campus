package com.smart.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.campusvo.CourseScheduleAutoArrangeVo;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto.CourseScheduleAutoArrangeItemDto;
import com.smart.system.service.ISysConfigService;

@Service
public class CourseScheduleAutoArrangeService {
    private static final String CONFIG_KEY = "campus.schedule.timeTableLayout";
    private static final long VIRTUAL_OPEN_SPACE_CLASSROOM_ID = -1L;
    private static final String VIRTUAL_OPEN_SPACE_CLASSROOM_NAME = "空场地";
    private static final String VIRTUAL_OPEN_SPACE_ROOM_TYPE = "OPEN_SPACE";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ISysConfigService configService;

    public CourseScheduleAutoArrangeVo autoArrange(CourseScheduleAutoArrangeDto request,
            List<ScClassCourse> classCourses,
            List<ScClassroom> classrooms,
            List<ScCourseSchedule> existingSchedules,
            ScSchoolTerm term,
            String operator) {
        if (request == null || request.getTermId() == null) {
            throw new ServiceException("学期不能为空");
        }
        if (term == null) {
            throw new ServiceException("未找到对应学期");
        }

        int populationSize = normalizePopulationSize(request.getPopulationSize());
        int generationCount = normalizeGenerationCount(request.getGenerationCount());
        double mutationRate = normalizeMutationRate(request.getMutationRate());
        boolean clearExistingSchedules = Boolean.TRUE.equals(request.getClearExistingSchedules());
        List<Integer> preferredDurations = normalizePreferredDurations(request.getPreferredSessionDurations());
        Set<String> excludedDayParts = normalizeExcludedDayParts(request.getExcludedDayParts());
        Set<Integer> excludedWeekDays = normalizeExcludedWeekDays(request.getExcludedWeekDays());
        Map<Long, CourseScheduleAutoArrangeItemDto> itemConfigMap = buildItemConfigMap(request.getItems());

        Map<Integer, String> sectionDayPartMap = loadSectionDayPartMap();
        List<Integer> sectionUnits = filterSectionUnits(sectionDayPartMap, excludedDayParts);
        if (sectionUnits.isEmpty()) {
            throw new ServiceException("课表布局未配置有效节次，无法自动排课");
        }

        List<ScClassroom> activeClassrooms = classrooms == null ? Collections.emptyList()
                : classrooms.stream()
                        .filter(item -> item != null && "0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0")))
                        .sorted(Comparator
                                .comparing((ScClassroom item) -> item.getSortOrder() == null ? Integer.MAX_VALUE
                                        : item.getSortOrder())
                                .thenComparing(
                                        item -> item.getClassroomId() == null ? Long.MAX_VALUE : item.getClassroomId()))
                        .collect(Collectors.toList());
        if (hasVirtualOpenSpaceSelection(itemConfigMap)) {
            activeClassrooms = new ArrayList<>(activeClassrooms);
            activeClassrooms.add(createVirtualOpenSpaceClassroom());
        }
        if (activeClassrooms.isEmpty()) {
            throw new ServiceException("当前没有可用教室，无法自动排课");
        }

        Set<Long> requestedCourseIds = request.getClassCourseIds() == null ? Collections.emptySet()
                : Arrays.stream(request.getClassCourseIds()).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        List<ScCourseSchedule> termExistingSchedules = existingSchedules == null ? new ArrayList<>()
                : existingSchedules.stream()
                        .filter(item -> item != null && Objects.equals(item.getTermId(), request.getTermId()))
                        .collect(Collectors.toList());

        Map<Long, Integer> existingArrangedHours = calculateExistingArrangedHours(termExistingSchedules);
        List<LessonTask> lessonTasks = buildLessonTasks(classCourses, requestedCourseIds, existingArrangedHours, term,
                activeClassrooms, itemConfigMap, preferredDurations, excludedWeekDays, excludedDayParts);

        CourseScheduleAutoArrangeVo result = new CourseScheduleAutoArrangeVo();
        result.setTermId(term.getTermId());
        result.setTermName(term.getTermName());
        result.setPopulationSize(populationSize);
        result.setGenerationCount(generationCount);
        result.setMutationRate(mutationRate);
        result.setTotalLessonTasks(lessonTasks.size());
        result.setClearedSchedules(0);

        if (lessonTasks.isEmpty()) {
            result.getWarnings().add("当前学期没有需要自动排课的班级课程，可能是周学时为空，或者现有排课已经覆盖了目标学时。");
            result.setArrangedLessonTasks(0);
            result.setInsertedSchedules(0);
            result.setBestFitnessScore(0D);
            return result;
        }

        Map<Long, ScClassCourse> classCourseMap = classCourses == null ? Collections.emptyMap()
                : classCourses.stream()
                        .filter(item -> item != null && item.getId() != null)
                        .collect(Collectors.toMap(ScClassCourse::getId, item -> item, (left, right) -> left));
        List<ScCourseSchedule> lockedSchedules = clearExistingSchedules ? new ArrayList<>() : termExistingSchedules;
        GeneticSolution best = runGeneticAlgorithm(lessonTasks, activeClassrooms, sectionUnits, lockedSchedules,
                classCourseMap, sectionDayPartMap, populationSize, generationCount, mutationRate);
        List<LessonAssignment> validAssignments = collectValidAssignments(best, lessonTasks, activeClassrooms,
                lockedSchedules, classCourseMap, sectionDayPartMap);

        result.setBestFitnessScore(best.fitnessScore);
        result.setArrangedLessonTasks(validAssignments.size());
        result.setInsertedSchedules(validAssignments.size());
        Map<String, LessonTask> taskMap = lessonTasks.stream()
                .collect(Collectors.toMap(item -> item.taskId, item -> item, (left, right) -> left));
        Map<Long, ScClassroom> classroomMap = activeClassrooms.stream()
                .filter(item -> item != null && item.getClassroomId() != null)
                .collect(Collectors.toMap(ScClassroom::getClassroomId, item -> item, (left, right) -> left));
        result.setArrangedLessons(validAssignments.stream()
                .map(item -> buildArrangedLessonMap(item, term, taskMap, classroomMap))
                .collect(Collectors.toList()));
        result.setUnscheduledLessons(buildUnscheduledLessons(lessonTasks, validAssignments));

        if (clearExistingSchedules) {
            result.setClearedSchedules(
                    (int) termExistingSchedules.stream().filter(item -> item.getScheduleId() != null).count());
        }
        if (!result.getUnscheduledLessons().isEmpty()) {
            result.getWarnings().add("部分课块未能排入可行时段，建议补充教室资源、降低同教师同班级集中度，或开启清空后重排。");
        }
        if (!clearExistingSchedules && !lockedSchedules.isEmpty()) {
            result.getWarnings().add("本次自动排课保留了学期内已有排课，仅为剩余课块补排。");
        }
        if (StringUtils.isNotEmpty(operator)) {
            result.getWarnings().add("自动排课执行人：" + operator);
        }
        return result;
    }

    public List<ScCourseSchedule> toSchedules(List<Map<String, Object>> arrangedLessons,
            List<ScClassCourse> classCourses,
            List<ScClassroom> classrooms,
            ScSchoolTerm term,
            String operator) {
        Map<Long, ScClassCourse> classCourseMap = classCourses.stream()
                .filter(item -> item != null && item.getId() != null)
                .collect(Collectors.toMap(ScClassCourse::getId, item -> item, (left, right) -> left));
        Map<Long, ScClassroom> classroomMap = classrooms.stream()
                .filter(item -> item != null && item.getClassroomId() != null)
                .collect(Collectors.toMap(ScClassroom::getClassroomId, item -> item, (left, right) -> left));

        List<ScCourseSchedule> schedules = new ArrayList<>();
        for (Map<String, Object> item : arrangedLessons) {
            String taskId = String.valueOf(item.get("taskId"));
            Long classCourseId = Long.parseLong(taskId.split("-")[0]);
            ScClassCourse classCourse = classCourseMap.get(classCourseId);
            Long classroomId = item.get("classroomId") == null ? null
                    : Long.parseLong(String.valueOf(item.get("classroomId")));
            ScClassroom classroom = classroomMap.get(classroomId);
            if (classCourse == null) {
                continue;
            }
            ScCourseSchedule schedule = new ScCourseSchedule();
            schedule.setTermId(term.getTermId());
            schedule.setClassCourseId(classCourseId);
            schedule.setWeekDay(Integer.parseInt(String.valueOf(item.get("weekDay"))));
            schedule.setStartSection(Integer.parseInt(String.valueOf(item.get("startSection"))));
            schedule.setEndSection(Integer.parseInt(String.valueOf(item.get("endSection"))));
            if (isVirtualOpenSpaceClassroomId(classroomId)) {
                schedule.setClassroomId(null);
                schedule.setClassroom(VIRTUAL_OPEN_SPACE_CLASSROOM_NAME);
                schedule.setClassroomName(VIRTUAL_OPEN_SPACE_CLASSROOM_NAME);
                schedule.setCampusName(classCourse.getCampusName());
            } else if (classroom != null) {
                schedule.setClassroomId(classroomId);
                schedule.setClassroom(classroom.getClassroomName());
                schedule.setClassroomName(classroom.getClassroomName());
                schedule.setBuildingName(classroom.getBuildingName());
                schedule.setCampusName(classroom.getCampusName());
            } else {
                continue;
            }
            String weeksText = StringUtils.defaultIfEmpty(String.valueOf(item.get("weeksText")), buildWeeksText(classCourse, term));
            if ("null".equalsIgnoreCase(weeksText)) {
                weeksText = buildWeeksText(classCourse, term);
            }
            schedule.setWeeksText(weeksText);
            String weeksJson = item.get("weeksJson") == null ? null : String.valueOf(item.get("weeksJson"));
            if (StringUtils.isEmpty(weeksJson) || "null".equalsIgnoreCase(weeksJson)) {
                weeksJson = buildWeeksJsonFromText(schedule.getWeeksText());
            }
            schedule.setWeeksJson(weeksJson);
            schedule.setStatus("0");
            schedule.setCreateBy(operator);
            schedule.setRemark("遗传算法自动排课生成");
            schedules.add(schedule);
        }
        return schedules;
    }

    private int normalizePopulationSize(Integer value) {
        int resolved = value == null ? 60 : value;
        return Math.min(Math.max(resolved, 20), 200);
    }

    private int normalizeGenerationCount(Integer value) {
        int resolved = value == null ? 120 : value;
        return Math.min(Math.max(resolved, 20), 500);
    }

    private double normalizeMutationRate(Double value) {
        double resolved = value == null ? 0.12D : value;
        if (resolved < 0D) {
            return 0D;
        }
        return Math.min(resolved, 1D);
    }

    private Map<Integer, String> loadSectionDayPartMap() {
        String configValue = configService.selectConfigByKey(CONFIG_KEY);
        if (StringUtils.isNotEmpty(configValue)) {
            try {
                Map<String, Object> compact = objectMapper.readValue(configValue,
                        new TypeReference<LinkedHashMap<String, Object>>() {
                        });
                Object unitsObject = compact.get("u");
                if (unitsObject instanceof List<?> units) {
                    Map<Integer, String> result = new LinkedHashMap<>();
                    for (Object unitObject : units) {
                        if (!(unitObject instanceof List<?> unit) || unit.size() < 2) {
                            continue;
                        }
                        String encodedDayPart = unit.size() > 4 ? String.valueOf(unit.get(4)) : "M";
                        String dayPart = decodeDayPart(encodedDayPart);
                        Object index = unit.get(1);
                        if (index != null) {
                            result.put(Integer.parseInt(String.valueOf(index)), dayPart);
                        }
                    }
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        Map<Integer, String> defaults = new LinkedHashMap<>();
        for (int unit = 1; unit <= 12; unit++) {
            defaults.put(unit, resolveDefaultDayPart(unit));
        }
        return defaults;
    }

    private List<Integer> filterSectionUnits(Map<Integer, String> sectionDayPartMap, Set<String> excludedDayParts) {
        if (sectionDayPartMap == null || sectionDayPartMap.isEmpty()) {
            return Collections.emptyList();
        }
        return sectionDayPartMap.entrySet().stream()
                .filter(entry -> !excludedDayParts.contains(StringUtils.defaultIfEmpty(entry.getValue(), "MORNING")))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> calculateExistingArrangedHours(List<ScCourseSchedule> existingSchedules) {
        Map<Long, Integer> result = new HashMap<>();
        for (ScCourseSchedule item : existingSchedules) {
            if (item == null || !"0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0"))
                    || item.getClassCourseId() == null) {
                continue;
            }
            int start = item.getStartSection() == null ? 0 : item.getStartSection();
            int end = item.getEndSection() == null ? start : item.getEndSection();
            int duration = Math.max(1, end - start + 1);
            int weekCount = Math.max(extractWeeks(item.getWeeksText()).size(), 1);
            result.merge(item.getClassCourseId(), duration * weekCount, Integer::sum);
        }
        return result;
    }

    private List<LessonTask> buildLessonTasks(List<ScClassCourse> classCourses,
            Set<Long> requestedCourseIds,
            Map<Long, Integer> existingArrangedHours,
            ScSchoolTerm term,
            List<ScClassroom> classrooms,
            Map<Long, CourseScheduleAutoArrangeItemDto> itemConfigMap,
            List<Integer> preferredDurations,
            Set<Integer> globalExcludedWeekDays,
            Set<String> globalExcludedDayParts) {
        List<LessonTask> tasks = new ArrayList<>();
        if (classCourses == null) {
            return tasks;
        }
        Map<String, Integer> selectionGroupOptionCountMap = buildSelectionGroupOptionCountMap(classCourses, term);
        for (ScClassCourse item : classCourses) {
            if (item == null || !"0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0"))) {
                continue;
            }
            if (item.getTermId() == null || !item.getTermId().equals(term.getTermId())) {
                continue;
            }
            if (!requestedCourseIds.isEmpty() && !requestedCourseIds.contains(item.getId())) {
                continue;
            }

            CourseScheduleAutoArrangeItemDto itemConfig = itemConfigMap.get(item.getId());
            Set<Integer> excludedWeekDays = new LinkedHashSet<>(globalExcludedWeekDays == null
                    ? Collections.emptySet()
                    : globalExcludedWeekDays);
            Set<String> excludedDayParts = new LinkedHashSet<>(globalExcludedDayParts == null
                    ? Collections.emptySet()
                    : globalExcludedDayParts);
            if (itemConfig != null && itemConfig.getExcludedWeekDays() != null) {
                excludedWeekDays.addAll(Arrays.stream(itemConfig.getExcludedWeekDays())
                        .filter(Objects::nonNull)
                        .map(Integer::intValue)
                        .filter(weekDay -> weekDay >= 1 && weekDay <= 7)
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            if (itemConfig != null && itemConfig.getExcludedDayParts() != null) {
                excludedDayParts.addAll(Arrays.stream(itemConfig.getExcludedDayParts())
                        .filter(StringUtils::isNotEmpty)
                        .map(value -> value.trim().toUpperCase())
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            List<Integer> configuredWeeks = resolveConfiguredWeeks(itemConfig, item, term);
            int weekCount = Math.max(configuredWeeks.size(), 1);
            int weeklySections = resolveWeeklySections(item, term, weekCount);
            if (itemConfig != null && itemConfig.getMaxWeeklySections() != null
                    && itemConfig.getMaxWeeklySections() > 0) {
                weeklySections = Math.min(weeklySections, itemConfig.getMaxWeeklySections());
            }
            int targetArrangeHours = resolveTargetArrangeHours(item, weeklySections, weekCount);
            int alreadyArrangedHours = existingArrangedHours.getOrDefault(item.getId(), 0);
            int remainArrangeHours = Math.max(targetArrangeHours - alreadyArrangedHours, 0);
            int scheduleCapacityHours = Math.max(weeklySections, 0) * weekCount;
            int arrangeHours = Math.min(remainArrangeHours, scheduleCapacityHours);
            if (arrangeHours <= 0) {
                continue;
            }

            Long assignedClassroomId = itemConfig != null ? itemConfig.getClassroomId() : null;
            int estimatedStudentCount = resolveStudentCount(item,
                    resolveSelectionGroupOptionCount(item, selectionGroupOptionCountMap));
            List<LessonTaskSeed> taskSeeds = buildTaskSeeds(arrangeHours, weeklySections, configuredWeeks, preferredDurations);
            List<Long> preferredClassrooms;
            if (isVirtualOpenSpaceClassroomId(assignedClassroomId)) {
                preferredClassrooms = Collections.singletonList(VIRTUAL_OPEN_SPACE_CLASSROOM_ID);
            } else if (assignedClassroomId != null) {
                preferredClassrooms = Collections.singletonList(assignedClassroomId);
            } else {
                preferredClassrooms = resolvePreferredClassrooms(item, classrooms, estimatedStudentCount);
            }
            int sequence = 1;
            for (LessonTaskSeed seed : taskSeeds) {
                LessonTask task = new LessonTask();
                task.taskId = item.getId() + "-" + sequence;
                task.classCourseId = item.getId();
                task.classId = item.getClassId();
                task.courseId = item.getCourseId();
                task.className = item.getClassName();
                task.courseName = resolveDisplayedCourseName(item);
                task.courseCategory = item.getCourseCategory();
                task.teacherId = item.getTeacherId();
                task.teacherName = item.getTeacherName();
                task.campusName = item.getCampusName();
                task.openDeptId = item.getOpenDeptId();
                task.expectedDuration = seed.duration;
                task.studentCount = estimatedStudentCount;
                task.weeksText = seed.weeksText;
                task.preferredClassroomIds = preferredClassrooms;
                task.fixedClassroom = assignedClassroomId != null;
                task.excludedWeekDays = excludedWeekDays;
                task.excludedDayParts = excludedDayParts;
                task.selectionGroupCode = normalizeSelectionGroupCode(item.getSelectionGroupCode());
                task.selectionGroupLimit = normalizeSelectionGroupLimit(item.getSelectionGroupLimit());
                task.priorityScore = (item.getRequiredFlag() != null && "Y".equalsIgnoreCase(item.getRequiredFlag())
                        ? 10
                        : 0)
                        + Math.max(task.studentCount, 0)
                        + Math.max(arrangeHours, 0) * 2
                        + (assignedClassroomId != null ? 20 : 0);
                tasks.add(task);
                sequence++;
            }
        }
        tasks.sort(Comparator.comparingInt((LessonTask item) -> item.priorityScore).reversed()
                .thenComparing((LessonTask item) -> item.studentCount).reversed()
                .thenComparing(item -> item.classCourseId == null ? Long.MAX_VALUE : item.classCourseId));
        return tasks;
    }

    private int resolveWeeklySections(ScClassCourse item, ScSchoolTerm term, int weekCount) {
        int effectiveWeekCount = Math.max(weekCount, 1);
        if (item.getTotalHours() != null && item.getTotalHours() > 0) {
            return Math.max(1, (int) Math.ceil(item.getTotalHours() * 1D / effectiveWeekCount));
        }
        if (item.getWeeklyHours() != null && item.getWeeklyHours() > 0) {
            return item.getWeeklyHours();
        }
        if (item.getTotalHours() != null && item.getTotalHours() > 0) {
            int requiredWeeks = item.getRequiredWeeks() == null || item.getRequiredWeeks() <= 0
                    ? Math.max(term.getTotalWeeks() == null ? 1 : term.getTotalWeeks(), 1)
                    : item.getRequiredWeeks();
            return Math.max(1, (int) Math.ceil(item.getTotalHours() * 1D / requiredWeeks));
        }
        return 0;
    }

    private int resolveTargetArrangeHours(ScClassCourse item, int weeklySections, int weekCount) {
        if (item.getTotalHours() != null && item.getTotalHours() > 0) {
            return item.getTotalHours();
        }
        return Math.max(weeklySections, 0) * Math.max(weekCount, 1);
    }

    private List<Integer> splitDurations(int remainSections, List<Integer> preferredDurations) {
        List<Integer> durations = new ArrayList<>();
        int rest = remainSections;
        List<Integer> normalizedDurations = preferredDurations == null || preferredDurations.isEmpty()
                ? Arrays.asList(2, 1)
                : preferredDurations;
        while (rest > 0) {
            Integer picked = 1;
            for (Integer item : normalizedDurations) {
                if (item != null && item > 0 && item <= rest) {
                    picked = item;
                    break;
                }
            }
            durations.add(picked);
            rest -= picked;
        }
        return durations;
    }

    private List<LessonTaskSeed> buildTaskSeeds(int arrangeHours,
            int weeklySections,
            List<Integer> configuredWeeks,
            List<Integer> preferredDurations) {
        List<LessonTaskSeed> result = new ArrayList<>();
        if (arrangeHours <= 0 || configuredWeeks == null || configuredWeeks.isEmpty() || weeklySections <= 0) {
            return result;
        }
        int weekCount = configuredWeeks.size();
        int fullWeekSections = Math.min(weeklySections, arrangeHours / weekCount);
        String fullWeeksText = buildWeeksText(configuredWeeks);
        for (Integer duration : splitDurations(fullWeekSections, preferredDurations)) {
            result.add(new LessonTaskSeed(duration, fullWeeksText));
        }

        int remainderHours = arrangeHours - fullWeekSections * weekCount;
        if (remainderHours > 0 && weeklySections > fullWeekSections) {
            List<Integer> partialWeeks = new ArrayList<>(configuredWeeks.subList(0, Math.min(remainderHours, weekCount)));
            if (!partialWeeks.isEmpty()) {
                result.add(new LessonTaskSeed(1, buildWeeksText(partialWeeks)));
            }
        }
        return result;
    }

    private int resolveStudentCount(ScClassCourse item, int selectionGroupOptionCount) {
        int baseStudentCount = resolveBaseStudentCount(item);
        if (baseStudentCount <= 0 || selectionGroupOptionCount <= 1) {
            return baseStudentCount;
        }
        String groupKey = buildSelectionGroupKey(item);
        if (StringUtils.isEmpty(groupKey)) {
            return baseStudentCount;
        }
        int groupLimit = Math.max(1,
                Math.min(normalizeSelectionGroupLimit(item.getSelectionGroupLimit()), selectionGroupOptionCount));
        int estimatedStudentCount = (int) Math.ceil(baseStudentCount * groupLimit * 1D / selectionGroupOptionCount);
        return Math.max(1, Math.min(baseStudentCount, estimatedStudentCount));
    }

    private int resolveBaseStudentCount(ScClassCourse item) {
        if (item.getActualStudentCount() != null && item.getActualStudentCount() > 0) {
            return item.getActualStudentCount();
        }
        if (item.getStudentLimit() != null && item.getStudentLimit() > 0) {
            return item.getStudentLimit();
        }
        return 0;
    }

    private Map<String, Integer> buildSelectionGroupOptionCountMap(List<ScClassCourse> classCourses, ScSchoolTerm term) {
        Map<String, Integer> result = new HashMap<>();
        if (classCourses == null || term == null || term.getTermId() == null) {
            return result;
        }
        for (ScClassCourse item : classCourses) {
            if (item == null || !"0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0"))) {
                continue;
            }
            if (!Objects.equals(item.getTermId(), term.getTermId())) {
                continue;
            }
            String groupKey = buildSelectionGroupKey(item);
            if (StringUtils.isEmpty(groupKey)) {
                continue;
            }
            result.merge(groupKey, 1, Integer::sum);
        }
        return result;
    }

    private int resolveSelectionGroupOptionCount(ScClassCourse item, Map<String, Integer> selectionGroupOptionCountMap) {
        String groupKey = buildSelectionGroupKey(item);
        if (StringUtils.isEmpty(groupKey) || selectionGroupOptionCountMap == null) {
            return 0;
        }
        return selectionGroupOptionCountMap.getOrDefault(groupKey, 0);
    }

    private List<Long> resolvePreferredClassrooms(ScClassCourse item, List<ScClassroom> classrooms, int studentCount) {
        List<Long> preferred = classrooms.stream()
                .filter(room -> !isVirtualOpenSpaceClassroom(room))
                .filter(room -> room.getClassroomId() != null)
                .filter(room -> room.getCapacity() == null || room.getCapacity() <= 0 || studentCount <= 0
                        || room.getCapacity() >= studentCount)
                .sorted(Comparator
                        .comparing((ScClassroom room) -> sameDept(item, room) ? 0 : 1)
                        .thenComparing(room -> sameCampus(item, room) ? 0 : 1)
                        .thenComparing(room -> sameRoomType(item, room) ? 0 : 1)
                        .thenComparing(room -> room.getCapacity() == null ? Integer.MAX_VALUE
                                : Math.abs(room.getCapacity() - studentCount))
                        .thenComparing(room -> room.getSortOrder() == null ? Integer.MAX_VALUE : room.getSortOrder()))
                .map(ScClassroom::getClassroomId)
                .collect(Collectors.toList());
        if (!preferred.isEmpty()) {
            return preferred;
        }
        return classrooms.stream()
                .filter(room -> !isVirtualOpenSpaceClassroom(room))
                .map(ScClassroom::getClassroomId).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean sameCampus(ScClassCourse course, ScClassroom room) {
        if (course == null || room == null) {
            return false;
        }
        if (StringUtils.isEmpty(course.getCampusName()) || StringUtils.isEmpty(room.getCampusName())) {
            return false;
        }
        return StringUtils.equals(course.getCampusName(), room.getCampusName());
    }

    private boolean sameDept(ScClassCourse course, ScClassroom room) {
        if (course == null || room == null || room.getDeptId() == null) {
            return false;
        }
        return course.getOpenDeptId() != null && course.getOpenDeptId().equals(room.getDeptId());
    }

    private boolean sameRoomType(ScClassCourse course, ScClassroom room) {
        if (course == null || room == null) {
            return false;
        }
        String category = StringUtils.defaultIfEmpty(course.getCourseCategory(), "");
        String roomType = StringUtils.defaultIfEmpty(room.getRoomType(), "");
        if (StringUtils.isEmpty(category) || StringUtils.isEmpty(roomType)) {
            return false;
        }
        if (StringUtils.contains(category, "实训") || StringUtils.contains(category, "实验")) {
            return StringUtils.contains(roomType, "实验室") || StringUtils.contains(roomType, "机房");
        }
        if (StringUtils.contains(category, "上机")) {
            return StringUtils.contains(roomType, "机房");
        }
        if (StringUtils.contains(category, "讲座")) {
            return StringUtils.contains(roomType, "报告厅");
        }
        return StringUtils.contains(roomType, "普通教室");
    }

    private boolean sameCampus(LessonTask task, ScClassroom room) {
        if (task == null || room == null) {
            return false;
        }
        if (StringUtils.isEmpty(task.campusName) || StringUtils.isEmpty(room.getCampusName())) {
            return false;
        }
        return StringUtils.equals(task.campusName, room.getCampusName());
    }

    private boolean sameDept(LessonTask task, ScClassroom room) {
        if (task == null || room == null || room.getDeptId() == null) {
            return false;
        }
        return task.openDeptId != null && task.openDeptId.equals(room.getDeptId());
    }

    private boolean sameRoomType(LessonTask task, ScClassroom room) {
        if (task == null || room == null) {
            return false;
        }
        String category = StringUtils.defaultIfEmpty(task.courseCategory, "");
        String roomType = StringUtils.defaultIfEmpty(room.getRoomType(), "");
        if (StringUtils.isEmpty(category) || StringUtils.isEmpty(roomType)) {
            return false;
        }
        if (StringUtils.contains(category, "实训") || StringUtils.contains(category, "实验")) {
            return StringUtils.contains(roomType, "实验室") || StringUtils.contains(roomType, "机房");
        }
        if (StringUtils.contains(category, "上机")) {
            return StringUtils.contains(roomType, "机房");
        }
        if (StringUtils.contains(category, "讲座")) {
            return StringUtils.contains(roomType, "报告厅");
        }
        return StringUtils.contains(roomType, "普通教室");
    }

    private String buildWeeksText(ScClassCourse item, ScSchoolTerm term) {
        int totalWeeks = term.getTotalWeeks() == null || term.getTotalWeeks() <= 0 ? 20 : term.getTotalWeeks();
        int requiredWeeks = item.getRequiredWeeks() == null || item.getRequiredWeeks() <= 0 ? totalWeeks
                : Math.min(item.getRequiredWeeks(), totalWeeks);
        if (requiredWeeks <= 1) {
            return "1周";
        }
        return "1-" + requiredWeeks + "周";
    }

    private String buildWeeksText(List<Integer> weeks) {
        if (weeks == null || weeks.isEmpty()) {
            return "";
        }
        List<Integer> sortedWeeks = weeks.stream().filter(Objects::nonNull).distinct().sorted().collect(Collectors.toList());
        if (sortedWeeks.isEmpty()) {
            return "";
        }
        List<String> segments = new ArrayList<>();
        int start = sortedWeeks.get(0);
        int previous = start;
        for (int index = 1; index < sortedWeeks.size(); index++) {
            int current = sortedWeeks.get(index);
            if (current == previous + 1) {
                previous = current;
                continue;
            }
            segments.add(formatWeekSegment(start, previous));
            start = current;
            previous = current;
        }
        segments.add(formatWeekSegment(start, previous));
        return String.join(",", segments);
    }

    private String formatWeekSegment(int start, int end) {
        return start == end ? start + "周" : start + "-" + end + "周";
    }

    private List<Integer> resolveConfiguredWeeks(CourseScheduleAutoArrangeItemDto itemConfig,
            ScClassCourse item,
            ScSchoolTerm term) {
        if (itemConfig != null && StringUtils.isNotEmpty(itemConfig.getWeeksJson())) {
            try {
                List<Integer> weeks = objectMapper.readValue(itemConfig.getWeeksJson(), new TypeReference<List<Integer>>() {
                });
                List<Integer> normalized = weeks.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                if (!normalized.isEmpty()) {
                    return normalized;
                }
            } catch (Exception ignored) {
            }
        }
        String weeksText = itemConfig != null && StringUtils.isNotEmpty(itemConfig.getWeeksText())
                ? normalizeWeeksText(itemConfig.getWeeksText())
                : buildWeeksText(item, term);
        return extractWeeks(weeksText).stream().sorted().collect(Collectors.toList());
    }

    private String buildWeeksJsonFromText(String weeksText) {
        try {
            return objectMapper.writeValueAsString(extractWeeks(weeksText));
        } catch (Exception ignored) {
            return null;
        }
    }

    private Map<Long, CourseScheduleAutoArrangeItemDto> buildItemConfigMap(CourseScheduleAutoArrangeItemDto[] items) {
        Map<Long, CourseScheduleAutoArrangeItemDto> result = new LinkedHashMap<>();
        if (items == null) {
            return result;
        }
        for (CourseScheduleAutoArrangeItemDto item : items) {
            if (item == null || item.getClassCourseId() == null) {
                continue;
            }
            result.put(item.getClassCourseId(), item);
        }
        return result;
    }

    private List<Integer> normalizePreferredDurations(Integer[] durations) {
        if (durations == null || durations.length == 0) {
            return Arrays.asList(2, 1);
        }
        return Arrays.stream(durations)
                .filter(Objects::nonNull)
                .map(Integer::intValue)
                .filter(item -> item > 0)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private Set<String> normalizeExcludedDayParts(String[] excludedDayParts) {
        if (excludedDayParts == null || excludedDayParts.length == 0) {
            return Collections.emptySet();
        }
        return Arrays.stream(excludedDayParts)
                .filter(StringUtils::isNotEmpty)
                .map(item -> item.trim().toUpperCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Integer> normalizeExcludedWeekDays(Integer[] excludedWeekDays) {
        if (excludedWeekDays == null || excludedWeekDays.length == 0) {
            return Collections.emptySet();
        }
        return Arrays.stream(excludedWeekDays)
                .filter(Objects::nonNull)
                .map(Integer::intValue)
                .filter(item -> item >= 1 && item <= 7)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String decodeDayPart(String value) {
        return switch (StringUtils.defaultIfEmpty(value, "M")) {
            case "N" -> "NOON";
            case "A" -> "AFTERNOON";
            case "E" -> "EVENING";
            case "NOON" -> "NOON";
            case "AFTERNOON" -> "AFTERNOON";
            case "EVENING" -> "EVENING";
            default -> "MORNING";
        };
    }

    private String resolveDefaultDayPart(Integer unit) {
        if (unit == null) {
            return "MORNING";
        }
        if (unit <= 4) {
            return "MORNING";
        }
        if (unit <= 6) {
            return "NOON";
        }
        if (unit <= 10) {
            return "AFTERNOON";
        }
        return "EVENING";
    }

    private String normalizeWeeksText(String weeksText) {
        if (StringUtils.isEmpty(weeksText)) {
            return weeksText;
        }
        return weeksText.trim()
                .replace("，", ",")
                .replace("、", ",")
                .replace("；", ",")
                .replace(";", ",")
                .replace("～", "-")
                .replace("~", "-")
                .replace("至", "-")
                .replace("到", "-");
    }

    private GeneticSolution runGeneticAlgorithm(List<LessonTask> tasks,
            List<ScClassroom> classrooms,
            List<Integer> sectionUnits,
            List<ScCourseSchedule> lockedSchedules,
            Map<Long, ScClassCourse> classCourseMap,
            Map<Integer, String> sectionDayPartMap,
            int populationSize,
            int generationCount,
            double mutationRate) {
        Random random = ThreadLocalRandom.current();
        List<GeneticSolution> population = new ArrayList<>();
        for (int index = 0; index < populationSize; index++) {
            GeneticSolution solution = createRandomSolution(tasks, classrooms, sectionUnits, random, sectionDayPartMap);
            evaluate(solution, tasks, classrooms, lockedSchedules, classCourseMap, sectionDayPartMap);
            population.add(solution);
        }

        GeneticSolution best = population.stream()
                .max(Comparator.comparingDouble(item -> item.fitnessScore))
                .map(GeneticSolution::copy)
                .orElseGet(GeneticSolution::new);

        for (int generation = 0; generation < generationCount; generation++) {
            population.sort(Comparator.comparingDouble((GeneticSolution item) -> item.fitnessScore).reversed());
            if (!population.isEmpty() && population.get(0).fitnessScore > best.fitnessScore) {
                best = population.get(0).copy();
            }

            List<GeneticSolution> nextPopulation = new ArrayList<>();
            int eliteCount = Math.max(2, populationSize / 10);
            for (int index = 0; index < Math.min(eliteCount, population.size()); index++) {
                nextPopulation.add(population.get(index).copy());
            }

            while (nextPopulation.size() < populationSize) {
                GeneticSolution parentA = selectParent(population, random);
                GeneticSolution parentB = selectParent(population, random);
                GeneticSolution child = crossover(parentA, parentB, random);
                mutate(child, tasks, classrooms, sectionUnits, mutationRate, random, sectionDayPartMap);
                evaluate(child, tasks, classrooms, lockedSchedules, classCourseMap, sectionDayPartMap);
                nextPopulation.add(child);
            }
            population = nextPopulation;
        }

        evaluate(best, tasks, classrooms, lockedSchedules, classCourseMap, sectionDayPartMap);
        return best;
    }

    private GeneticSolution createRandomSolution(List<LessonTask> tasks, List<ScClassroom> classrooms,
            List<Integer> sectionUnits, Random random, Map<Integer, String> sectionDayPartMap) {
        GeneticSolution solution = new GeneticSolution();
        for (LessonTask task : tasks) {
            solution.assignments.put(task.taskId, randomAssignment(task, classrooms, sectionUnits, random, sectionDayPartMap));
        }
        return solution;
    }

    private LessonAssignment randomAssignment(LessonTask task, List<ScClassroom> classrooms, List<Integer> sectionUnits,
            Random random, Map<Integer, String> sectionDayPartMap) {
        LessonAssignment assignment = new LessonAssignment();
        assignment.taskId = task.taskId;
        assignment.weekDay = pickWeekDay(task, random);
        assignment.startSection = pickStartSection(task, sectionUnits, random, sectionDayPartMap);
        assignment.endSection = assignment.startSection + task.expectedDuration - 1;
        assignment.classroomId = pickClassroom(task, classrooms, random);
        return assignment;
    }

    private Integer pickWeekDay(LessonTask task, Random random) {
        List<Integer> allowedWeekDays = resolveAllowedWeekDays(task);
        if (allowedWeekDays.isEmpty()) {
            return random.nextInt(7) + 1;
        }
        return allowedWeekDays.get(random.nextInt(allowedWeekDays.size()));
    }

    private List<Integer> resolveAllowedWeekDays(LessonTask task) {
        Set<Integer> excluded = task == null || task.excludedWeekDays == null
                ? Collections.emptySet()
                : task.excludedWeekDays;
        List<Integer> result = new ArrayList<>();
        for (int weekDay = 1; weekDay <= 7; weekDay++) {
            if (!excluded.contains(weekDay)) {
                result.add(weekDay);
            }
        }
        return result;
    }

    private int pickStartSection(LessonTask task, List<Integer> sectionUnits, Random random,
            Map<Integer, String> sectionDayPartMap) {
        List<Integer> candidates = new ArrayList<>();
        Set<Integer> sections = new HashSet<>(sectionUnits);
        for (Integer start : sectionUnits) {
            boolean available = true;
            for (int offset = 0; offset < task.expectedDuration; offset++) {
                if (!sections.contains(start + offset)) {
                    available = false;
                    break;
                }
            }
            if (available && isExcludedDayPartAssignment(start, task.expectedDuration, task, sectionDayPartMap)) {
                available = false;
            }
            if (available) {
                candidates.add(start);
            }
        }
        List<Integer> preferredCandidates = candidates.stream()
                .filter(start -> isStandardBlockStart(start, task.expectedDuration, sectionUnits))
                .collect(Collectors.toList());
        if (!preferredCandidates.isEmpty()) {
            candidates = preferredCandidates;
        }
        if (candidates.isEmpty()) {
            return sectionUnits.get(0);
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean isExcludedDayPartAssignment(Integer startSection, int duration, LessonTask task,
            Map<Integer, String> sectionDayPartMap) {
        if (task == null || task.excludedDayParts == null || task.excludedDayParts.isEmpty()) {
            return false;
        }
        for (int offset = 0; offset < duration; offset++) {
            int section = (startSection == null ? 0 : startSection) + offset;
            String dayPart = StringUtils.defaultIfEmpty(sectionDayPartMap.get(section), resolveDefaultDayPart(section));
            if (task.excludedDayParts.contains(dayPart)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStandardBlockStart(Integer start, int duration, List<Integer> sectionUnits) {
        if (start == null || duration <= 1) {
            return true;
        }
        List<List<Integer>> runs = splitContinuousRuns(sectionUnits);
        for (List<Integer> run : runs) {
            int index = run.indexOf(start);
            if (index < 0) {
                continue;
            }
            if (duration == 2) {
                return index % 2 == 0;
            }
            if (duration >= 4) {
                return index % 4 == 0;
            }
            return index % 2 == 0;
        }
        return false;
    }

    private List<List<Integer>> splitContinuousRuns(List<Integer> sectionUnits) {
        if (sectionUnits == null || sectionUnits.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> sortedUnits = sectionUnits.stream().distinct().sorted().collect(Collectors.toList());
        List<List<Integer>> runs = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        Integer previous = null;
        for (Integer unit : sortedUnits) {
            if (previous != null && unit != previous + 1) {
                runs.add(new ArrayList<>(current));
                current.clear();
            }
            current.add(unit);
            previous = unit;
        }
        if (!current.isEmpty()) {
            runs.add(current);
        }
        return runs;
    }

    private Long pickClassroom(LessonTask task, List<ScClassroom> classrooms, Random random) {
        List<Long> preferred = task.preferredClassroomIds == null ? Collections.emptyList()
                : task.preferredClassroomIds;
        if (task.fixedClassroom && !preferred.isEmpty()) {
            return preferred.get(0);
        }
        if (!preferred.isEmpty()) {
            return preferred.get(random.nextInt(preferred.size()));
        }
        List<ScClassroom> assignableClassrooms = classrooms.stream()
                .filter(room -> !isVirtualOpenSpaceClassroom(room))
                .collect(Collectors.toList());
        if (!assignableClassrooms.isEmpty()) {
            return assignableClassrooms.get(random.nextInt(assignableClassrooms.size())).getClassroomId();
        }
        return classrooms.get(random.nextInt(classrooms.size())).getClassroomId();
    }

    private void evaluate(GeneticSolution solution,
            List<LessonTask> tasks,
            List<ScClassroom> classrooms,
            List<ScCourseSchedule> lockedSchedules,
            Map<Long, ScClassCourse> classCourseMap,
            Map<Integer, String> sectionDayPartMap) {
        Map<String, LessonTask> taskMap = tasks.stream().collect(Collectors.toMap(item -> item.taskId, item -> item));
        Map<Long, ScClassroom> classroomMap = classrooms.stream()
                .filter(item -> item.getClassroomId() != null)
                .collect(Collectors.toMap(ScClassroom::getClassroomId, item -> item, (left, right) -> left));

        double score = 0D;
        Map<String, List<LessonAssignment>> slotMap = new HashMap<>();
        Map<Long, Set<Integer>> classDailySections = new HashMap<>();
        Map<Long, Set<Integer>> classDays = new HashMap<>();
        Map<Long, Integer> classCourseDailyCount = new HashMap<>();

        for (LessonAssignment assignment : solution.assignments.values()) {
            LessonTask task = taskMap.get(assignment.taskId);
            if (task == null) {
                continue;
            }
            score += 120D;
            if (task.excludedWeekDays != null && task.excludedWeekDays.contains(assignment.weekDay)) {
                score -= 220D;
            }
            if (isExcludedDayPartAssignment(assignment.startSection, assignment.getDuration(), task, sectionDayPartMap)) {
                score -= 220D;
            }
            if (assignment.weekDay == null || assignment.weekDay < 1 || assignment.weekDay > 7) {
                score -= 200D;
            }
            if (assignment.startSection == null || assignment.endSection == null
                    || assignment.startSection > assignment.endSection) {
                score -= 240D;
            }

            ScClassroom room = classroomMap.get(assignment.classroomId);
            if (room == null) {
                score -= 180D;
            } else {
                if (!isVirtualOpenSpaceClassroom(room)
                        && task.studentCount > 0 && room.getCapacity() != null && room.getCapacity() > 0
                        && room.getCapacity() < task.studentCount) {
                    score -= (task.studentCount - room.getCapacity()) * 6D;
                }
                if (isVirtualOpenSpaceClassroom(room)) {
                    score += 4D;
                }
                if (sameCampus(task, room)) {
                    score += 8D;
                }
                if (sameDept(task, room)) {
                    score += 14D;
                }
                if (sameRoomType(task, room)) {
                    score += 10D;
                }
            }

            String slotKey = assignment.weekDay + "-" + assignment.startSection + "-" + assignment.endSection;
            slotMap.computeIfAbsent(slotKey, key -> new ArrayList<>()).add(assignment);

            if (task.classId != null) {
                long classDailyKey = task.classId * 10 + assignment.weekDay;
                Set<Integer> occupiedSections = classDailySections.computeIfAbsent(classDailyKey,
                        key -> new LinkedHashSet<>());
                for (int section = assignment.startSection; section <= assignment.endSection; section++) {
                    occupiedSections.add(section);
                }
                classDays.computeIfAbsent(task.classId, key -> new LinkedHashSet<>()).add(assignment.weekDay);
            }
            if (task.classCourseId != null) {
                long classCourseDailyKey = task.classCourseId * 10 + assignment.weekDay;
                classCourseDailyCount.merge(classCourseDailyKey, 1, Integer::sum);
            }
        }

        for (List<LessonAssignment> sameSlotAssignments : slotMap.values()) {
            for (int leftIndex = 0; leftIndex < sameSlotAssignments.size(); leftIndex++) {
                LessonAssignment left = sameSlotAssignments.get(leftIndex);
                LessonTask leftTask = taskMap.get(left.taskId);
                for (int rightIndex = leftIndex + 1; rightIndex < sameSlotAssignments.size(); rightIndex++) {
                    LessonAssignment right = sameSlotAssignments.get(rightIndex);
                    LessonTask rightTask = taskMap.get(right.taskId);
                    if (leftTask != null && rightTask != null
                            && !isWeekOverlap(leftTask.weeksText, rightTask.weeksText)) {
                        continue;
                    }
                    if (left.classroomId != null && left.classroomId.equals(right.classroomId)
                            && !isVirtualOpenSpaceClassroomId(left.classroomId)) {
                        score -= 260D;
                    }
                    if (leftTask != null && rightTask != null) {
                        if (leftTask.teacherId != null && leftTask.teacherId.equals(rightTask.teacherId)) {
                            score -= 250D;
                        }
                        boolean allowParallelSelectionGroup = canParallelSelectionGroup(leftTask, rightTask);
                        if (leftTask.classId != null && leftTask.classId.equals(rightTask.classId)
                                && !allowParallelSelectionGroup) {
                            score -= 280D;
                        }
                        if (allowParallelSelectionGroup) {
                            score += 36D;
                        }
                        if (leftTask.classCourseId != null && leftTask.classCourseId.equals(rightTask.classCourseId)) {
                            score -= 60D;
                        }
                    }
                }
            }
        }

        for (Set<Integer> sections : classDailySections.values()) {
            if (sections.size() > 6) {
                score -= (sections.size() - 6) * 12D;
            }
        }
        for (Set<Integer> days : classDays.values()) {
            score += Math.min(days.size(), 5) * 4D;
        }
        for (Integer count : classCourseDailyCount.values()) {
            if (count > 1) {
                score -= (count - 1) * 36D;
            }
        }

        for (LessonAssignment assignment : solution.assignments.values()) {
            LessonTask task = taskMap.get(assignment.taskId);
            if (task == null) {
                continue;
            }
            for (ScCourseSchedule locked : lockedSchedules) {
                if (locked == null || !"0".equals(StringUtils.defaultIfEmpty(locked.getStatus(), "0"))) {
                    continue;
                }
                if (!isOverlap(assignment.startSection, assignment.endSection, locked.getStartSection(),
                        locked.getEndSection())) {
                    continue;
                }
                if (!Objects.equals(assignment.weekDay, locked.getWeekDay())) {
                    continue;
                }
                if (!isWeekOverlap(task.weeksText, locked.getWeeksText())) {
                    continue;
                }
                if (assignment.classroomId != null && assignment.classroomId.equals(locked.getClassroomId())
                        && !isVirtualOpenSpaceClassroomId(assignment.classroomId)) {
                    score -= 280D;
                }
                ScClassCourse lockedClassCourse = classCourseMap.get(locked.getClassCourseId());
                if (lockedClassCourse != null) {
                    if (task.teacherId != null && task.teacherId.equals(lockedClassCourse.getTeacherId())) {
                        score -= 250D;
                    }
                    if (task.classId != null && task.classId.equals(lockedClassCourse.getClassId())
                            && !canParallelSelectionGroup(task, lockedClassCourse)) {
                        score -= 280D;
                    }
                }
                if (task.classCourseId != null && task.classCourseId.equals(locked.getClassCourseId())) {
                    score -= 120D;
                }
            }
        }

        solution.fitnessScore = score;
    }

    private GeneticSolution selectParent(List<GeneticSolution> population, Random random) {
        GeneticSolution best = null;
        for (int index = 0; index < 3; index++) {
            GeneticSolution candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.fitnessScore > best.fitnessScore) {
                best = candidate;
            }
        }
        return best;
    }

    private GeneticSolution crossover(GeneticSolution parentA, GeneticSolution parentB, Random random) {
        GeneticSolution child = new GeneticSolution();
        for (Map.Entry<String, LessonAssignment> entry : parentA.assignments.entrySet()) {
            LessonAssignment source = random.nextBoolean()
                    ? entry.getValue()
                    : parentB.assignments.getOrDefault(entry.getKey(), entry.getValue());
            child.assignments.put(entry.getKey(), source.copy());
        }
        return child;
    }

    private void mutate(GeneticSolution solution,
            List<LessonTask> tasks,
            List<ScClassroom> classrooms,
            List<Integer> sectionUnits,
            double mutationRate,
            Random random,
            Map<Integer, String> sectionDayPartMap) {
        Map<String, LessonTask> taskMap = tasks.stream().collect(Collectors.toMap(item -> item.taskId, item -> item));
        for (Map.Entry<String, LessonAssignment> entry : solution.assignments.entrySet()) {
            if (random.nextDouble() > mutationRate) {
                continue;
            }
            LessonTask task = taskMap.get(entry.getKey());
            if (task == null) {
                continue;
            }
            LessonAssignment assignment = entry.getValue();
            if (task.fixedClassroom) {
                assignment.weekDay = pickWeekDay(task, random);
                assignment.startSection = pickStartSection(task, sectionUnits, random, sectionDayPartMap);
                assignment.endSection = assignment.startSection + task.expectedDuration - 1;
            } else {
                int action = random.nextInt(3);
                if (action == 0) {
                    assignment.weekDay = pickWeekDay(task, random);
                    assignment.startSection = pickStartSection(task, sectionUnits, random, sectionDayPartMap);
                    assignment.endSection = assignment.startSection + task.expectedDuration - 1;
                } else if (action == 1) {
                    assignment.classroomId = pickClassroom(task, classrooms, random);
                } else {
                    assignment.weekDay = pickWeekDay(task, random);
                    assignment.startSection = pickStartSection(task, sectionUnits, random, sectionDayPartMap);
                    assignment.endSection = assignment.startSection + task.expectedDuration - 1;
                    assignment.classroomId = pickClassroom(task, classrooms, random);
                }
            }
        }
    }

    private List<LessonAssignment> collectValidAssignments(GeneticSolution best,
            List<LessonTask> tasks,
            List<ScClassroom> classrooms,
            List<ScCourseSchedule> lockedSchedules,
            Map<Long, ScClassCourse> classCourseMap,
            Map<Integer, String> sectionDayPartMap) {
        Map<String, LessonTask> taskMap = tasks.stream().collect(Collectors.toMap(item -> item.taskId, item -> item));
        Map<Long, ScClassroom> classroomMap = classrooms.stream()
                .filter(item -> item.getClassroomId() != null)
                .collect(Collectors.toMap(ScClassroom::getClassroomId, item -> item, (left, right) -> left));
        List<LessonAssignment> orderedAssignments = new ArrayList<>(best.assignments.values());
        orderedAssignments.sort(Comparator.comparing(item -> item.taskId));

        List<LessonAssignment> accepted = new ArrayList<>();
        for (LessonAssignment assignment : orderedAssignments) {
            LessonTask task = taskMap.get(assignment.taskId);
            if (task == null || !isAssignmentValid(assignment, task, accepted, lockedSchedules, classroomMap, taskMap,
                    classCourseMap, sectionDayPartMap)) {
                continue;
            }
            accepted.add(assignment.copy());
        }
        return accepted;
    }

    private boolean isAssignmentValid(LessonAssignment assignment,
            LessonTask task,
            List<LessonAssignment> accepted,
            List<ScCourseSchedule> lockedSchedules,
            Map<Long, ScClassroom> classroomMap,
            Map<String, LessonTask> taskMap,
            Map<Long, ScClassCourse> classCourseMap,
            Map<Integer, String> sectionDayPartMap) {
        if (assignment.weekDay == null || assignment.startSection == null || assignment.endSection == null) {
            return false;
        }
        if (assignment.weekDay < 1 || assignment.weekDay > 7 || assignment.startSection > assignment.endSection) {
            return false;
        }
        if (task.excludedWeekDays != null && task.excludedWeekDays.contains(assignment.weekDay)) {
            return false;
        }
        if (isExcludedDayPartAssignment(assignment.startSection, assignment.getDuration(), task, sectionDayPartMap)) {
            return false;
        }

        ScClassroom room = classroomMap.get(assignment.classroomId);
        if (room == null) {
            return false;
        }
        if (!isVirtualOpenSpaceClassroom(room)
                && task.studentCount > 0 && room.getCapacity() != null && room.getCapacity() > 0
                && room.getCapacity() < task.studentCount) {
            return false;
        }
        if (!isVirtualOpenSpaceClassroom(room)
                && (StringUtils.contains(StringUtils.defaultIfEmpty(task.courseCategory, ""), "实训")
                || StringUtils.contains(StringUtils.defaultIfEmpty(task.courseCategory, ""), "实验"))
                && StringUtils.isNotEmpty(room.getRoomType())
                && !(StringUtils.contains(room.getRoomType(), "实验室")
                        || StringUtils.contains(room.getRoomType(), "机房"))) {
            return false;
        }

        for (LessonAssignment item : accepted) {
            if (!Objects.equals(item.weekDay, assignment.weekDay)) {
                continue;
            }
            if (!isOverlap(item.startSection, item.endSection, assignment.startSection, assignment.endSection)) {
                continue;
            }
            LessonTask current = taskMap.get(item.taskId);
            if (current == null) {
                continue;
            }
            if (!isWeekOverlap(task.weeksText, current.weeksText)) {
                continue;
            }
            if (assignment.classroomId != null && assignment.classroomId.equals(item.classroomId)
                    && !isVirtualOpenSpaceClassroomId(assignment.classroomId)) {
                return false;
            }
            if (task.teacherId != null && task.teacherId.equals(current.teacherId)) {
                return false;
            }
            if (task.classId != null && task.classId.equals(current.classId)
                    && !canParallelSelectionGroup(task, current)) {
                return false;
            }
        }

        for (ScCourseSchedule locked : lockedSchedules) {
            if (locked == null || !"0".equals(StringUtils.defaultIfEmpty(locked.getStatus(), "0"))) {
                continue;
            }
            if (!Objects.equals(assignment.weekDay, locked.getWeekDay())) {
                continue;
            }
            if (!isOverlap(assignment.startSection, assignment.endSection, locked.getStartSection(),
                    locked.getEndSection())) {
                continue;
            }
            if (!isWeekOverlap(task.weeksText, locked.getWeeksText())) {
                continue;
            }
            if (assignment.classroomId != null && assignment.classroomId.equals(locked.getClassroomId())
                    && !isVirtualOpenSpaceClassroomId(assignment.classroomId)) {
                return false;
            }
            ScClassCourse lockedClassCourse = classCourseMap.get(locked.getClassCourseId());
            if (lockedClassCourse != null) {
                if (task.teacherId != null && task.teacherId.equals(lockedClassCourse.getTeacherId())) {
                    return false;
                }
                if (task.classId != null && task.classId.equals(lockedClassCourse.getClassId())
                        && !canParallelSelectionGroup(task, lockedClassCourse)) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<Map<String, Object>> buildUnscheduledLessons(List<LessonTask> tasks,
            List<LessonAssignment> validAssignments) {
        Set<String> arrangedIds = validAssignments.stream().map(item -> item.taskId).collect(Collectors.toSet());
        List<Map<String, Object>> result = new ArrayList<>();
        for (LessonTask task : tasks) {
            if (arrangedIds.contains(task.taskId)) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("taskId", task.taskId);
            item.put("classCourseId", task.classCourseId);
            item.put("className", task.className);
            item.put("courseName", task.courseName);
            item.put("teacherName", task.teacherName);
            item.put("duration", task.expectedDuration);
            item.put("weeksText", task.weeksText);
            item.put("reason", "未在本轮进化中找到满足教师、班级、教室约束的可行时段");
            result.add(item);
        }
        return result;
    }

    private boolean canParallelSelectionGroup(LessonTask leftTask, LessonTask rightTask) {
        if (leftTask == null || rightTask == null) {
            return false;
        }
        if (!Objects.equals(leftTask.classId, rightTask.classId) || !Objects.equals(leftTask.courseId, rightTask.courseId)) {
            return false;
        }
        String leftGroupKey = buildSelectionGroupKey(leftTask.classId, leftTask.courseId, leftTask.selectionGroupCode);
        String rightGroupKey = buildSelectionGroupKey(rightTask.classId, rightTask.courseId, rightTask.selectionGroupCode);
        if (StringUtils.isEmpty(leftGroupKey) || !leftGroupKey.equals(rightGroupKey)) {
            return false;
        }
        return normalizeSelectionGroupLimit(leftTask.selectionGroupLimit) == 1
                && normalizeSelectionGroupLimit(rightTask.selectionGroupLimit) == 1;
    }

    private boolean canParallelSelectionGroup(LessonTask task, ScClassCourse classCourse) {
        if (task == null || classCourse == null) {
            return false;
        }
        if (!Objects.equals(task.classId, classCourse.getClassId()) || !Objects.equals(task.courseId, classCourse.getCourseId())) {
            return false;
        }
        String taskGroupKey = buildSelectionGroupKey(task.classId, task.courseId, task.selectionGroupCode);
        String classCourseGroupKey = buildSelectionGroupKey(classCourse.getClassId(), classCourse.getCourseId(),
                classCourse.getSelectionGroupCode());
        if (StringUtils.isEmpty(taskGroupKey) || !taskGroupKey.equals(classCourseGroupKey)) {
            return false;
        }
        return normalizeSelectionGroupLimit(task.selectionGroupLimit) == 1
                && normalizeSelectionGroupLimit(classCourse.getSelectionGroupLimit()) == 1;
    }

    private Map<String, Object> buildArrangedLessonMap(LessonAssignment assignment, ScSchoolTerm term,
            Map<String, LessonTask> taskMap,
            Map<Long, ScClassroom> classroomMap) {
        Map<String, Object> result = new LinkedHashMap<>();
        LessonTask task = taskMap.get(assignment.taskId);
        ScClassroom classroom = classroomMap.get(assignment.classroomId);
        result.put("taskId", assignment.taskId);
        result.put("termId", term.getTermId());
        result.put("termName", term.getTermName());
        result.put("weekDay", assignment.weekDay);
        result.put("startSection", assignment.startSection);
        result.put("endSection", assignment.endSection);
        result.put("classroomId", assignment.classroomId);
        if (task != null) {
            result.put("classCourseId", task.classCourseId);
            result.put("className", task.className);
            result.put("courseName", task.courseName);
            result.put("teacherName", task.teacherName);
            result.put("weeksText", task.weeksText);
            result.put("weeksJson", buildWeeksJsonFromText(task.weeksText));
            result.put("studentCount", task.studentCount);
            result.put("courseCategory", task.courseCategory);
            result.put("excludedWeekDays", task.excludedWeekDays);
            result.put("excludedDayParts", task.excludedDayParts);
        }
        if (classroom != null) {
            result.put("classroomName", classroom.getClassroomName());
            result.put("buildingName", classroom.getBuildingName());
            result.put("campusName", classroom.getCampusName());
            result.put("roomType", classroom.getRoomType());
            result.put("capacity", classroom.getCapacity());
        }
        result.put("reasons", buildRoomReasonTags(task, classroom));
        return result;
    }

    private List<String> buildRoomReasonTags(LessonTask task, ScClassroom classroom) {
        List<String> reasons = new ArrayList<>();
        if (task == null || classroom == null) {
            return reasons;
        }
        if (isVirtualOpenSpaceClassroom(classroom)) {
            reasons.add("空场地");
            return reasons;
        }
        if (sameDept(task, classroom)) {
            reasons.add("同部门优先");
        }
        if (sameCampus(task, classroom)) {
            reasons.add("同校区优先");
        }
        if (sameRoomType(task, classroom)) {
            reasons.add("类型匹配");
        }
        if (task.studentCount > 0 && classroom.getCapacity() != null && classroom.getCapacity() >= task.studentCount) {
            reasons.add("容量匹配");
        }
        if (reasons.isEmpty() && task.studentCount > 0 && classroom.getCapacity() != null) {
            reasons.add("容量可承载");
        }
        return reasons;
    }

    private boolean hasVirtualOpenSpaceSelection(Map<Long, CourseScheduleAutoArrangeItemDto> itemConfigMap) {
        if (itemConfigMap == null || itemConfigMap.isEmpty()) {
            return false;
        }
        return itemConfigMap.values().stream()
                .filter(Objects::nonNull)
                .map(CourseScheduleAutoArrangeItemDto::getClassroomId)
                .anyMatch(this::isVirtualOpenSpaceClassroomId);
    }

    private ScClassroom createVirtualOpenSpaceClassroom() {
        ScClassroom classroom = new ScClassroom();
        classroom.setClassroomId(VIRTUAL_OPEN_SPACE_CLASSROOM_ID);
        classroom.setClassroomName(VIRTUAL_OPEN_SPACE_CLASSROOM_NAME);
        classroom.setRoomType(VIRTUAL_OPEN_SPACE_ROOM_TYPE);
        classroom.setCapacity(Integer.MAX_VALUE);
        classroom.setStatus("0");
        classroom.setSortOrder(Integer.MAX_VALUE);
        return classroom;
    }

    private boolean isVirtualOpenSpaceClassroom(ScClassroom classroom) {
        return classroom != null && isVirtualOpenSpaceClassroomId(classroom.getClassroomId());
    }

    private boolean isVirtualOpenSpaceClassroomId(Long classroomId) {
        return classroomId != null && classroomId.longValue() == VIRTUAL_OPEN_SPACE_CLASSROOM_ID;
    }

    private boolean isOverlap(Integer leftStart, Integer leftEnd, Integer rightStart, Integer rightEnd) {
        int a = leftStart == null ? 0 : leftStart;
        int b = leftEnd == null ? a : leftEnd;
        int c = rightStart == null ? 0 : rightStart;
        int d = rightEnd == null ? c : rightEnd;
        return Math.max(a, c) <= Math.min(b, d);
    }

    private boolean isWeekOverlap(String leftWeeks, String rightWeeks) {
        Set<Integer> leftSet = extractWeeks(leftWeeks);
        Set<Integer> rightSet = extractWeeks(rightWeeks);
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

    private String buildSelectionGroupKey(ScClassCourse item) {
        if (item == null) {
            return null;
        }
        return buildSelectionGroupKey(item.getClassId(), item.getCourseId(), item.getSelectionGroupCode());
    }

    private String buildSelectionGroupKey(Long classId, Long courseId, String selectionGroupCode) {
        String normalizedGroupCode = normalizeSelectionGroupCode(selectionGroupCode);
        if (classId == null || courseId == null || StringUtils.isEmpty(normalizedGroupCode)) {
            return null;
        }
        return classId + ":" + courseId + ":" + normalizedGroupCode;
    }

    private String normalizeSelectionGroupCode(String selectionGroupCode) {
        String normalized = StringUtils.trimToEmpty(selectionGroupCode);
        return StringUtils.isEmpty(normalized) ? null : normalized;
    }

    private int normalizeSelectionGroupLimit(Integer selectionGroupLimit) {
        return selectionGroupLimit == null || selectionGroupLimit <= 0 ? 1 : selectionGroupLimit;
    }

    private String resolveDisplayedCourseName(ScClassCourse classCourse) {
        if (classCourse == null) {
            return null;
        }
        String baseCourseName = StringUtils.trimToEmpty(classCourse.getCourseName());
        String selectionOptionName = StringUtils.trimToEmpty(classCourse.getSelectionOptionName());
        if (StringUtils.isEmpty(selectionOptionName) || StringUtils.equals(baseCourseName, selectionOptionName)) {
            return StringUtils.defaultIfEmpty(baseCourseName, selectionOptionName);
        }
        if (StringUtils.isEmpty(baseCourseName)) {
            return selectionOptionName;
        }
        return baseCourseName + " / " + selectionOptionName;
    }

    private Set<Integer> extractWeeks(String weeksText) {
        Set<Integer> weekSet = new LinkedHashSet<>();
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

    private static class LessonTask {
        private String taskId;
        private Long classCourseId;
        private Long classId;
        private Long courseId;
        private String className;
        private String courseName;
        private String courseCategory;
        private Long teacherId;
        private String teacherName;
        private String campusName;
        private Long openDeptId;
        private int expectedDuration;
        private int studentCount;
        private int priorityScore;
        private String weeksText;
        private String selectionGroupCode;
        private int selectionGroupLimit;
        private List<Long> preferredClassroomIds = new ArrayList<>();
        private Set<Integer> excludedWeekDays = new LinkedHashSet<>();
        private Set<String> excludedDayParts = new LinkedHashSet<>();
        private boolean fixedClassroom;
    }

    private static class LessonTaskSeed {
        private final int duration;
        private final String weeksText;

        private LessonTaskSeed(int duration, String weeksText) {
            this.duration = duration;
            this.weeksText = weeksText;
        }
    }

    private static class LessonAssignment {
        private String taskId;
        private Integer weekDay;
        private Integer startSection;
        private Integer endSection;
        private Long classroomId;

        private int getDuration() {
            int start = startSection == null ? 0 : startSection;
            int end = endSection == null ? start : endSection;
            return Math.max(1, end - start + 1);
        }

        private LessonAssignment copy() {
            LessonAssignment result = new LessonAssignment();
            result.taskId = this.taskId;
            result.weekDay = this.weekDay;
            result.startSection = this.startSection;
            result.endSection = this.endSection;
            result.classroomId = this.classroomId;
            return result;
        }
    }

    private static class GeneticSolution {
        private Map<String, LessonAssignment> assignments = new LinkedHashMap<>();
        private double fitnessScore = Double.NEGATIVE_INFINITY;

        private GeneticSolution copy() {
            GeneticSolution result = new GeneticSolution();
            result.fitnessScore = this.fitnessScore;
            this.assignments.forEach((key, value) -> result.assignments.put(key, value.copy()));
            return result;
        }
    }
}
