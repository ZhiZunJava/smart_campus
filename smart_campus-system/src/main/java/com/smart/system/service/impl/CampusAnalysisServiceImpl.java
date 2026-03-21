package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.campusvo.LearningBehaviorStatsVo;
import com.smart.system.domain.campusvo.LearningDiagnosisVo;
import com.smart.system.domain.campusvo.LearningProfileOverviewVo;
import com.smart.system.domain.campusvo.LearningWorkbenchVo;
import com.smart.system.domain.campusvo.RecommendationItemVo;
import com.smart.system.domain.campusvo.ResourceInsightVo;
import com.smart.system.domain.dto.AiChatRequestDto;
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
import com.smart.system.service.ai.IAiGatewayService;

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

    @Autowired
    private IAiGatewayService aiGatewayService;

    @Autowired
    private ObjectMapper objectMapper;

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
        enrichDiagnosisWithAi(vo, studyRecords, examRecords, wrongQuestions);
        return vo;
    }

    @Override
    public LearningWorkbenchVo getLearningWorkbench(Long userId, Long courseId, Integer limit) {
        int size = (limit == null || limit <= 0) ? 5 : limit;
        ScLearningProfile profile = scLearningProfileService.rebuildProfile(userId, courseId);
        List<ScStudyRecord> studyRecords = sortStudyRecords(listStudyRecords(userId, courseId));
        List<ScExamRecord> examRecords = sortExamRecords(listExamRecords(userId, courseId));
        List<ScWrongQuestionBook> wrongQuestions = listWrongQuestions(userId, courseId);
        List<ScLearningWarning> warnings = sortWarnings(listWarningsAll(userId, courseId));
        List<ScLearningReport> reports = sortReports(listReports(userId));

        LearningWorkbenchVo vo = new LearningWorkbenchVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setProfile(toProfileOverview(profile));
        vo.setBehaviorStats(buildBehaviorStats(studyRecords, examRecords, wrongQuestions));
        vo.setRecentStudyRecords(limitList(studyRecords, size));
        vo.setRecentExamRecords(limitList(examRecords, size));
        vo.setRecentWarnings(limitList(warnings, size));
        vo.setRecentReports(limitList(reports, size));
        List<ScLearningRecommendation> recommendations = scLearningRecommendationService
                .listActiveRecommendations(userId, "workbench", size);
        vo.setActiveRecommendations(recommendations.stream().map(this::toRecommendationVo).collect(Collectors.toList()));
        vo.setFocusResources(buildFocusResources(recommendations, size));
        enrichWorkbenchProfileWithAi(vo, studyRecords, examRecords, wrongQuestions);
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

    private void enrichDiagnosisWithAi(LearningDiagnosisVo vo, List<ScStudyRecord> studyRecords,
            List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        if (vo == null || vo.getProfile() == null) {
            return;
        }
        LearningProfileOverviewVo enriched = buildAiProfileInsight(vo.getProfile(), vo, studyRecords, examRecords,
                wrongQuestions);
        vo.setProfile(enriched);
        vo.setAiNarrative(enriched.getAiSummary());
        if (enriched.getAiSuggestions() != null && !enriched.getAiSuggestions().isEmpty()) {
            List<String> mergedSuggestions = new ArrayList<>(vo.getActionSuggestions() == null ? Collections.emptyList()
                    : vo.getActionSuggestions());
            for (String suggestion : enriched.getAiSuggestions()) {
                if (!mergedSuggestions.contains(suggestion)) {
                    mergedSuggestions.add(suggestion);
                }
            }
            vo.setActionSuggestions(mergedSuggestions);
        }
        if (enriched.getRisks() != null && !enriched.getRisks().isEmpty()) {
            List<String> mergedTags = new ArrayList<>(vo.getRiskTags() == null ? Collections.emptyList() : vo.getRiskTags());
            for (String item : enriched.getRisks()) {
                if (!mergedTags.contains(item)) {
                    mergedTags.add(item);
                }
            }
            vo.setRiskTags(mergedTags);
        }
    }

    private void enrichWorkbenchProfileWithAi(LearningWorkbenchVo vo, List<ScStudyRecord> studyRecords,
            List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        if (vo == null || vo.getProfile() == null) {
            return;
        }
        LearningDiagnosisVo tempDiagnosis = new LearningDiagnosisVo();
        tempDiagnosis.setRiskLevel(determineRiskLevelFromScore(vo.getProfile().getRiskScore()));
        tempDiagnosis.setOverallSummary("基于近期学习行为、考试与错题数据生成的学习画像解读。");
        tempDiagnosis.setRiskTags(buildRiskTagsFromScores(vo.getProfile(), examRecords, wrongQuestions));
        tempDiagnosis.setActionSuggestions(buildActionSuggestionsFromScores(vo.getProfile(), examRecords, wrongQuestions));
        vo.setProfile(buildAiProfileInsight(vo.getProfile(), tempDiagnosis, studyRecords, examRecords, wrongQuestions));
    }

    private LearningProfileOverviewVo buildAiProfileInsight(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis,
            List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        LearningProfileOverviewVo result = profile;
        try {
            AiChatResponseVo responseVo = invokeAiProfileAnalysis(profile, diagnosis, studyRecords, examRecords,
                    wrongQuestions);
            fillProfileAiFields(result, responseVo == null ? null : responseVo.getContent(), diagnosis, studyRecords,
                    examRecords, wrongQuestions);
        } catch (Exception ignored) {
            fillProfileAiFields(result, null, diagnosis, studyRecords, examRecords, wrongQuestions);
        }
        return result;
    }

    private AiChatResponseVo invokeAiProfileAnalysis(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis,
            List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setBizType("learning_profile_analysis");
        requestDto.setSystemPrompt("你是智慧校园学习画像分析助手。请基于提供的画像、学习行为、考试表现、错题情况，输出严格 JSON："
                + "{\"portraitTitle\":\"\",\"aiSummary\":\"\",\"learningPattern\":\"\",\"aiConfidence\":0,"
                + "\"strengths\":[],\"risks\":[],\"aiSuggestions\":[]}。"
                + "必须使用中文，strengths/risks/aiSuggestions 各返回 2-4 条简洁可执行内容。"
                + "如果数据不足，要给出保守结论，不允许输出解释性 markdown。");
        requestDto.setUserPrompt(buildAiDiagnosisPrompt(profile, diagnosis, studyRecords, examRecords, wrongQuestions));
        requestDto.setStream(Boolean.FALSE);
        requestDto.setDeepThinking(Boolean.FALSE);
        try {
            return CompletableFuture.supplyAsync(() -> aiGatewayService.chat(requestDto))
                    .orTimeout(8, TimeUnit.SECONDS)
                    .exceptionally(ex -> null)
                    .join();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String buildAiDiagnosisPrompt(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis,
            List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("profile", mapOfProfile(profile));
        payload.put("diagnosis", mapOfDiagnosis(diagnosis));
        payload.put("studyBehaviorCount", studyRecords == null ? 0 : studyRecords.size());
        payload.put("studyDurationSeconds", sumStudyDuration(studyRecords == null ? Collections.emptyList() : studyRecords));
        payload.put("avgProgressRate", avgProgressRate(studyRecords == null ? Collections.emptyList() : studyRecords));
        payload.put("avgExamScore", avgExamScore(examRecords == null ? Collections.emptyList() : examRecords));
        payload.put("avgCorrectRate", avgCorrectRate(examRecords == null ? Collections.emptyList() : examRecords));
        payload.put("wrongQuestionCount", wrongQuestions == null ? 0 : wrongQuestions.size());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return payload.toString();
        }
    }

    private Map<String, Object> mapOfProfile(LearningProfileOverviewVo profile) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("abilityLevel", profile == null ? null : profile.getAbilityLevel());
        map.put("activeScore", profile == null ? null : profile.getActiveScore());
        map.put("concentrationScore", profile == null ? null : profile.getConcentrationScore());
        map.put("masteryScore", profile == null ? null : profile.getMasteryScore());
        map.put("interestScore", profile == null ? null : profile.getInterestScore());
        map.put("riskScore", profile == null ? null : profile.getRiskScore());
        return map;
    }

    private Map<String, Object> mapOfDiagnosis(LearningDiagnosisVo diagnosis) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("riskLevel", diagnosis == null ? null : diagnosis.getRiskLevel());
        map.put("overallSummary", diagnosis == null ? null : diagnosis.getOverallSummary());
        map.put("riskTags", diagnosis == null ? Collections.emptyList() : diagnosis.getRiskTags());
        map.put("actionSuggestions", diagnosis == null ? Collections.emptyList() : diagnosis.getActionSuggestions());
        return map;
    }

    private void fillProfileAiFields(LearningProfileOverviewVo profile, String rawContent, LearningDiagnosisVo diagnosis,
            List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        if (profile == null) {
            return;
        }
        try {
            if (rawContent != null && !rawContent.trim().isEmpty()) {
                JsonNode root = objectMapper.readTree(extractJsonObject(rawContent));
                profile.setPortraitTitle(root.path("portraitTitle").asText(defaultPortraitTitle(profile)));
                profile.setAiSummary(root.path("aiSummary").asText(defaultAiSummary(profile, diagnosis)));
                profile.setLearningPattern(root.path("learningPattern").asText(defaultLearningPattern(profile)));
                profile.setAiConfidence(root.path("aiConfidence").isNumber() ? root.path("aiConfidence").asInt() : 78);
                profile.setStrengths(readStringList(root.path("strengths"), defaultStrengths(profile, examRecords)));
                profile.setRisks(readStringList(root.path("risks"), defaultRisks(profile, diagnosis, wrongQuestions)));
                profile.setAiSuggestions(readStringList(root.path("aiSuggestions"),
                        defaultAiSuggestions(profile, diagnosis, studyRecords, examRecords, wrongQuestions)));
                return;
            }
        } catch (Exception ignored) {
        }
        profile.setPortraitTitle(defaultPortraitTitle(profile));
        profile.setAiSummary(defaultAiSummary(profile, diagnosis));
        profile.setLearningPattern(defaultLearningPattern(profile));
        profile.setAiConfidence(72);
        profile.setStrengths(defaultStrengths(profile, examRecords));
        profile.setRisks(defaultRisks(profile, diagnosis, wrongQuestions));
        profile.setAiSuggestions(defaultAiSuggestions(profile, diagnosis, studyRecords, examRecords, wrongQuestions));
    }

    private String extractJsonObject(String rawContent) {
        String normalized = rawContent == null ? "" : rawContent.trim();
        int firstBrace = normalized.indexOf('{');
        int lastBrace = normalized.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return normalized.substring(firstBrace, lastBrace + 1);
        }
        return normalized;
    }

    private List<String> readStringList(JsonNode node, List<String> fallback) {
        if (node == null || !node.isArray()) {
            return fallback;
        }
        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            if (item != null && item.isTextual() && !item.asText().trim().isEmpty()) {
                values.add(item.asText().trim());
            }
        }
        return values.isEmpty() ? fallback : values;
    }

    private String defaultPortraitTitle(LearningProfileOverviewVo profile) {
        if (profile == null || profile.getRiskScore() == null) {
            return "待完善学习画像";
        }
        if (profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "待重点干预型画像";
        }
        if (profile.getMasteryScore() != null && profile.getMasteryScore().compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "稳步领先型画像";
        }
        return "持续提升型画像";
    }

    private String defaultAiSummary(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis) {
        String level = diagnosis == null ? "--" : diagnosis.getRiskLevel();
        return String.format("当前画像显示学生处于%s状态，建议结合活跃度、掌握度和错题情况进行阶段性跟进。", level);
    }

    private String defaultLearningPattern(LearningProfileOverviewVo profile) {
        if (profile == null) {
            return "数据不足型";
        }
        if (profile.getActiveScore() != null && profile.getActiveScore().compareTo(BigDecimal.valueOf(70)) >= 0
                && profile.getMasteryScore() != null
                && profile.getMasteryScore().compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "高投入稳步吸收型";
        }
        if (profile.getActiveScore() != null && profile.getActiveScore().compareTo(BigDecimal.valueOf(40)) < 0) {
            return "低活跃待唤醒型";
        }
        return "阶段波动调整型";
    }

    private List<String> defaultStrengths(LearningProfileOverviewVo profile, List<ScExamRecord> examRecords) {
        List<String> strengths = new ArrayList<>();
        if (profile != null && profile.getInterestScore() != null
                && profile.getInterestScore().compareTo(BigDecimal.valueOf(60)) >= 0) {
            strengths.add("对课程资源保持一定关注度，具备继续深挖的基础");
        }
        if (profile != null && profile.getConcentrationScore() != null
                && profile.getConcentrationScore().compareTo(BigDecimal.valueOf(60)) >= 0) {
            strengths.add("连续学习时长较稳定，专注投入表现尚可");
        }
        if (avgCorrectRate(examRecords == null ? Collections.emptyList() : examRecords)
                .compareTo(BigDecimal.valueOf(70)) >= 0) {
            strengths.add("测验正确率较好，说明核心知识点已有一定掌握");
        }
        if (strengths.isEmpty()) {
            strengths.add("当前已有基础学习数据，可继续形成更稳定的学习画像");
        }
        return strengths;
    }

    private List<String> defaultRisks(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis,
            List<ScWrongQuestionBook> wrongQuestions) {
        List<String> risks = new ArrayList<>();
        if (profile != null && profile.getRiskScore() != null
                && profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0) {
            risks.add("画像风险分偏高，需要尽快进入跟进和干预节奏");
        }
        if (profile != null && profile.getActiveScore() != null
                && profile.getActiveScore().compareTo(BigDecimal.valueOf(40)) < 0) {
            risks.add("学习活跃度不足，容易影响后续进度与知识保持");
        }
        if (wrongQuestions != null && wrongQuestions.size() >= 3) {
            risks.add("错题累计偏多，说明知识漏洞尚未被系统修复");
        }
        if (diagnosis != null && diagnosis.getAvgCorrectRate() != null
                && diagnosis.getAvgCorrectRate().compareTo(BigDecimal.valueOf(60)) < 0) {
            risks.add("测验正确率偏低，当前掌握质量仍需加强");
        }
        return risks;
    }

    private List<String> defaultAiSuggestions(LearningProfileOverviewVo profile, LearningDiagnosisVo diagnosis,
            List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords, List<ScWrongQuestionBook> wrongQuestions) {
        List<String> suggestions = new ArrayList<>();
        if (sumStudyDuration(studyRecords == null ? Collections.emptyList() : studyRecords) < 1800) {
            suggestions.add("先稳定每日学习时段，再逐步提升学习总时长");
        }
        if (diagnosis != null && diagnosis.getAvgProgressRate() != null
                && diagnosis.getAvgProgressRate().compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("优先补完当前课程主线进度，不建议过早分散到拓展内容");
        }
        if (wrongQuestions != null && wrongQuestions.size() >= 3) {
            suggestions.add("围绕错题做 1-2 次专项训练，形成查漏补缺闭环");
        }
        if (avgCorrectRate(examRecords == null ? Collections.emptyList() : examRecords)
                .compareTo(BigDecimal.valueOf(65)) < 0) {
            suggestions.add("安排一次低难度复测，验证关键知识点是否真正掌握");
        }
        if (profile != null && profile.getInterestScore() != null
                && profile.getInterestScore().compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("优先投放更贴近当前知识点的视频或互动型资源，提升参与感");
        }
        return suggestions;
    }

    private String determineRiskLevelFromScore(BigDecimal riskScore) {
        if (riskScore == null) {
            return "LOW";
        }
        if (riskScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "HIGH";
        }
        if (riskScore.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private List<String> buildRiskTagsFromScores(LearningProfileOverviewVo profile, List<ScExamRecord> examRecords,
            List<ScWrongQuestionBook> wrongQuestions) {
        List<String> tags = new ArrayList<>();
        if (profile != null && profile.getRiskScore() != null
                && profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0) {
            tags.add("高风险学情");
        }
        if (profile != null && profile.getActiveScore() != null
                && profile.getActiveScore().compareTo(BigDecimal.valueOf(40)) < 0) {
            tags.add("学习活跃度偏低");
        }
        if (avgCorrectRate(examRecords == null ? Collections.emptyList() : examRecords)
                .compareTo(BigDecimal.valueOf(60)) < 0 && examRecords != null && !examRecords.isEmpty()) {
            tags.add("测验正确率偏低");
        }
        if (wrongQuestions != null && wrongQuestions.size() >= 5) {
            tags.add("错题堆积");
        }
        return tags;
    }

    private List<String> buildActionSuggestionsFromScores(LearningProfileOverviewVo profile, List<ScExamRecord> examRecords,
            List<ScWrongQuestionBook> wrongQuestions) {
        List<String> suggestions = new ArrayList<>();
        if (profile != null && profile.getActiveScore() != null
                && profile.getActiveScore().compareTo(BigDecimal.valueOf(40)) < 0) {
            suggestions.add("先提升学习频次，建立稳定学习节奏");
        }
        if (avgCorrectRate(examRecords == null ? Collections.emptyList() : examRecords)
                .compareTo(BigDecimal.valueOf(60)) < 0 && examRecords != null && !examRecords.isEmpty()) {
            suggestions.add("先做一次基础巩固测验，定位关键漏洞");
        }
        if (wrongQuestions != null && wrongQuestions.size() >= 3) {
            suggestions.add("对高频错题做专题复习并及时复测");
        }
        return suggestions;
    }

    private LearningBehaviorStatsVo buildBehaviorStats(List<ScStudyRecord> studyRecords, List<ScExamRecord> examRecords,
            List<ScWrongQuestionBook> wrongQuestions) {
        LearningBehaviorStatsVo vo = new LearningBehaviorStatsVo();
        vo.setTotalBehaviorCount(studyRecords.size());
        vo.setTotalDurationSeconds(sumStudyDuration(studyRecords));
        vo.setStudyDayCount(countStudyDays(studyRecords));
        vo.setLearnedResourceCount(countResourceCoverage(studyRecords));
        vo.setAvgProgressRate(avgProgressRate(studyRecords));
        vo.setAvgExamScore(avgExamScore(examRecords));
        vo.setAvgCorrectRate(avgCorrectRate(examRecords));
        vo.setWrongQuestionCount(wrongQuestions == null ? 0 : wrongQuestions.size());
        vo.setCompletedBehaviorCount((int) studyRecords.stream()
                .filter(item -> "complete".equalsIgnoreCase(item.getBehaviorType())
                        || "submit".equalsIgnoreCase(item.getBehaviorType()))
                .count());
        vo.setBehaviorTypeDistribution(groupByBehaviorType(studyRecords));
        vo.setDeviceTypeDistribution(groupByDeviceType(studyRecords));
        vo.setRecentActivityTrend(buildRecentTrend(studyRecords));
        return vo;
    }

    private int countStudyDays(List<ScStudyRecord> studyRecords) {
        Set<String> days = new LinkedHashSet<>();
        for (ScStudyRecord studyRecord : studyRecords) {
            if (studyRecord.getCreateTime() != null) {
                days.add(studyRecord.getCreateTime().toString().substring(0, 10));
            }
        }
        return days.size();
    }

    private int countResourceCoverage(List<ScStudyRecord> studyRecords) {
        Set<Long> resourceIds = new LinkedHashSet<>();
        for (ScStudyRecord studyRecord : studyRecords) {
            if (studyRecord.getResourceId() != null) {
                resourceIds.add(studyRecord.getResourceId());
            }
        }
        return resourceIds.size();
    }

    private Map<String, Integer> groupByBehaviorType(List<ScStudyRecord> studyRecords) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (ScStudyRecord studyRecord : studyRecords) {
            String key = studyRecord.getBehaviorType() == null ? "unknown" : studyRecord.getBehaviorType();
            result.put(key, result.getOrDefault(key, 0) + 1);
        }
        return result;
    }

    private Map<String, Integer> groupByDeviceType(List<ScStudyRecord> studyRecords) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (ScStudyRecord studyRecord : studyRecords) {
            String key = studyRecord.getDeviceType() == null ? "unknown" : studyRecord.getDeviceType();
            result.put(key, result.getOrDefault(key, 0) + 1);
        }
        return result;
    }

    private List<String> buildRecentTrend(List<ScStudyRecord> studyRecords) {
        return limitList(studyRecords.stream()
                .map(item -> String.format("%s %s %s%%",
                        item.getCreateTime() == null ? "--" : item.getCreateTime().toString(),
                        item.getBehaviorType() == null ? "study" : item.getBehaviorType(),
                        item.getProgressRate() == null ? "0" : item.getProgressRate().setScale(0, RoundingMode.HALF_UP)
                                .toPlainString()))
                .collect(Collectors.toList()), 7);
    }

    private List<ResourceInsightVo> buildFocusResources(List<ScLearningRecommendation> recommendations, int size) {
        List<ResourceInsightVo> focusResources = new ArrayList<>();
        for (ScLearningRecommendation recommendation : recommendations) {
            if (!"resource".equalsIgnoreCase(recommendation.getBizType()) || recommendation.getBizId() == null) {
                continue;
            }
            ScResource resource = scResourceService.selectScResourceByResourceId(recommendation.getBizId());
            if (resource == null) {
                continue;
            }
            ResourceInsightVo vo = new ResourceInsightVo();
            vo.setResourceId(resource.getResourceId());
            vo.setResourceName(resource.getResourceName());
            vo.setResourceType(resource.getResourceType());
            vo.setCourseId(resource.getCourseId());
            vo.setQualityScore(resource.getQualityScore());
            vo.setHeatScore(BigDecimal.valueOf((resource.getViewCount() == null ? 0 : resource.getViewCount())
                    + (resource.getDownloadCount() == null ? 0 : resource.getDownloadCount()) * 2L
                    + (resource.getFavoriteCount() == null ? 0 : resource.getFavoriteCount()) * 3L)
                    .setScale(2, RoundingMode.HALF_UP));
            vo.setRecommendationScore(recommendation.getRecommendScore());
            vo.setInteractionCount((resource.getViewCount() == null ? 0 : resource.getViewCount())
                    + (resource.getDownloadCount() == null ? 0 : resource.getDownloadCount())
                    + (resource.getFavoriteCount() == null ? 0 : resource.getFavoriteCount()));
            vo.setSummary(resource.getSummary());
            vo.setRecommendationReason(recommendation.getRecommendReason());
            focusResources.add(vo);
            if (focusResources.size() >= size) {
                break;
            }
        }
        return focusResources;
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
