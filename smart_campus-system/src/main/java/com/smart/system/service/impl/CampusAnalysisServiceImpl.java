package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.campusvo.AnalysisMetaVo;
import com.smart.system.domain.campusvo.LearningDiagnosisVo;
import com.smart.system.domain.campusvo.LearningProfileOverviewVo;
import com.smart.system.domain.campusvo.LearningWorkbenchVo;
import com.smart.system.domain.campusvo.RecommendationItemVo;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.service.ICampusAnalysisService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScLearningProfileService;
import com.smart.system.service.IScLearningRecommendationService;
import com.smart.system.service.IScLearningReportService;
import com.smart.system.service.IScLearningWarningService;
import com.smart.system.service.IScResourceService;
import com.smart.system.service.IScStudyRecordService;
import com.smart.system.service.IScWrongQuestionBookService;

@Service
public class CampusAnalysisServiceImpl implements ICampusAnalysisService {
    @Autowired
    private IScStudyRecordService scStudyRecordService;

    @Autowired
    private IScExamRecordService scExamRecordService;

    @Autowired
    private IScLearningProfileService scLearningProfileService;

    @Autowired
    private IScLearningWarningService scLearningWarningService;

    @Autowired
    private IScLearningReportService scLearningReportService;

    @Autowired
    private IScLearningRecommendationService scLearningRecommendationService;

    @Autowired
    private IScWrongQuestionBookService scWrongQuestionBookService;

    @Autowired
    private IScResourceService scResourceService;

    @Autowired
    private ScExamPaperMapper scExamPaperMapper;

