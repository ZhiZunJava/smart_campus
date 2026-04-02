package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.ScStudentScore;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.ScStudentScoreImportDto;
import com.smart.system.domain.dto.ScStudentScorePublishDto;
import com.smart.system.mapper.ScStudentScoreMapper;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScCourseStudentService;
import com.smart.system.service.IScExamPaperService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.IScStudentScoreService;
import com.smart.system.service.IScUserProfileService;

@Service
public class ScStudentScoreServiceImpl implements IScStudentScoreService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private ScStudentScoreMapper scStudentScoreMapper;
    @Autowired
    private IScClassCourseService scClassCourseService;
    @Autowired
    private IScCourseStudentService scCourseStudentService;
    @Autowired
    private IScUserProfileService scUserProfileService;
    @Autowired
    private IScSchoolTermService scSchoolTermService;
    @Autowired
    private IScExamPaperService scExamPaperService;
    @Autowired
    private IScExamRecordService scExamRecordService;

    @Override
    public ScStudentScore selectScStudentScoreByScoreId(Long scoreId) {
        return scStudentScoreMapper.selectScStudentScoreByScoreId(scoreId);
    }

    @Override
    public List<ScStudentScore> selectScStudentScoreList(ScStudentScore scStudentScore) {
        return scStudentScoreMapper.selectScStudentScoreList(scStudentScore);
    }

    @Override
    public int insertScStudentScore(ScStudentScore scStudentScore) {
        if (scStudentScore == null || scStudentScore.getClassCourseId() == null || scStudentScore.getStudentUserId() == null) {
            throw new ServiceException("班级课程和学生不能为空");
        }
        ScClassCourse classCourse = requireClassCourse(scStudentScore.getClassCourseId());
        ensureNoDuplicate(scStudentScore.getClassCourseId(), scStudentScore.getStudentUserId(), null);
        fillScoreContext(scStudentScore, classCourse);
        applyDefaultWeights(scStudentScore, classCourse);
        rebuildCalculatedFields(scStudentScore);
        int rows = scStudentScoreMapper.insertScStudentScore(scStudentScore);
        recalculateRanking(scStudentScore.getClassCourseId());
        return rows;
    }

    @Override
    public int updateScStudentScore(ScStudentScore scStudentScore) {
        if (scStudentScore == null || scStudentScore.getScoreId() == null) {
            throw new ServiceException("成绩记录不存在");
        }
        ScStudentScore exists = requireScore(scStudentScore.getScoreId());
        ScClassCourse classCourse = requireClassCourse(exists.getClassCourseId());
        mergeEditableFields(exists, scStudentScore);
        applyDefaultWeights(exists, classCourse);
        rebuildCalculatedFields(exists);
        int rows = scStudentScoreMapper.updateScStudentScore(exists);
        recalculateRanking(exists.getClassCourseId());
        return rows;
    }

    @Override
    public int deleteScStudentScoreByScoreIds(Long[] scoreIds) {
        if (scoreIds == null || scoreIds.length == 0) {
            return 0;
        }
        Set<Long> classCourseIds = new LinkedHashSet<>();
        for (Long scoreId : scoreIds) {
            ScStudentScore item = scStudentScoreMapper.selectScStudentScoreByScoreId(scoreId);
            if (item != null && item.getClassCourseId() != null) {
                classCourseIds.add(item.getClassCourseId());
            }
        }
        int rows = scStudentScoreMapper.deleteScStudentScoreByScoreIds(scoreIds);
        classCourseIds.forEach(this::recalculateRanking);
        return rows;
    }

    @Override
    public int deleteScStudentScoreByScoreId(Long scoreId) {
        if (scoreId == null) {
            return 0;
        }
        ScStudentScore item = scStudentScoreMapper.selectScStudentScoreByScoreId(scoreId);
        int rows = scStudentScoreMapper.deleteScStudentScoreByScoreId(scoreId);
        if (item != null && item.getClassCourseId() != null) {
            recalculateRanking(item.getClassCourseId());
        }
        return rows;
    }

    @Override
    public Map<String, Object> buildScoreOverview(ScStudentScore query) {
        List<ScStudentScore> list = scStudentScoreMapper.selectScStudentScoreList(query == null ? new ScStudentScore() : query);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCount", list.size());
        result.put("publishedCount", list.stream().filter(item -> "1".equals(item.getPublishStatus())).count());
        result.put("draftCount", list.stream().filter(item -> !"1".equals(item.getPublishStatus())).count());
        result.put("passCount", list.stream().filter(item -> "1".equals(item.getPassFlag())).count());
        result.put("excellentCount", list.stream().filter(item -> safeScore(item.getTotalScore()).compareTo(BigDecimal.valueOf(90)) >= 0).count());
        result.put("warningCount", list.stream().filter(item -> safeScore(item.getTotalScore()).compareTo(BigDecimal.valueOf(60)) < 0).count());
        result.put("avgTotalScore", average(list.stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList())));
        result.put("avgExamScore", average(list.stream().map(ScStudentScore::getExamScore).collect(Collectors.toList())));
        result.put("avgGradePoint", average(list.stream().map(ScStudentScore::getGradePoint).collect(Collectors.toList())));
        result.put("courseStats", buildCourseStats(list));
        result.put("termStats", buildTermStats(list));
        result.put("scoreBands", buildScoreBands(list));
        return result;
    }

    @Override
    public Map<String, Object> initScoresByClassCourse(Long classCourseId, String operator) {
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        List<StudentCandidate> candidates = resolveCandidates(classCourse);
        int inserted = 0;
        int skipped = 0;
        for (StudentCandidate candidate : candidates) {
            if (candidate == null || candidate.getStudentUserId() == null) {
                continue;
            }
            if (findScoreByClassCourseAndStudent(classCourseId, candidate.getStudentUserId()) != null) {
                skipped++;
                continue;
            }
            ScStudentScore score = new ScStudentScore();
            score.setClassCourseId(classCourseId);
            score.setStudentUserId(candidate.getStudentUserId());
            score.setCreateBy(operator);
            score.setStatus("0");
            score.setPublishStatus("0");
            score.setTeacherComment("");
            fillScoreContext(score, classCourse);
            applyDefaultWeights(score, classCourse);
            rebuildCalculatedFields(score);
            scStudentScoreMapper.insertScStudentScore(score);
            inserted++;
        }
        Map<String, Object> syncResult = syncExamScores(classCourseId, operator);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classCourseId", classCourseId);
        result.put("className", classCourse.getClassName());
        result.put("courseName", classCourse.getCourseName());
        result.put("studentCount", candidates.size());
        result.put("insertedCount", inserted);
        result.put("skippedCount", skipped);
        result.put("syncedCount", syncResult.get("updatedCount"));
        return result;
    }

    @Override
    public Map<String, Object> syncExamScores(Long classCourseId, String operator) {
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        ScSchoolTerm term = classCourse.getTermId() == null ? null : scSchoolTermService.selectScSchoolTermByTermId(classCourse.getTermId());
        List<ScStudentScore> scores = listByClassCourseId(classCourseId);
        List<ScExamPaper> papers = listCoursePapers(classCourse.getCourseId());
        Set<Long> paperIds = papers.stream().map(ScExamPaper::getPaperId).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        int updated = 0;
        for (ScStudentScore score : scores) {
            if (score == null || score.getStudentUserId() == null) {
                continue;
            }
            List<ScExamRecord> records = listStudentCourseRecords(score.getStudentUserId(), paperIds, term);
            List<ScExamRecord> submittedRecords = records.stream()
                    .filter(item -> "SUBMITTED".equalsIgnoreCase(item.getExamStatus()))
                    .collect(Collectors.toList());
            BigDecimal examAvgScore = average(submittedRecords.stream().map(ScExamRecord::getScore).collect(Collectors.toList()));
            score.setExamRecordCount(submittedRecords.size());
            score.setExamAvgScore(examAvgScore);
            score.setExamScore(examAvgScore);
            score.setSyncTime(new Date());
            score.setUpdateBy(operator);
            rebuildCalculatedFields(score);
            scStudentScoreMapper.updateScStudentScore(score);
            updated++;
        }
        recalculateRanking(classCourseId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classCourseId", classCourseId);
        result.put("courseName", classCourse.getCourseName());
        result.put("updatedCount", updated);
        result.put("paperCount", paperIds.size());
        return result;
    }

    @Override
    public Map<String, Object> publishScores(ScStudentScorePublishDto dto, String operator) {
        if (dto == null) {
            throw new ServiceException("发布参数不能为空");
        }
        String publishStatus = "1".equals(dto.getPublishStatus()) ? "1" : "0";
        List<ScStudentScore> targetList = new ArrayList<>();
        if (dto.getScoreIds() != null && dto.getScoreIds().length > 0) {
            for (Long scoreId : dto.getScoreIds()) {
                ScStudentScore score = scStudentScoreMapper.selectScStudentScoreByScoreId(scoreId);
                if (score != null) {
                    targetList.add(score);
                }
            }
        } else if (dto.getClassCourseId() != null) {
            targetList.addAll(listByClassCourseId(dto.getClassCourseId()));
        }
        int updated = 0;
        Date now = new Date();
        for (ScStudentScore item : targetList) {
            item.setPublishStatus(publishStatus);
            item.setPublishBy("1".equals(publishStatus) ? operator : "");
            item.setPublishTime("1".equals(publishStatus) ? now : null);
            item.setUpdateBy(operator);
            scStudentScoreMapper.updateScStudentScore(item);
            updated++;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updatedCount", updated);
        result.put("publishStatus", publishStatus);
        result.put("published", "1".equals(publishStatus));
        return result;
    }

    @Override
    public Map<String, Object> saveBatchScores(List<ScStudentScore> scores, String operator) {
        if (scores == null || scores.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> classCourseIds = new LinkedHashSet<>();
        int updated = 0;
        for (ScStudentScore item : scores) {
            if (item == null || item.getScoreId() == null) {
                continue;
            }
            ScStudentScore exists = requireScore(item.getScoreId());
            ScClassCourse classCourse = requireClassCourse(exists.getClassCourseId());
            mergeEditableFields(exists, item);
            exists.setUpdateBy(operator);
            applyDefaultWeights(exists, classCourse);
            rebuildCalculatedFields(exists);
            scStudentScoreMapper.updateScStudentScore(exists);
            classCourseIds.add(exists.getClassCourseId());
            updated++;
        }
        classCourseIds.forEach(this::recalculateRanking);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updatedCount", updated);
        result.put("classCourseCount", classCourseIds.size());
        return result;
    }

    @Override
    public Map<String, Object> importStudentScores(Long classCourseId, List<ScStudentScoreImportDto> rows, boolean updateSupport, String operator) {
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException("导入数据不能为空");
        }
        List<StudentCandidate> candidates = resolveCandidates(classCourse);
        Map<Long, StudentCandidate> userIdMap = candidates.stream()
                .filter(item -> item.getStudentUserId() != null)
                .collect(Collectors.toMap(StudentCandidate::getStudentUserId, item -> item, (left, right) -> left, LinkedHashMap::new));
        Map<String, StudentCandidate> studentNoMap = candidates.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getStudentNo()))
                .collect(Collectors.toMap(item -> normalizeText(item.getStudentNo()), item -> item, (left, right) -> left, LinkedHashMap::new));
        Map<String, List<StudentCandidate>> studentNameMap = candidates.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getStudentName()))
                .collect(Collectors.groupingBy(item -> normalizeText(item.getStudentName()), LinkedHashMap::new, Collectors.toList()));

        int successCount = 0;
        int skipCount = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            ScStudentScoreImportDto row = rows.get(i);
            if (isImportRowEmpty(row)) {
                continue;
            }
            StudentCandidate candidate = resolveImportCandidate(row, userIdMap, studentNoMap, studentNameMap);
            if (candidate == null || candidate.getStudentUserId() == null) {
                errors.add("第" + (i + 2) + "行：无法匹配学生，请检查学生用户ID/学号/学生姓名");
                skipCount++;
                continue;
            }

            ScStudentScore target = findScoreByClassCourseAndStudent(classCourseId, candidate.getStudentUserId());
            boolean exists = target != null;
            if (exists && !updateSupport) {
                skipCount++;
                continue;
            }
            if (!exists) {
                target = new ScStudentScore();
                target.setClassCourseId(classCourseId);
                target.setStudentUserId(candidate.getStudentUserId());
                target.setCreateBy(operator);
                target.setStatus("0");
                target.setPublishStatus("0");
                fillScoreContext(target, classCourse);
                applyDefaultWeights(target, classCourse);
            } else {
                target.setUpdateBy(operator);
            }
            applyImportFields(target, row);
            rebuildCalculatedFields(target);
            if (exists) {
                scStudentScoreMapper.updateScStudentScore(target);
            } else {
                scStudentScoreMapper.insertScStudentScore(target);
            }
            successCount++;
        }
        recalculateRanking(classCourseId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classCourseId", classCourseId);
        result.put("successCount", successCount);
        result.put("skipCount", skipCount);
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        return result;
    }

    @Override
    public List<ScStudentScoreImportDto> buildImportTemplateRows(Long classCourseId) {
        ScClassCourse classCourse = requireClassCourse(classCourseId);
        List<StudentCandidate> candidates = resolveCandidates(classCourse).stream()
                .sorted(Comparator
                        .comparing(StudentCandidate::getStudentNo, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StudentCandidate::getStudentName, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        List<ScStudentScoreImportDto> result = new ArrayList<>();
        for (StudentCandidate candidate : candidates) {
            if (candidate == null || candidate.getStudentUserId() == null) {
                continue;
            }
            ScStudentScore exists = findScoreByClassCourseAndStudent(classCourseId, candidate.getStudentUserId());
            ScStudentScoreImportDto row = new ScStudentScoreImportDto();
            row.setStudentUserId(candidate.getStudentUserId());
            row.setStudentNo(candidate.getStudentNo());
            row.setStudentName(candidate.getStudentName());
            if (exists != null) {
                row.setUsualScore(exists.getUsualScore());
                row.setAttendanceScore(exists.getAttendanceScore());
                row.setHomeworkScore(exists.getHomeworkScore());
                row.setLabScore(exists.getLabScore());
                row.setExamScore(exists.getExamScore());
                row.setBonusScore(exists.getBonusScore());
                row.setPenaltyScore(exists.getPenaltyScore());
                row.setTeacherComment(exists.getTeacherComment());
                row.setRemark(exists.getRemark());
            }
            result.add(row);
        }
        return result;
    }

    @Override
    public List<ScStudentScore> selectPortalStudentScoreList(Long userId, Long termId, Long courseId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        ScStudentScore query = new ScStudentScore();
        query.setStudentUserId(userId);
        query.setTermId(termId);
        query.setCourseId(courseId);
        query.setPublishStatus("1");
        query.setStatus("0");
        return scStudentScoreMapper.selectScStudentScoreList(query);
    }

    @Override
    public Map<String, Object> buildPortalScoreOverview(Long userId, Long termId) {
        List<ScStudentScore> list = selectPortalStudentScoreList(userId, termId, null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("courseCount", list.size());
        result.put("avgScore", average(list.stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList())));
        result.put("bestScore", list.stream().map(ScStudentScore::getTotalScore).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
        result.put("passCount", list.stream().filter(item -> "1".equals(item.getPassFlag())).count());
        result.put("failedCount", list.stream().filter(item -> !"1".equals(item.getPassFlag())).count());
        result.put("creditTotal", list.stream().map(ScStudentScore::getCredits).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        result.put("creditEarned", list.stream()
                .filter(item -> "1".equals(item.getPassFlag()))
                .map(ScStudentScore::getCredits)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        result.put("gradeDistribution", buildGradeDistribution(list));
        return result;
    }

    @Override
    public Map<String, Object> buildPortalScoreAnalytics(Long userId, Long termId, Long courseId) {
        List<ScStudentScore> filteredList = selectPortalStudentScoreList(userId, termId, courseId);
        List<ScStudentScore> trendSource = selectPortalStudentScoreList(userId, null, courseId);
        String termName = filteredList.stream()
                .map(ScStudentScore::getTermName)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(resolveTermName(termId));
        String courseName = filteredList.stream()
                .map(ScStudentScore::getCourseName)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse("");

        Map<String, Object> scope = new LinkedHashMap<>();
        scope.put("userId", userId);
        scope.put("termId", termId);
        scope.put("termName", termName);
        scope.put("courseId", courseId);
        scope.put("courseName", courseName);
        scope.put("courseCount", filteredList.size());
        scope.put("scopeLabel", buildPortalScopeLabel(termName, courseName));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scope", scope);
        result.put("termTrend", buildPortalTermTrend(trendSource));
        result.put("courseSeries", buildPortalCourseSeries(filteredList));
        result.put("componentSeries", buildPortalComponentSeries(filteredList));
        result.put("scoreBands", buildScoreBands(filteredList));
        result.put("gradeDistribution", buildGradeDistribution(filteredList));
        result.put("passDistribution", buildPortalPassDistribution(filteredList));
        result.put("insights", buildPortalInsights(filteredList, termName, courseName));
        result.put("aiContextPrompt", buildPortalAiContext(filteredList, termName, courseName, trendSource));
        return result;
    }

    @Override
    public Map<String, Object> buildPortalScoreDetail(Long userId, Long classCourseId) {
        if (userId == null || classCourseId == null) {
            return Collections.emptyMap();
        }
        ScStudentScore query = new ScStudentScore();
        query.setStudentUserId(userId);
        query.setClassCourseId(classCourseId);
        query.setPublishStatus("1");
        query.setStatus("0");
        List<ScStudentScore> list = scStudentScoreMapper.selectScStudentScoreList(query);
        if (list.isEmpty()) {
            return Collections.emptyMap();
        }
        ScStudentScore score = list.get(0);
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        ScSchoolTerm term = score.getTermId() == null ? null : scSchoolTermService.selectScSchoolTermByTermId(score.getTermId());
        Set<Long> paperIds = listCoursePapers(score.getCourseId()).stream().map(ScExamPaper::getPaperId).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<ScExamRecord> records = listStudentCourseRecords(userId, paperIds, term);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("score", score);
        result.put("course", classCourse);
        result.put("examRecords", records.stream().limit(20).collect(Collectors.toList()));
        result.put("examSummary", buildExamSummary(records));
        return result;
    }

    private ScClassCourse requireClassCourse(Long classCourseId) {
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null) {
            throw new ServiceException("班级课程不存在");
        }
        return classCourse;
    }

    private ScStudentScore requireScore(Long scoreId) {
        ScStudentScore score = scStudentScoreMapper.selectScStudentScoreByScoreId(scoreId);
        if (score == null) {
            throw new ServiceException("成绩记录不存在");
        }
        return score;
    }

    private void ensureNoDuplicate(Long classCourseId, Long studentUserId, Long excludeScoreId) {
        ScStudentScore query = new ScStudentScore();
        query.setClassCourseId(classCourseId);
        query.setStudentUserId(studentUserId);
        List<ScStudentScore> list = scStudentScoreMapper.selectScStudentScoreList(query);
        boolean duplicated = list.stream().anyMatch(item -> !Objects.equals(item.getScoreId(), excludeScoreId));
        if (duplicated) {
            throw new ServiceException("该学生在当前班级课程下已存在成绩台账");
        }
    }

    private void fillScoreContext(ScStudentScore score, ScClassCourse classCourse) {
        if (score == null || classCourse == null) {
            return;
        }
        score.setClassCourseId(classCourse.getId());
        score.setTermId(classCourse.getTermId());
        score.setCourseId(classCourse.getCourseId());
        score.setClassId(classCourse.getClassId());
        if (StringUtils.isEmpty(score.getStatus())) {
            score.setStatus("0");
        }
        if (StringUtils.isEmpty(score.getPublishStatus())) {
            score.setPublishStatus("0");
        }
        if (score.getTeacherComment() == null) {
            score.setTeacherComment("");
        }
    }

    private void mergeEditableFields(ScStudentScore target, ScStudentScore source) {
        target.setUsualScore(source.getUsualScore());
        target.setAttendanceScore(source.getAttendanceScore());
        target.setHomeworkScore(source.getHomeworkScore());
        target.setLabScore(source.getLabScore());
        target.setExamScore(source.getExamScore());
        target.setExamAvgScore(source.getExamAvgScore() == null ? target.getExamAvgScore() : source.getExamAvgScore());
        target.setBonusScore(source.getBonusScore());
        target.setPenaltyScore(source.getPenaltyScore());
        target.setUsualWeight(source.getUsualWeight());
        target.setAttendanceWeight(source.getAttendanceWeight());
        target.setHomeworkWeight(source.getHomeworkWeight());
        target.setLabWeight(source.getLabWeight());
        target.setExamWeight(source.getExamWeight());
        target.setTeacherComment(source.getTeacherComment());
        if (source.getPublishStatus() != null) {
            target.setPublishStatus(source.getPublishStatus());
        }
        if (source.getPublishTime() != null || source.getPublishStatus() != null) {
            target.setPublishTime(source.getPublishTime());
        }
        if (source.getPublishBy() != null || source.getPublishStatus() != null) {
            target.setPublishBy(source.getPublishBy());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        target.setRemark(source.getRemark());
    }

    private void applyDefaultWeights(ScStudentScore score, ScClassCourse classCourse) {
        String assessmentType = StringUtils.defaultIfEmpty(classCourse == null ? null : classCourse.getAssessmentType(), "")
                .trim().toLowerCase(Locale.ROOT);
        BigDecimal usual = BigDecimal.valueOf(20);
        BigDecimal attendance = BigDecimal.valueOf(10);
        BigDecimal homework = BigDecimal.valueOf(20);
        BigDecimal lab = BigDecimal.valueOf(10);
        BigDecimal exam = BigDecimal.valueOf(40);
        if (assessmentType.contains("考查")) {
            usual = BigDecimal.valueOf(30);
            attendance = BigDecimal.valueOf(15);
            homework = BigDecimal.valueOf(25);
            lab = BigDecimal.valueOf(10);
            exam = BigDecimal.valueOf(20);
        } else if (assessmentType.contains("论文") || assessmentType.contains("报告")) {
            usual = BigDecimal.valueOf(20);
            attendance = BigDecimal.valueOf(10);
            homework = BigDecimal.valueOf(30);
            lab = BigDecimal.valueOf(20);
            exam = BigDecimal.valueOf(20);
        }
        score.setUsualWeight(defaultIfNull(score.getUsualWeight(), usual));
        score.setAttendanceWeight(defaultIfNull(score.getAttendanceWeight(), attendance));
        score.setHomeworkWeight(defaultIfNull(score.getHomeworkWeight(), homework));
        score.setLabWeight(defaultIfNull(score.getLabWeight(), lab));
        score.setExamWeight(defaultIfNull(score.getExamWeight(), exam));
    }

    private void rebuildCalculatedFields(ScStudentScore score) {
        BigDecimal usualScore = normalizeComponentScore(score.getUsualScore());
        BigDecimal attendanceScore = normalizeComponentScore(score.getAttendanceScore());
        BigDecimal homeworkScore = normalizeComponentScore(score.getHomeworkScore());
        BigDecimal labScore = normalizeComponentScore(score.getLabScore());
        BigDecimal examScore = normalizeComponentScore(score.getExamScore());
        BigDecimal bonusScore = normalizeBonusScore(score.getBonusScore());
        BigDecimal penaltyScore = normalizeBonusScore(score.getPenaltyScore());
        BigDecimal usualWeight = normalizeWeight(score.getUsualWeight());
        BigDecimal attendanceWeight = normalizeWeight(score.getAttendanceWeight());
        BigDecimal homeworkWeight = normalizeWeight(score.getHomeworkWeight());
        BigDecimal labWeight = normalizeWeight(score.getLabWeight());
        BigDecimal examWeight = normalizeWeight(score.getExamWeight());
        BigDecimal weightTotal = usualWeight.add(attendanceWeight).add(homeworkWeight).add(labWeight).add(examWeight);

        BigDecimal total = BigDecimal.ZERO;
        if (weightTotal.compareTo(BigDecimal.ZERO) > 0) {
            total = total.add(usualScore.multiply(usualWeight))
                    .add(attendanceScore.multiply(attendanceWeight))
                    .add(homeworkScore.multiply(homeworkWeight))
                    .add(labScore.multiply(labWeight))
                    .add(examScore.multiply(examWeight))
                    .divide(weightTotal, 2, RoundingMode.HALF_UP);
        }
        total = total.add(bonusScore).subtract(penaltyScore).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        score.setUsualScore(usualScore);
        score.setAttendanceScore(attendanceScore);
        score.setHomeworkScore(homeworkScore);
        score.setLabScore(labScore);
        score.setExamScore(examScore);
        score.setExamAvgScore(normalizeComponentScore(score.getExamAvgScore()));
        score.setBonusScore(bonusScore);
        score.setPenaltyScore(penaltyScore);
        score.setUsualWeight(usualWeight);
        score.setAttendanceWeight(attendanceWeight);
        score.setHomeworkWeight(homeworkWeight);
        score.setLabWeight(labWeight);
        score.setExamWeight(examWeight);
        score.setTotalScore(total);
        score.setPassFlag(total.compareTo(BigDecimal.valueOf(60)) >= 0 ? "1" : "0");
        score.setGradeLevel(resolveGradeLevel(total));
        score.setGradePoint(resolveGradePoint(total));
        if (score.getExamRecordCount() == null) {
            score.setExamRecordCount(0);
        }
        if (StringUtils.isEmpty(score.getStatus())) {
            score.setStatus("0");
        }
        if (StringUtils.isEmpty(score.getPublishStatus())) {
            score.setPublishStatus("0");
        }
    }

    private void recalculateRanking(Long classCourseId) {
        if (classCourseId == null) {
            return;
        }
        List<ScStudentScore> list = listByClassCourseId(classCourseId).stream()
                .sorted(Comparator
                        .comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScStudentScore::getExamScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScStudentScore::getStudentUserId, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        int size = list.size();
        BigDecimal lastTotal = null;
        BigDecimal lastExam = null;
        int currentRank = 0;
        for (int i = 0; i < list.size(); i++) {
            ScStudentScore item = list.get(i);
            BigDecimal currentTotal = safeScore(item.getTotalScore());
            BigDecimal currentExam = safeScore(item.getExamScore());
            if (lastTotal == null || currentTotal.compareTo(lastTotal) != 0 || currentExam.compareTo(lastExam) != 0) {
                currentRank = i + 1;
                lastTotal = currentTotal;
                lastExam = currentExam;
            }
            item.setRankNo(currentRank);
            item.setPercentile(size == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf((size - currentRank + 1D) * 100D / size).setScale(2, RoundingMode.HALF_UP));
            scStudentScoreMapper.updateScStudentScore(item);
        }
    }

    private List<ScStudentScore> listByClassCourseId(Long classCourseId) {
        ScStudentScore query = new ScStudentScore();
        query.setClassCourseId(classCourseId);
        query.setStatus("0");
        return scStudentScoreMapper.selectScStudentScoreList(query);
    }

    private List<ScExamPaper> listCoursePapers(Long courseId) {
        if (courseId == null) {
            return Collections.emptyList();
        }
        ScExamPaper query = new ScExamPaper();
        query.setCourseId(courseId);
        query.setStatus("0");
        return scExamPaperService.selectScExamPaperList(query).stream()
                .filter(item -> !"SUB".equalsIgnoreCase(item.getPaperLevel()))
                .collect(Collectors.toList());
    }

    private List<ScExamRecord> listStudentCourseRecords(Long userId, Set<Long> paperIds, ScSchoolTerm term) {
        if (userId == null || paperIds == null || paperIds.isEmpty()) {
            return Collections.emptyList();
        }
        ScExamRecord query = new ScExamRecord();
        query.setUserId(userId);
        return scExamRecordService.selectScExamRecordList(query).stream()
                .filter(item -> item.getPaperId() != null && paperIds.contains(item.getPaperId()))
                .filter(item -> withinTerm(item, term))
                .sorted(Comparator.comparing(ScExamRecord::getSubmitTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScExamRecord::getStartTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean withinTerm(ScExamRecord record, ScSchoolTerm term) {
        if (record == null || term == null) {
            return true;
        }
        Date checkTime = record.getSubmitTime() != null ? record.getSubmitTime() : record.getStartTime();
        if (checkTime == null) {
            return true;
        }
        if (term.getStartDate() != null && checkTime.before(term.getStartDate())) {
            return false;
        }
        if (term.getEndDate() != null && checkTime.after(term.getEndDate())) {
            return false;
        }
        return true;
    }

    private List<StudentCandidate> resolveCandidates(ScClassCourse classCourse) {
        Map<Long, StudentCandidate> result = new LinkedHashMap<>();
        ScCourseStudent courseStudentQuery = new ScCourseStudent();
        courseStudentQuery.setCourseId(classCourse.getCourseId());
        courseStudentQuery.setClassId(classCourse.getClassId());
        courseStudentQuery.setStatus("0");
        List<ScCourseStudent> courseStudents = scCourseStudentService.selectScCourseStudentList(courseStudentQuery);
        if (courseStudents != null && !courseStudents.isEmpty()) {
            for (ScCourseStudent item : courseStudents) {
                if (item == null || item.getStudentUserId() == null) {
                    continue;
                }
                result.put(item.getStudentUserId(), new StudentCandidate(item.getStudentUserId(), item.getStudentName(), item.getStudentNo()));
            }
            return new ArrayList<>(result.values());
        }

        ScUserProfile profileQuery = new ScUserProfile();
        profileQuery.setClassId(classCourse.getClassId());
        profileQuery.setUserType("student");
        profileQuery.setStatus("0");
        List<ScUserProfile> profiles = scUserProfileService.selectScUserProfileList(profileQuery);
        for (ScUserProfile profile : profiles) {
            if (profile == null || profile.getUserId() == null) {
                continue;
            }
            result.put(profile.getUserId(), new StudentCandidate(profile.getUserId(), profile.getRealName(), profile.getStudentNo()));
        }
        return new ArrayList<>(result.values());
    }

    private ScStudentScore findScoreByClassCourseAndStudent(Long classCourseId, Long studentUserId) {
        ScStudentScore query = new ScStudentScore();
        query.setClassCourseId(classCourseId);
        query.setStudentUserId(studentUserId);
        List<ScStudentScore> list = scStudentScoreMapper.selectScStudentScoreList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    private boolean isImportRowEmpty(ScStudentScoreImportDto row) {
        return row == null
                || (row.getStudentUserId() == null
                && StringUtils.isEmpty(row.getStudentNo())
                && StringUtils.isEmpty(row.getStudentName())
                && row.getUsualScore() == null
                && row.getAttendanceScore() == null
                && row.getHomeworkScore() == null
                && row.getLabScore() == null
                && row.getExamScore() == null
                && row.getBonusScore() == null
                && row.getPenaltyScore() == null
                && StringUtils.isEmpty(row.getTeacherComment())
                && StringUtils.isEmpty(row.getRemark()));
    }

    private StudentCandidate resolveImportCandidate(ScStudentScoreImportDto row,
            Map<Long, StudentCandidate> userIdMap,
            Map<String, StudentCandidate> studentNoMap,
            Map<String, List<StudentCandidate>> studentNameMap) {
        if (row == null) {
            return null;
        }
        if (row.getStudentUserId() != null && userIdMap.containsKey(row.getStudentUserId())) {
            return userIdMap.get(row.getStudentUserId());
        }
        if (StringUtils.isNotEmpty(row.getStudentNo())) {
            StudentCandidate byNo = studentNoMap.get(normalizeText(row.getStudentNo()));
            if (byNo != null) {
                return byNo;
            }
        }
        if (StringUtils.isNotEmpty(row.getStudentName())) {
            List<StudentCandidate> byName = studentNameMap.get(normalizeText(row.getStudentName()));
            if (byName != null && byName.size() == 1) {
                return byName.get(0);
            }
        }
        return null;
    }

    private void applyImportFields(ScStudentScore target, ScStudentScoreImportDto row) {
        if (row.getUsualScore() != null) {
            target.setUsualScore(row.getUsualScore());
        }
        if (row.getAttendanceScore() != null) {
            target.setAttendanceScore(row.getAttendanceScore());
        }
        if (row.getHomeworkScore() != null) {
            target.setHomeworkScore(row.getHomeworkScore());
        }
        if (row.getLabScore() != null) {
            target.setLabScore(row.getLabScore());
        }
        if (row.getExamScore() != null) {
            target.setExamScore(row.getExamScore());
        }
        if (row.getBonusScore() != null) {
            target.setBonusScore(row.getBonusScore());
        }
        if (row.getPenaltyScore() != null) {
            target.setPenaltyScore(row.getPenaltyScore());
        }
        if (row.getTeacherComment() != null) {
            target.setTeacherComment(row.getTeacherComment());
        }
        if (row.getRemark() != null) {
            target.setRemark(row.getRemark());
        }
    }

    private String normalizeText(String value) {
        return StringUtils.defaultIfEmpty(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private List<Map<String, Object>> buildCourseStats(List<ScStudentScore> list) {
        Map<String, List<ScStudentScore>> groupMap = list.stream()
                .collect(Collectors.groupingBy(item -> StringUtils.defaultIfEmpty(item.getCourseName(), "未命名课程"),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ScStudentScore>> entry : groupMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("courseName", entry.getKey());
            item.put("count", entry.getValue().size());
            item.put("avgScore", average(entry.getValue().stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList())));
            item.put("passCount", entry.getValue().stream().filter(row -> "1".equals(row.getPassFlag())).count());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildTermStats(List<ScStudentScore> list) {
        Map<String, List<ScStudentScore>> groupMap = list.stream()
                .collect(Collectors.groupingBy(item -> StringUtils.defaultIfEmpty(item.getTermName(), "未配置学期"),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ScStudentScore>> entry : groupMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("termName", entry.getKey());
            item.put("count", entry.getValue().size());
            item.put("avgScore", average(entry.getValue().stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList())));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildScoreBands(List<ScStudentScore> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(scoreBand("90-100", list, BigDecimal.valueOf(90), null));
        result.add(scoreBand("80-89", list, BigDecimal.valueOf(80), BigDecimal.valueOf(90)));
        result.add(scoreBand("70-79", list, BigDecimal.valueOf(70), BigDecimal.valueOf(80)));
        result.add(scoreBand("60-69", list, BigDecimal.valueOf(60), BigDecimal.valueOf(70)));
        result.add(scoreBand("<60", list, null, BigDecimal.valueOf(60)));
        return result;
    }

    private Map<String, Object> scoreBand(String label, List<ScStudentScore> list, BigDecimal minInclusive, BigDecimal maxExclusive) {
        long count = list.stream().filter(item -> {
            BigDecimal total = safeScore(item.getTotalScore());
            boolean lower = minInclusive == null || total.compareTo(minInclusive) >= 0;
            boolean upper = maxExclusive == null || total.compareTo(maxExclusive) < 0;
            return lower && upper;
        }).count();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("label", label);
        result.put("count", count);
        return result;
    }

    private List<Map<String, Object>> buildGradeDistribution(List<ScStudentScore> list) {
        Map<String, Long> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> StringUtils.defaultIfEmpty(item.getGradeLevel(), "未评级"),
                        LinkedHashMap::new, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : grouped.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("gradeLevel", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildPortalTermTrend(List<ScStudentScore> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScStudentScore> sorted = list.stream()
                .sorted(Comparator
                        .comparing(ScStudentScore::getTermId, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ScStudentScore::getTermName, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(ScStudentScore::getPublishTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        Map<String, List<ScStudentScore>> grouped = sorted.stream()
                .collect(Collectors.groupingBy(
                        item -> StringUtils.defaultIfEmpty(item.getTermName(), "未配置学期"),
                        LinkedHashMap::new,
                        Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ScStudentScore>> entry : grouped.entrySet()) {
            List<ScStudentScore> scores = entry.getValue();
            ScStudentScore first = scores.get(0);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("termId", first.getTermId());
            item.put("termName", entry.getKey());
            item.put("courseCount", scores.size());
            item.put("avgScore", average(scores.stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList())));
            item.put("avgGradePoint", average(scores.stream().map(ScStudentScore::getGradePoint).collect(Collectors.toList())));
            item.put("passCount", scores.stream().filter(score -> "1".equals(score.getPassFlag())).count());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> buildPortalCourseSeries(List<ScStudentScore> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .sorted(Comparator
                        .comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScStudentScore::getPublishTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScStudentScore::getCourseName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(score -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("scoreId", score.getScoreId());
                    item.put("classCourseId", score.getClassCourseId());
                    item.put("termName", score.getTermName());
                    item.put("courseId", score.getCourseId());
                    item.put("courseName", score.getCourseName());
                    item.put("className", score.getClassName());
                    item.put("totalScore", safeScore(score.getTotalScore()));
                    item.put("gradePoint", safeScore(score.getGradePoint()));
                    item.put("rankNo", score.getRankNo());
                    item.put("gradeLevel", score.getGradeLevel());
                    item.put("publishTime", score.getPublishTime());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildPortalComponentSeries(List<ScStudentScore> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(componentSeriesItem("平时表现", average(list.stream().map(ScStudentScore::getUsualScore).collect(Collectors.toList())),
                average(list.stream().map(ScStudentScore::getUsualWeight).collect(Collectors.toList()))));
        result.add(componentSeriesItem("考勤参与", average(list.stream().map(ScStudentScore::getAttendanceScore).collect(Collectors.toList())),
                average(list.stream().map(ScStudentScore::getAttendanceWeight).collect(Collectors.toList()))));
        result.add(componentSeriesItem("作业训练", average(list.stream().map(ScStudentScore::getHomeworkScore).collect(Collectors.toList())),
                average(list.stream().map(ScStudentScore::getHomeworkWeight).collect(Collectors.toList()))));
        result.add(componentSeriesItem("实验实训", average(list.stream().map(ScStudentScore::getLabScore).collect(Collectors.toList())),
                average(list.stream().map(ScStudentScore::getLabWeight).collect(Collectors.toList()))));
        result.add(componentSeriesItem("考试表现", average(list.stream().map(ScStudentScore::getExamScore).collect(Collectors.toList())),
                average(list.stream().map(ScStudentScore::getExamWeight).collect(Collectors.toList()))));
        return result;
    }

    private Map<String, Object> componentSeriesItem(String label, BigDecimal avgScore, BigDecimal avgWeight) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("label", label);
        item.put("avgScore", avgScore);
        item.put("avgWeight", avgWeight);
        return item;
    }

    private List<Map<String, Object>> buildPortalPassDistribution(List<ScStudentScore> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(distributionItem("已通过", list.stream().filter(item -> "1".equals(item.getPassFlag())).count()));
        result.add(distributionItem("待提升", list.stream().filter(item -> !"1".equals(item.getPassFlag())).count()));
        return result;
    }

    private Map<String, Object> distributionItem(String label, long count) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("label", label);
        item.put("count", count);
        return item;
    }

    private List<Map<String, Object>> buildPortalInsights(List<ScStudentScore> list, String termName, String courseName) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            result.add(portalInsight("info", "暂无分析数据", "当前筛选范围内暂无已发布成绩，切换学期或课程后即可查看趋势和图表分析。"));
            return result;
        }
        String scopeLabel = buildPortalScopeLabel(termName, courseName);
        BigDecimal avgScore = average(list.stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList()));
        long passCount = list.stream().filter(item -> "1".equals(item.getPassFlag())).count();
        long totalCount = list.size();
        String overallTone = avgScore.compareTo(BigDecimal.valueOf(85)) >= 0 ? "success"
                : avgScore.compareTo(BigDecimal.valueOf(70)) >= 0 ? "info" : "warning";
        String overallTitle = avgScore.compareTo(BigDecimal.valueOf(85)) >= 0 ? "整体表现较稳"
                : avgScore.compareTo(BigDecimal.valueOf(70)) >= 0 ? "整体表现中等" : "整体表现需要关注";
        String overallContent = scopeLabel + "共 " + totalCount + " 门课程，平均分 " + formatPromptDecimal(avgScore)
                + "，通过率 " + (totalCount == 0 ? "0" : String.valueOf((passCount * 100) / totalCount)) + "%。";
        result.add(portalInsight(overallTone, overallTitle, overallContent));

        List<Map<String, Object>> componentSeries = buildPortalComponentSeries(list);
        Map<String, Object> strongest = componentSeries.stream()
                .max(Comparator.comparing(item -> (BigDecimal) item.get("avgScore")))
                .orElse(null);
        Map<String, Object> weakest = componentSeries.stream()
                .min(Comparator.comparing(item -> (BigDecimal) item.get("avgScore")))
                .orElse(null);
        if (strongest != null && weakest != null) {
            result.add(portalInsight(
                    "info",
                    "成绩构成有明显差异",
                    String.valueOf(strongest.get("label")) + "均分最高，为 " + formatPromptDecimal((BigDecimal) strongest.get("avgScore"))
                            + "；" + String.valueOf(weakest.get("label")) + "相对偏弱，为 "
                            + formatPromptDecimal((BigDecimal) weakest.get("avgScore")) + "。"));
        }

        ScStudentScore best = list.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
        ScStudentScore risk = list.stream()
                .filter(item -> item != null && !"1".equals(item.getPassFlag()))
                .min(Comparator.comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
        if (risk != null) {
            result.add(portalInsight(
                    "warning",
                    "存在待提升课程",
                    StringUtils.defaultIfEmpty(risk.getCourseName(), "未命名课程") + "当前总评 "
                            + formatPromptDecimal(safeScore(risk.getTotalScore())) + "，建议优先关注考试表现、作业补交和过程性得分。"));
        } else if (best != null) {
            result.add(portalInsight(
                    "success",
                    "当前暂无未通过课程",
                    StringUtils.defaultIfEmpty(best.getCourseName(), "未命名课程") + "表现最好，总评 "
                            + formatPromptDecimal(safeScore(best.getTotalScore())) + "，可以复盘其学习节奏并迁移到其他课程。"));
        }
        return result;
    }

    private Map<String, Object> portalInsight(String tone, String title, String content) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("tone", tone);
        item.put("title", title);
        item.put("content", content);
        return item;
    }

    private String buildPortalAiContext(List<ScStudentScore> filteredList, String termName, String courseName, List<ScStudentScore> trendSource) {
        if (filteredList == null || filteredList.isEmpty()) {
            return "当前筛选范围内暂无已发布成绩，请提示学生先切换学期或课程，再进行成绩趋势与改进建议分析。";
        }
        BigDecimal avgScore = average(filteredList.stream().map(ScStudentScore::getTotalScore).collect(Collectors.toList()));
        BigDecimal bestScore = filteredList.stream().map(ScStudentScore::getTotalScore).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lowestScore = filteredList.stream().map(ScStudentScore::getTotalScore).filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        long passCount = filteredList.stream().filter(item -> "1".equals(item.getPassFlag())).count();
        long totalCount = filteredList.size();
        List<Map<String, Object>> scoreBands = buildScoreBands(filteredList);
        List<Map<String, Object>> componentSeries = buildPortalComponentSeries(filteredList);
        List<Map<String, Object>> termTrend = buildPortalTermTrend(trendSource);
        ScStudentScore bestCourse = filteredList.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
        ScStudentScore riskCourse = filteredList.stream()
                .filter(item -> item != null && !"1".equals(item.getPassFlag()))
                .min(Comparator.comparing(ScStudentScore::getTotalScore, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        String scoreBandText = scoreBands.stream()
                .map(item -> item.get("label") + " " + item.get("count") + "门")
                .collect(Collectors.joining("，"));
        String componentText = componentSeries.stream()
                .map(item -> item.get("label") + " " + formatPromptDecimal((BigDecimal) item.get("avgScore"))
                        + "（权重均值 " + formatPromptDecimal((BigDecimal) item.get("avgWeight")) + "%）")
                .collect(Collectors.joining("，"));
        String termTrendText = termTrend.stream()
                .map(item -> item.get("termName") + " 平均分 " + formatPromptDecimal((BigDecimal) item.get("avgScore")))
                .collect(Collectors.joining("；"));

        StringBuilder builder = new StringBuilder();
        builder.append("请基于以下学生成绩信息做一次学习诊断分析。\n");
        builder.append("分析范围：").append(buildPortalScopeLabel(termName, courseName)).append("\n");
        builder.append("课程数量：").append(totalCount).append(" 门\n");
        builder.append("平均分：").append(formatPromptDecimal(avgScore)).append("\n");
        builder.append("最高分：").append(formatPromptDecimal(bestScore)).append("，最低分：").append(formatPromptDecimal(lowestScore)).append("\n");
        builder.append("通过率：").append(totalCount == 0 ? "0" : String.valueOf((passCount * 100) / totalCount)).append("%\n");
        builder.append("分数段分布：").append(scoreBandText).append("\n");
        builder.append("成绩构成均值：").append(componentText).append("\n");
        if (StringUtils.isNotEmpty(termTrendText)) {
            builder.append("学期趋势：").append(termTrendText).append("\n");
        }
        if (bestCourse != null) {
            builder.append("优势课程：").append(StringUtils.defaultIfEmpty(bestCourse.getCourseName(), "未命名课程"))
                    .append("，总评 ").append(formatPromptDecimal(safeScore(bestCourse.getTotalScore()))).append("\n");
        }
        if (riskCourse != null) {
            builder.append("风险课程：").append(StringUtils.defaultIfEmpty(riskCourse.getCourseName(), "未命名课程"))
                    .append("，总评 ").append(formatPromptDecimal(safeScore(riskCourse.getTotalScore()))).append("\n");
        }
        builder.append("请输出：1. 总体表现判断；2. 主要优势；3. 风险与可能原因；4. 接下来两周的具体改进建议。");
        return builder.toString();
    }

    private String buildPortalScopeLabel(String termName, String courseName) {
        if (StringUtils.isNotEmpty(termName) && StringUtils.isNotEmpty(courseName)) {
            return termName + "｜" + courseName;
        }
        if (StringUtils.isNotEmpty(termName)) {
            return termName;
        }
        if (StringUtils.isNotEmpty(courseName)) {
            return courseName;
        }
        return "全部已发布成绩";
    }

    private String resolveTermName(Long termId) {
        if (termId == null) {
            return "";
        }
        ScSchoolTerm term = scSchoolTermService.selectScSchoolTermByTermId(termId);
        return term == null ? "" : StringUtils.defaultIfEmpty(term.getTermName(), "");
    }

    private String formatPromptDecimal(BigDecimal value) {
        return safeScore(value).stripTrailingZeros().toPlainString();
    }

    private Map<String, Object> buildExamSummary(List<ScExamRecord> records) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordCount", records.size());
        result.put("submittedCount", records.stream().filter(item -> "SUBMITTED".equalsIgnoreCase(item.getExamStatus())).count());
        result.put("avgScore", average(records.stream().map(ScExamRecord::getScore).collect(Collectors.toList())));
        result.put("bestScore", records.stream().map(ScExamRecord::getScore).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
        return result;
    }

    private BigDecimal average(List<BigDecimal> values) {
        List<BigDecimal> safeValues = values == null ? Collections.emptyList()
                : values.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (safeValues.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : safeValues) {
            total = total.add(value);
        }
        return total.divide(BigDecimal.valueOf(safeValues.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultIfNull(BigDecimal source, BigDecimal fallback) {
        return source == null ? fallback.setScale(2, RoundingMode.HALF_UP) : normalizeWeight(source);
    }

    private BigDecimal normalizeComponentScore(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeBonusScore(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeWeight(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveGradePoint(BigDecimal totalScore) {
        BigDecimal total = safeScore(totalScore);
        if (total.compareTo(BigDecimal.valueOf(95)) >= 0) return BigDecimal.valueOf(4.0).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(90)) >= 0) return BigDecimal.valueOf(3.8).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(85)) >= 0) return BigDecimal.valueOf(3.5).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(80)) >= 0) return BigDecimal.valueOf(3.0).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(75)) >= 0) return BigDecimal.valueOf(2.5).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(70)) >= 0) return BigDecimal.valueOf(2.0).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(60)) >= 0) return BigDecimal.valueOf(1.0).setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private String resolveGradeLevel(BigDecimal totalScore) {
        BigDecimal total = safeScore(totalScore);
        if (total.compareTo(BigDecimal.valueOf(95)) >= 0) return "A+";
        if (total.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (total.compareTo(BigDecimal.valueOf(85)) >= 0) return "B+";
        if (total.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (total.compareTo(BigDecimal.valueOf(75)) >= 0) return "C+";
        if (total.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (total.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }

    private BigDecimal safeScore(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private static class StudentCandidate {
        private final Long studentUserId;
        private final String studentName;
        private final String studentNo;

        StudentCandidate(Long studentUserId, String studentName, String studentNo) {
            this.studentUserId = studentUserId;
            this.studentName = studentName;
            this.studentNo = studentNo;
        }

        Long getStudentUserId() {
            return studentUserId;
        }

        String getStudentName() {
            return studentName;
        }

        String getStudentNo() {
            return studentNo;
        }
    }
}