    @Override
    public LearningDiagnosisVo diagnoseLearning(Long userId, Long courseId, Integer recommendLimit,
            Boolean autoGenerate) {
        ScLearningProfile profile = scLearningProfileService.rebuildProfile(userId, courseId);

        List<ScStudyRecord> studyRecords = listStudyRecords(userId, courseId);
        List<ScExamRecord> examRecords = listExamRecords(userId, courseId);
        List<ScWrongQuestionBook> wrongQuestions = listWrongQuestions(userId, courseId);
        List<ScLearningWarning> warnings = listWarnings(userId, courseId);

        ScLearningWarning latestWarning = findLatest(warnings);
        if (Boolean.TRUE.equals(autoGenerate) && latestWarning == null && profile != null
                && profile.getRiskScore() != null && profile.getRiskScore().compareTo(BigDecimal.valueOf(40)) >= 0) {
            latestWarning = scLearningWarningService.buildWarning(userId, courseId);
            warnings = listWarnings(userId, courseId);
        }

        ScLearningReport latestReport = Boolean.TRUE.equals(autoGenerate)
                ? scLearningReportService.generateReport(userId, "diagnosis")
                : findLatestReport(userId);

        List<ScLearningRecommendation> recommendations = scLearningRecommendationService.generateRecommendations(userId,
                "diagnosis",
                recommendLimit == null ? 5 : recommendLimit);

        LearningDiagnosisVo vo = new LearningDiagnosisVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setProfile(toProfileOverview(profile));
        vo.setStudyRecordCount(studyRecords.size());
        vo.setTotalStudyDurationSeconds(sumStudyDuration(studyRecords));
        vo.setAvgProgressRate(avgProgressRate(studyRecords));
        vo.setExamCount(examRecords.size());
        vo.setAvgExamScore(avgExamScore(examRecords));
        vo.setAvgCorrectRate(avgCorrectRate(examRecords));
        vo.setWrongQuestionCount(wrongQuestions.size());
        vo.setPendingWarningCount(warnings.size());
        vo.setRecommendationCount(recommendations.size());
        vo.setRiskLevel(determineRiskLevel(profile));
        vo.setRiskTags(buildRiskTags(profile, studyRecords, examRecords, wrongQuestions));
        vo.setActionSuggestions(
                buildActionSuggestions(profile, studyRecords, examRecords, wrongQuestions, recommendations));
        vo.setOverallSummary(buildSummary(vo));
        vo.setLatestWarning(latestWarning);
        vo.setLatestReport(latestReport);
        vo.setRecommendations(recommendations.stream().map(this::toRecommendationVo).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public LearningWorkbenchVo getLearningWorkbench(Long userId, Long courseId, Integer limit) {
        int size = (limit == null || limit <= 0) ? 5 : limit;
        ScLearningProfile profile = scLearningProfileService.rebuildProfile(userId, courseId);
        List<ScStudyRecord> studyRecords = sortStudyRecords(listStudyRecords(userId, courseId));
        List<ScExamRecord> examRecords = sortExamRecords(listExamRecords(userId, courseId));
        List<ScLearningWarning> warnings = sortWarnings(listWarningsAll(userId, courseId));
        List<ScLearningReport> reports = sortReports(listReports(userId));

        LearningWorkbenchVo vo = new LearningWorkbenchVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setProfile(toProfileOverview(profile));
        vo.setRecentStudyRecords(limitList(studyRecords, size));
        vo.setRecentExamRecords(limitList(examRecords, size));
        vo.setRecentWarnings(limitList(warnings, size));
        vo.setRecentReports(limitList(reports, size));
        vo.setActiveRecommendations(scLearningRecommendationService.listActiveRecommendations(userId, "workbench", size)
                .stream().map(this::toRecommendationVo).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public AnalysisMetaVo getAnalysisMeta() {
        AnalysisMetaVo vo = new AnalysisMetaVo();
        vo.setRiskLevels(mapOf("LOW", "低风险", "MEDIUM", "中风险", "HIGH", "高风险"));
        vo.setWarningLevels(mapOf("LOW", "低", "MEDIUM", "中", "HIGH", "高"));
        vo.setWarningProcessStatuses(mapOf("PENDING", "待处理", "PROCESSED", "已处理", "IGNORED", "已忽略"));
        vo.setRecommendationFeedbackStatuses(mapOf("0", "未反馈", "LIKE", "感兴趣", "DISLIKE", "不感兴趣", "DONE", "已完成"));
        vo.setSupportedScenes(Arrays.asList("home", "diagnosis", "workbench", "course", "exam"));
        return vo;
    }

    private List<ScStudyRecord> listStudyRecords(Long userId, Long courseId) {
        ScStudyRecord query = new ScStudyRecord();
        query.setUserId(userId);
        query.setCourseId(courseId);
        return scStudyRecordService.selectScStudyRecordList(query);
    }

    private List<ScExamRecord> listExamRecords(Long userId, Long courseId) {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(userId);
        List<ScExamRecord> records = scExamRecordService.selectScExamRecordList(query);
        if (courseId == null) {
            return records;
        }
        return records.stream().filter(record -> matchCourse(record, courseId)).collect(Collectors.toList());
    }

    private boolean matchCourse(ScExamRecord record, Long courseId) {
        if (record == null || record.getPaperId() == null) {
            return false;
        }
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(record.getPaperId());
        return paper != null && courseId.equals(paper.getCourseId());
    }

    private List<ScWrongQuestionBook> listWrongQuestions(Long userId, Long courseId) {
        ScWrongQuestionBook query = new ScWrongQuestionBook();
        query.setUserId(userId);
        query.setCourseId(courseId);
        return scWrongQuestionBookService.selectScWrongQuestionBookList(query);
    }

    private List<ScLearningWarning> listWarnings(Long userId, Long courseId) {
        return listWarningsAll(userId, courseId).stream()
                .filter(item -> "PENDING".equalsIgnoreCase(item.getProcessStatus()))
                .collect(Collectors.toList());
    }

    private List<ScLearningWarning> listWarningsAll(Long userId, Long courseId) {
        ScLearningWarning query = new ScLearningWarning();
        query.setUserId(userId);
        query.setCourseId(courseId);
        return scLearningWarningService.selectScLearningWarningList(query);
    }

    private List<ScLearningReport> listReports(Long userId) {
        ScLearningReport query = new ScLearningReport();
        query.setUserId(userId);
        return scLearningReportService.selectScLearningReportList(query);
    }

    private ScLearningWarning findLatest(List<ScLearningWarning> warnings) {
        return warnings.stream()
                .max(Comparator.comparing(ScLearningWarning::getCreateTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private ScLearningReport findLatestReport(Long userId) {
        com.smart.system.domain.ScLearningReport query = new com.smart.system.domain.ScLearningReport();
        query.setUserId(userId);
        return scLearningReportService.selectScLearningReportList(query).stream()
                .max(Comparator.comparing(ScLearningReport::getCreateTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private int sumStudyDuration(List<ScStudyRecord> studyRecords) {
        int total = 0;
        for (ScStudyRecord studyRecord : studyRecords) {
            total += studyRecord.getDurationSeconds() == null ? 0 : studyRecord.getDurationSeconds();
        }
        return total;
    }

    private BigDecimal avgProgressRate(List<ScStudyRecord> studyRecords) {
        if (studyRecords.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ScStudyRecord studyRecord : studyRecords) {
            total = total.add(studyRecord.getProgressRate() == null ? BigDecimal.ZERO : studyRecord.getProgressRate());
        }
        return total.divide(BigDecimal.valueOf(studyRecords.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal avgExamScore(List<ScExamRecord> examRecords) {
        if (examRecords.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (ScExamRecord examRecord : examRecords) {
            if (examRecord.getScore() != null) {
                total = total.add(examRecord.getScore());
                count++;
            }
        }
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal avgCorrectRate(List<ScExamRecord> examRecords) {
        if (examRecords.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (ScExamRecord examRecord : examRecords) {
            if (examRecord.getCorrectRate() != null) {
                total = total.add(examRecord.getCorrectRate());
                count++;
            }
        }
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private String determineRiskLevel(ScLearningProfile profile) {
        BigDecimal riskScore = profile == null || profile.getRiskScore() == null ? BigDecimal.ZERO
                : profile.getRiskScore();
        if (riskScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "HIGH";
        }
        if (riskScore.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private List<String> buildRiskTags(ScLearningProfile profile, List<ScStudyRecord> studyRecords,
            List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        List<String> tags = new ArrayList<>();
        BigDecimal avgProgress = avgProgressRate(studyRecords);
        BigDecimal avgCorrect = avgCorrectRate(examRecords);
        if (profile == null) {
            tags.add("画像缺失");
            return tags;
        }
        if (profile.getRiskScore() != null && profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0) {
            tags.add("高风险学情");
        }
        if (profile.getActiveScore() != null && profile.getActiveScore().compareTo(BigDecimal.valueOf(40)) < 0) {
            tags.add("学习活跃度偏低");
        }
        if (avgProgress.compareTo(BigDecimal.valueOf(60)) < 0) {
            tags.add("课程进度偏慢");
        }
        if (avgCorrect.compareTo(BigDecimal.valueOf(60)) < 0 && !examRecords.isEmpty()) {
            tags.add("测验正确率偏低");
        }
        if (wrongQuestions.size() >= 5) {
            tags.add("错题堆积");
        }
        return tags;
    }

    private List<String> buildActionSuggestions(ScLearningProfile profile, List<ScStudyRecord> studyRecords,
            List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions,
            List<ScLearningRecommendation> recommendations) {
        List<String> suggestions = new ArrayList<>();
        int totalDuration = sumStudyDuration(studyRecords);
        BigDecimal avgProgress = avgProgressRate(studyRecords);
        BigDecimal avgCorrect = avgCorrectRate(examRecords);

        if (profile == null) {
            suggestions.add("先补充学习行为数据，再进行画像和预警分析");
        }
        if (totalDuration < 1800) {
            suggestions.add("本阶段累计学习时长偏少，建议先保证每天稳定学习时段");
        }
        if (avgProgress.compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("优先推进当前课程进度，先完成未学完章节再做拓展练习");
        }
        if (!examRecords.isEmpty() && avgCorrect.compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("安排一次基础巩固测验，先修复核心知识点漏洞");
        }
        if (wrongQuestions.size() >= 3) {
            suggestions.add("围绕错题本进行专项复习，优先消化高频错题");
        }
        if (!recommendations.isEmpty()) {
            suggestions.add("从推荐资源中先完成前两项高分资源，形成短周期闭环");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("当前学习状态较稳定，建议保持节奏并逐步提升练习难度");
        }
        return suggestions;
    }

    private String buildSummary(LearningDiagnosisVo vo) {
        return String.format("当前学情风险等级为%s，累计学习行为%d次，平均进度%s，平均正确率%s，待处理预警%d条，推荐资源%d项。",
                vo.getRiskLevel(),
                vo.getStudyRecordCount(),
                vo.getAvgProgressRate().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                vo.getAvgCorrectRate().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                vo.getPendingWarningCount(),
                vo.getRecommendationCount());
    }

    private List<ScStudyRecord> sortStudyRecords(List<ScStudyRecord> records) {
        return records.stream()
                .sorted(Comparator.comparing(ScStudyRecord::getCreateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private List<ScExamRecord> sortExamRecords(List<ScExamRecord> records) {
        return records.stream()
                .sorted(Comparator
                        .comparing(ScExamRecord::getSubmitTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScExamRecord::getStartTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private List<ScLearningWarning> sortWarnings(List<ScLearningWarning> warnings) {
        return warnings.stream()
                .sorted(Comparator.comparing(ScLearningWarning::getCreateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private List<ScLearningReport> sortReports(List<ScLearningReport> reports) {
        return reports.stream()
                .sorted(Comparator
                        .comparing(ScLearningReport::getGenerateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ScLearningReport::getCreateTime,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private <T> List<T> limitList(List<T> items, int size) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(items.subList(0, Math.min(items.size(), size)));
    }

    private Map<String, String> mapOf(String... values) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int index = 0; index + 1 < values.length; index += 2) {
            map.put(values[index], values[index + 1]);
        }
        return map;
    }

    private LearningProfileOverviewVo toProfileOverview(ScLearningProfile profile) {
        if (profile == null) {
            return null;
        }
        LearningProfileOverviewVo profileVo = new LearningProfileOverviewVo();
        profileVo.setUserId(profile.getUserId());
        profileVo.setCourseId(profile.getCourseId());
        profileVo.setAbilityLevel(profile.getAbilityLevel());
        profileVo.setActiveScore(profile.getActiveScore());
        profileVo.setConcentrationScore(profile.getConcentrationScore());
        profileVo.setMasteryScore(profile.getMasteryScore());
        profileVo.setInterestScore(profile.getInterestScore());
        profileVo.setRiskScore(profile.getRiskScore());
        return profileVo;
    }

    private RecommendationItemVo toRecommendationVo(ScLearningRecommendation recommendation) {
        RecommendationItemVo vo = new RecommendationItemVo();
        vo.setRecommendId(recommendation.getRecommendId());
        vo.setBizId(recommendation.getBizId());
        vo.setBizType(recommendation.getBizType());
        vo.setRecommendReason(recommendation.getRecommendReason());
        vo.setRecommendScore(recommendation.getRecommendScore());
        vo.setSceneCode(recommendation.getSceneCode());
        vo.setExpireTime(recommendation.getExpireTime());
        if ("resource".equalsIgnoreCase(recommendation.getBizType()) && recommendation.getBizId() != null) {
            ScResource resource = scResourceService.selectScResourceByResourceId(recommendation.getBizId());
            if (resource != null) {
                vo.setTitle(resource.getResourceName());
                vo.setResourceType(resource.getResourceType());
                vo.setSummary(resource.getSummary());
            }
        }
        return vo;
    }
}
