package com.smart.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScExamAnswer;
import com.smart.system.domain.ScExamAnswerDraft;
import com.smart.system.domain.ScExamBehaviorLog;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScExamSession;
import com.smart.system.domain.ScExamSessionPaper;
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.domain.ScUserPointLedger;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.campusvo.QuestionDetailVo;
import com.smart.system.domain.dto.PaperQuestionConfigDto;
import com.smart.system.domain.dto.PaperUpsertDto;
import com.smart.system.domain.dto.WrongQuestionRetryDto;
import com.smart.system.mapper.ScExamAnswerMapper;
import com.smart.system.mapper.ScExamAnswerDraftMapper;
import com.smart.system.mapper.ScExamBehaviorLogMapper;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScExamSessionMapper;
import com.smart.system.mapper.ScExamSessionPaperMapper;
import com.smart.system.mapper.ScKnowledgePointMapper;
import com.smart.system.mapper.ScQuestionBankMapper;
import com.smart.system.mapper.ScUserPointLedgerMapper;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
import com.smart.system.service.IScExamAnalysisService;
import com.smart.system.service.IScExamManageService;

@Service
public class ScExamAnalysisServiceImpl implements IScExamAnalysisService {
    @Autowired
    private ScExamRecordMapper scExamRecordMapper;

    @Autowired
    private ScExamAnswerMapper scExamAnswerMapper;

    @Autowired
    private ScExamAnswerDraftMapper scExamAnswerDraftMapper;

    @Autowired
    private ScExamSessionMapper scExamSessionMapper;

    @Autowired
    private ScExamSessionPaperMapper scExamSessionPaperMapper;

    @Autowired
    private ScExamBehaviorLogMapper scExamBehaviorLogMapper;

    @Autowired
    private ScExamPaperMapper scExamPaperMapper;

    @Autowired
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;

    @Autowired
    private ScQuestionBankMapper scQuestionBankMapper;

    @Autowired
    private ScKnowledgePointMapper scKnowledgePointMapper;

    @Autowired
    private ScUserPointLedgerMapper scUserPointLedgerMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IScExamManageService scExamManageService;

    @Override
    public Map<String, Object> buildRecordOverview(ScExamRecord scExamRecord) {
        List<ScExamRecord> records = scExamRecordMapper.selectScExamRecordList(scExamRecord == null ? new ScExamRecord() : scExamRecord);
        List<ScExamAnswer> answers = collectAnswers(records);
        List<ScExamSession> sessions = collectSessions(records);
        List<ScExamSessionPaper> sessionPapers = collectSessionPapers(sessions);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRecordCount", records.size());
        result.put("submittedCount", countByStatus(records, "SUBMITTED"));
        result.put("ongoingCount", countByStatus(records, "ONGOING"));
        result.put("avgScore", average(records.stream().map(ScExamRecord::getScore).collect(Collectors.toList())));
        result.put("avgCorrectRate", average(records.stream().map(ScExamRecord::getCorrectRate).collect(Collectors.toList())));
        result.put("avgElapsedMinutes", averageDurationMinutes(records));
        result.put("latestSubmitTime", records.stream()
                .map(ScExamRecord::getSubmitTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null));
        result.put("questionTypeStats", buildQuestionTypeStats(answers));
        result.put("knowledgePointStats", buildKnowledgePointStats(answers));
        result.put("sessionOverview", buildSessionOverview(sessions));
        result.put("subPaperStats", buildSessionPaperStats(sessionPapers));
        result.put("topWrongQuestions", buildTopWrongQuestions(answers));
        return result;
    }

    @Override
    public Map<String, Object> buildRecordDetail(Long recordId) {
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null) {
            throw new ServiceException("考试记录不存在");
        }
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(record.getPaperId());
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
        ScExamSession session = record.getSessionId() == null ? null
                : scExamSessionMapper.selectScExamSessionBySessionId(record.getSessionId());
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record.getSessionId());
        boolean allowViewScoreSummary = paper == null || !"0".equals(paper.getAllowViewScore());
        boolean allowViewAnswerAnalysis = paper == null || !"0".equals(paper.getAllowReviewAnalysis());
        List<ScUserPointLedger> growthRewards = loadGrowthRewards(record);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", sanitizeRecordDetail(record, allowViewScoreSummary));
        result.put("session", session);
        result.put("timeSummary", buildRecordTimeSummary(record, session, paper, false));
        result.put("answers", sanitizeRecordAnswers(answers, allowViewScoreSummary, allowViewAnswerAnalysis));
        result.put("questionTypeStats", buildQuestionTypeStats(answers));
        result.put("knowledgePointStats", buildKnowledgePointStats(answers));
        result.put("subPaperStats", buildSessionPaperStats(sessionPapers));
        result.put("subPaperAnswers", buildSessionPaperAnswerDetail(sessionPapers, answers));
        result.put("behaviorLogs", loadBehaviorLogs(record.getSessionId()));
        result.put("growthRewards", growthRewards);
        result.put("growthPoints", growthRewards.stream().map(ScUserPointLedger::getChangePoints).filter(Objects::nonNull)
                .reduce(0, Integer::sum));
        return result;
    }

    @Override
    public Map<String, Object> buildExamRuntime(Long recordId) {
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null) {
            throw new ServiceException("考试记录不存在");
        }
        ScExamSession session = record.getSessionId() == null ? null
                : scExamSessionMapper.selectScExamSessionBySessionId(record.getSessionId());
        ExamPaperDetailVo paperDetail = sanitizePaperDetail(scExamManageService.selectPaperDetail(record.getPaperId()));
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record.getSessionId());
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
        List<ScExamAnswerDraft> drafts = loadDrafts(recordId);
        List<ScUserPointLedger> growthRewards = loadGrowthRewards(record);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", record);
        result.put("session", session);
        result.put("timeSummary", buildRecordTimeSummary(record, session, resolvePaper(record.getPaperId()), true));
        result.put("paperDetail", paperDetail);
        result.put("sessionPapers", sessionPapers);
        result.put("answers", answers);
        result.put("drafts", drafts);
        result.put("answerMap", buildAnswerMap(answers));
        result.put("draftMap", buildDraftMap(drafts));
        result.put("currentSessionPaper", resolveCurrentSessionPaper(sessionPapers));
        result.put("nextSessionPaper", resolveNextSessionPaper(sessionPapers));
        result.put("behaviorLogs", loadBehaviorLogs(record.getSessionId()));
        result.put("growthRewards", growthRewards);
        result.put("growthPoints", growthRewards.stream().map(ScUserPointLedger::getChangePoints).filter(Objects::nonNull)
                .reduce(0, Integer::sum));
        return result;
    }

    @Override
    public Map<String, Object> buildWrongOverview(ScWrongQuestionBook scWrongQuestionBook) {
        List<ScWrongQuestionBook> wrongList = scWrongQuestionBookMapper
                .selectScWrongQuestionBookList(scWrongQuestionBook == null ? new ScWrongQuestionBook() : scWrongQuestionBook);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalWrongCount", wrongList.size());
        result.put("affectedUserCount", wrongList.stream().map(ScWrongQuestionBook::getUserId).filter(item -> item != null).collect(Collectors.toSet()).size());
        result.put("unmasteredCount", wrongList.stream().filter(item -> "0".equals(item.getMasteryStatus())).count());
        result.put("masteredCount", wrongList.stream().filter(item -> "1".equals(item.getMasteryStatus())).count());
        result.put("catalogStats", buildWrongCatalogStats(wrongList));
        result.put("questionTypeStats", buildWrongQuestionTypeStats(wrongList));
        result.put("knowledgePointStats", buildWrongKnowledgePointStats(wrongList));
        result.put("topWrongQuestions", buildTopWrongBookList(wrongList));
        return result;
    }

    @Override
    public Map<String, Object> buildWrongDetail(Long wrongId) {
        ScWrongQuestionBook wrong = scWrongQuestionBookMapper.selectScWrongQuestionBookById(wrongId);
        if (wrong == null) {
            throw new ServiceException("错题记录不存在");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("wrong", wrong);
        result.put("question", sanitizeQuestionDetail(scExamManageService.selectQuestionDetail(wrong.getQuestionId())));
        return result;
    }

    @Override
    public ExamPaperDetailVo buildWrongRetryPaper(WrongQuestionRetryDto dto, String operator) {
        if (dto == null) {
            throw new ServiceException("回练参数不能为空");
        }
        List<Long> questionIds = resolveRetryQuestionIds(dto);
        if (questionIds.isEmpty()) {
            throw new ServiceException("请选择要回练的错题");
        }
        List<RetryQuestionSnapshot> retryQuestions = buildRetryQuestionSnapshots(questionIds);
        BigDecimal totalScore = retryQuestions.stream()
                .map(RetryQuestionSnapshot::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        PaperUpsertDto paperUpsertDto = new PaperUpsertDto();
        paperUpsertDto.setPaperName(
                StringUtils.defaultIfEmpty(StringUtils.trim(dto.getPaperName()), buildRetryPaperName(questionIds.size())));
        paperUpsertDto.setCourseId(dto.getCourseId());
        paperUpsertDto.setPaperType("fixed");
        paperUpsertDto.setPaperLevel("MAIN");
        paperUpsertDto.setDurationMinutes(dto.getDurationMinutes() == null ? 45 : dto.getDurationMinutes());
        paperUpsertDto.setPublishStatus("published");
        paperUpsertDto.setPublishStartTime(new java.util.Date());
        paperUpsertDto.setStatus("0");
        paperUpsertDto.setRemark(buildRetryRemark(retryQuestions));
        paperUpsertDto.setAllowViewScore("1");
        paperUpsertDto.setAllowReviewAnalysis("1");
        paperUpsertDto.setMarkingMode("auto_first");
        paperUpsertDto.setQuestionOrderMode("manual");
        paperUpsertDto.setQuestionNavigationMode("free");
        paperUpsertDto.setAntiCheatEnabled("0");
        paperUpsertDto.setMaxFocusLossCount(0);
        paperUpsertDto.setAutoSubmitOnFocusLossLimit("0");
        paperUpsertDto.setAllowCopyPaste("1");
        paperUpsertDto.setMaxAttemptCount(0);
        paperUpsertDto.setTotalScore(totalScore);
        paperUpsertDto.setPassScore(calculateRetryPassScore(totalScore));
        paperUpsertDto.setSectionConfigJson(buildRetrySectionConfig(retryQuestions));
        List<PaperQuestionConfigDto> questions = new ArrayList<>();
        for (int i = 0; i < retryQuestions.size(); i++) {
            PaperQuestionConfigDto config = new PaperQuestionConfigDto();
            config.setQuestionId(retryQuestions.get(i).getQuestionId());
            config.setScore(retryQuestions.get(i).getScore());
            config.setSortNo(i + 1);
            questions.add(config);
        }
        paperUpsertDto.setQuestions(questions);
        if (!Boolean.FALSE.equals(dto.getSavePaper())) {
            scExamManageService.savePaper(paperUpsertDto, operator);
            ScExamPaper savedPaper = findLatestRetryPaper(dto.getCourseId(), paperUpsertDto.getPaperName());
            if (savedPaper != null && savedPaper.getPaperId() != null) {
                return sanitizePaperDetail(scExamManageService.selectPaperDetail(savedPaper.getPaperId()));
            }
        }
        return buildPreviewPaper(paperUpsertDto);
    }

    private ScExamPaper findLatestRetryPaper(Long courseId, String paperName) {
        ScExamPaper query = new ScExamPaper();
        query.setCourseId(courseId);
        query.setPaperName(paperName);
        List<ScExamPaper> list = scExamPaperMapper.selectScExamPaperList(query);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private ExamPaperDetailVo buildPreviewPaper(PaperUpsertDto dto) {
        ExamPaperDetailVo result = new ExamPaperDetailVo();
        result.setPaperName(dto.getPaperName());
        result.setCourseId(dto.getCourseId());
        result.setPaperType(dto.getPaperType());
        result.setPaperLevel(dto.getPaperLevel());
        result.setDurationMinutes(dto.getDurationMinutes());
        result.setPublishStatus(dto.getPublishStatus());
        result.setStatus(dto.getStatus());
        result.setPassScore(dto.getPassScore());
        result.setSectionConfigJson(dto.getSectionConfigJson());
        result.setTotalScore(dto.getQuestions().stream().map(PaperQuestionConfigDto::getScore).reduce(BigDecimal.ZERO, BigDecimal::add));
        List<com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo> details = new ArrayList<>();
        for (PaperQuestionConfigDto item : dto.getQuestions()) {
            com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo detailVo = new com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo();
            detailVo.setQuestionId(item.getQuestionId());
            detailVo.setScore(item.getScore());
            detailVo.setSortNo(item.getSortNo());
            detailVo.setQuestion(sanitizeQuestionDetail(scExamManageService.selectQuestionDetail(item.getQuestionId())));
            details.add(detailVo);
        }
        result.setQuestions(details);
        return result;
    }

    private List<RetryQuestionSnapshot> buildRetryQuestionSnapshots(List<Long> questionIds) {
        List<RetryQuestionSnapshot> snapshots = new ArrayList<>();
        for (Long questionId : questionIds) {
            QuestionDetailVo questionDetail = scExamManageService.selectQuestionDetail(questionId);
            String questionType = questionDetail == null ? "single"
                    : StringUtils.defaultIfEmpty(questionDetail.getQuestionType(), "single");
            snapshots.add(new RetryQuestionSnapshot(questionId, questionType, resolveRetryQuestionScore(questionType)));
        }
        return snapshots;
    }

    private BigDecimal resolveRetryQuestionScore(String questionType) {
        String normalizedType = StringUtils.defaultIfEmpty(questionType, "single").toLowerCase();
        switch (normalizedType) {
            case "judge":
                return BigDecimal.valueOf(5);
            case "single":
                return BigDecimal.valueOf(6);
            case "multiple":
                return BigDecimal.valueOf(8);
            case "fill":
                return BigDecimal.valueOf(8);
            case "essay":
                return BigDecimal.valueOf(12);
            case "material":
            case "case":
                return BigDecimal.valueOf(15);
            default:
                return BigDecimal.TEN;
        }
    }

    private BigDecimal calculateRetryPassScore(BigDecimal totalScore) {
        if (totalScore == null || totalScore.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal passScore = totalScore.multiply(BigDecimal.valueOf(0.6)).setScale(0, RoundingMode.HALF_UP);
        if (passScore.compareTo(BigDecimal.ONE) < 0) {
            return BigDecimal.ONE;
        }
        return passScore.compareTo(totalScore) > 0 ? totalScore : passScore;
    }

    private String buildRetryPaperName(int questionCount) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return "错题回练卷-" + questionCount + "题-" + formatter.format(new java.util.Date());
    }

    private String buildRetryRemark(List<RetryQuestionSnapshot> retryQuestions) {
        Map<String, Long> grouped = retryQuestions.stream().collect(
                Collectors.groupingBy(RetryQuestionSnapshot::getQuestionType, LinkedHashMap::new, Collectors.counting()));
        String summary = grouped.entrySet().stream()
                .map(item -> retryQuestionTypeLabel(item.getKey()) + item.getValue() + "题")
                .collect(Collectors.joining("，"));
        return "错题回练自动生成，共 " + retryQuestions.size() + " 题"
                + (StringUtils.isEmpty(summary) ? "" : "，结构：" + summary);
    }

    private String buildRetrySectionConfig(List<RetryQuestionSnapshot> retryQuestions) {
        Map<String, List<RetryQuestionSnapshot>> grouped = retryQuestions.stream().collect(
                Collectors.groupingBy(RetryQuestionSnapshot::getQuestionType, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> sectionConfigs = new ArrayList<>();
        grouped.forEach((questionType, items) -> {
            if (items == null || items.isEmpty()) {
                return;
            }
            BigDecimal targetScore = items.stream().map(RetryQuestionSnapshot::getScore).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal defaultScore = items.get(0).getScore();
            Map<String, Object> section = new LinkedHashMap<>();
            section.put("title", retryQuestionTypeLabel(questionType));
            section.put("questionType", questionType);
            section.put("defaultScore", defaultScore);
            section.put("targetScore", targetScore);
            section.put("instructions", "共 " + items.size() + " 题，建议先独立作答，再结合解析复盘。");
            section.put("questionIds",
                    items.stream().map(RetryQuestionSnapshot::getQuestionId).collect(Collectors.toList()));
            sectionConfigs.add(section);
        });
        try {
            return objectMapper.writeValueAsString(sectionConfigs);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String retryQuestionTypeLabel(String questionType) {
        String normalizedType = StringUtils.defaultIfEmpty(questionType, "single").toLowerCase();
        switch (normalizedType) {
            case "single":
                return "单选题";
            case "multiple":
                return "多选题";
            case "judge":
                return "判断题";
            case "fill":
                return "填空题";
            case "essay":
                return "简答题";
            case "material":
                return "材料题";
            case "case":
                return "案例题";
            default:
                return normalizedType;
        }
    }

    private static final class RetryQuestionSnapshot {
        private final Long questionId;
        private final String questionType;
        private final BigDecimal score;

        private RetryQuestionSnapshot(Long questionId, String questionType, BigDecimal score) {
            this.questionId = questionId;
            this.questionType = questionType;
            this.score = score == null ? BigDecimal.TEN : score;
        }

        public Long getQuestionId() {
            return questionId;
        }

        public String getQuestionType() {
            return questionType;
        }

        public BigDecimal getScore() {
            return score;
        }
    }

    private ExamPaperDetailVo sanitizePaperDetail(ExamPaperDetailVo paperDetail) {
        if (paperDetail == null) {
            return null;
        }
        if (paperDetail.getQuestions() != null) {
            paperDetail.getQuestions().forEach(item -> {
                if (item != null) {
                    item.setQuestion(sanitizeQuestionDetail(item.getQuestion()));
                }
            });
        }
        if (paperDetail.getSubPapers() != null) {
            paperDetail.getSubPapers().forEach(this::sanitizePaperDetail);
        }
        return paperDetail;
    }

    private com.smart.system.domain.campusvo.QuestionDetailVo sanitizeQuestionDetail(
            com.smart.system.domain.campusvo.QuestionDetailVo questionDetail) {
        if (questionDetail == null) {
            return null;
        }
        questionDetail.setAnswer(null);
        questionDetail.setAnalysis(null);
        if (questionDetail.getOptions() != null) {
            questionDetail.getOptions().forEach(option -> {
                if (option != null) {
                    option.setIsRight(null);
                }
            });
        }
        return questionDetail;
    }

    private ScExamRecord sanitizeRecordDetail(ScExamRecord record, boolean allowViewScore) {
        if (record == null || allowViewScore) {
            return record;
        }
        record.setScore(null);
        record.setCorrectRate(null);
        return record;
    }

    private List<ScExamAnswer> sanitizeRecordAnswers(List<ScExamAnswer> answers, boolean allowViewScoreSummary,
            boolean allowViewAnswerAnalysis) {
        if (answers == null) {
            return answers;
        }
        answers.forEach(item -> {
            if (item != null) {
                if (!allowViewAnswerAnalysis) {
                    item.setStandardAnswer(null);
                    item.setAnalysis(null);
                }
                if (!allowViewScoreSummary) {
                    item.setScore(null);
                }
            }
        });
        return answers;
    }

    private List<Long> resolveRetryQuestionIds(WrongQuestionRetryDto dto) {
        Set<Long> result = new LinkedHashSet<>();
        if (dto.getQuestionIds() != null) {
            result.addAll(dto.getQuestionIds().stream().filter(item -> item != null).collect(Collectors.toSet()));
        }
        if (dto.getWrongIds() != null) {
            for (Long wrongId : dto.getWrongIds()) {
                ScWrongQuestionBook wrong = scWrongQuestionBookMapper.selectScWrongQuestionBookById(wrongId);
                if (wrong != null && wrong.getQuestionId() != null) {
                    result.add(wrong.getQuestionId());
                    if (dto.getCourseId() == null) {
                        dto.setCourseId(wrong.getCourseId());
                    }
                }
            }
        }
        return new ArrayList<>(result);
    }

    private long countByStatus(List<ScExamRecord> records, String status) {
        return records.stream().filter(item -> status.equals(item.getExamStatus())).count();
    }

    private BigDecimal average(List<BigDecimal> values) {
        List<BigDecimal> validValues = values.stream().filter(item -> item != null).collect(Collectors.toList());
        if (validValues.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = validValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(validValues.size()), 2, RoundingMode.HALF_UP);
    }

    private List<ScExamAnswer> collectAnswers(Collection<ScExamRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScExamAnswer> answers = new ArrayList<>();
        for (ScExamRecord record : records) {
            if (record.getRecordId() != null) {
                answers.addAll(scExamAnswerMapper.selectScExamAnswerByRecordId(record.getRecordId()));
            }
        }
        return answers;
    }

    private List<ScExamSession> collectSessions(Collection<ScExamRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScExamSession> sessions = new ArrayList<>();
        for (ScExamRecord record : records) {
            if (record.getSessionId() == null) {
                continue;
            }
            ScExamSession session = scExamSessionMapper.selectScExamSessionBySessionId(record.getSessionId());
            if (session != null) {
                sessions.add(session);
            }
        }
        return sessions;
    }

    private List<ScExamSessionPaper> collectSessionPapers(Collection<ScExamSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScExamSessionPaper> result = new ArrayList<>();
        for (ScExamSession session : sessions) {
            if (session == null || session.getSessionId() == null) {
                continue;
            }
            result.addAll(loadSessionPapers(session.getSessionId()));
        }
        return result;
    }

    private List<ScExamSessionPaper> loadSessionPapers(Long sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        ScExamSessionPaper query = new ScExamSessionPaper();
        query.setSessionId(sessionId);
        return scExamSessionPaperMapper.selectScExamSessionPaperList(query);
    }

    private List<ScExamBehaviorLog> loadBehaviorLogs(Long sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        ScExamBehaviorLog query = new ScExamBehaviorLog();
        query.setSessionId(sessionId);
        return scExamBehaviorLogMapper.selectScExamBehaviorLogList(query);
    }

    private List<ScExamAnswerDraft> loadDrafts(Long recordId) {
        if (recordId == null) {
            return Collections.emptyList();
        }
        ScExamAnswerDraft query = new ScExamAnswerDraft();
        query.setRecordId(recordId);
        query.setStatus("0");
        return scExamAnswerDraftMapper.selectScExamAnswerDraftList(query);
    }

    private List<ScUserPointLedger> loadGrowthRewards(ScExamRecord record) {
        if (record == null || record.getUserId() == null || record.getRecordId() == null) {
            return Collections.emptyList();
        }
        ScUserPointLedger query = new ScUserPointLedger();
        query.setUserId(record.getUserId());
        query.setBizId(record.getRecordId());
        return scUserPointLedgerMapper.selectScUserPointLedgerList(query).stream()
                .filter(item -> item != null && StringUtils.defaultIfEmpty(item.getBizType(), "").startsWith("EXAM_"))
                .sorted(Comparator.comparing(ScUserPointLedger::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildRecordTimeSummary(ScExamRecord record, ScExamSession session, ScExamPaper paper,
            boolean runtimeMode) {
        Date startTime = record == null ? null : record.getStartTime();
        Date endTime = record == null ? null : record.getSubmitTime();
        long elapsedSeconds = resolveElapsedSeconds(startTime, endTime, runtimeMode);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startTime", startTime);
        result.put("submitTime", endTime);
        result.put("elapsedSeconds", elapsedSeconds);
        result.put("elapsedMinutes", elapsedSeconds <= 0 ? 0L : (elapsedSeconds + 59L) / 60L);
        result.put("paperDurationMinutes", paper == null || paper.getDurationMinutes() == null ? 0 : paper.getDurationMinutes());
        result.put("focusLossCount", session == null ? 0 : session.getFocusLossCount());
        result.put("flaggedCount", session == null ? 0 : session.getFlaggedCount());
        return result;
    }

    private BigDecimal averageDurationMinutes(List<ScExamRecord> records) {
        List<Long> durations = records.stream()
                .map(item -> resolveElapsedSeconds(item == null ? null : item.getStartTime(),
                        item == null ? null : item.getSubmitTime(), false))
                .filter(item -> item > 0)
                .collect(Collectors.toList());
        if (durations.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        long totalSeconds = durations.stream().reduce(0L, Long::sum);
        return BigDecimal.valueOf(totalSeconds)
                .divide(BigDecimal.valueOf(durations.size() * 60D), 1, RoundingMode.HALF_UP);
    }

    private List<Map<String, Object>> buildQuestionTypeStats(List<ScExamAnswer> answers) {
        Map<String, List<ScExamAnswer>> grouped = answers.stream()
                .collect(Collectors.groupingBy(item -> StringUtils.defaultIfEmpty(item.getQuestionType(), "unknown"),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((key, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionType", key);
            item.put("totalCount", value.size());
            item.put("correctCount", value.stream().filter(answer -> "1".equals(answer.getIsCorrect())).count());
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildKnowledgePointStats(List<ScExamAnswer> answers) {
        Map<Long, List<ScExamAnswer>> grouped = answers.stream()
                .filter(item -> item.getKnowledgePointId() != null && item.getKnowledgePointId() > 0)
                .collect(Collectors.groupingBy(item -> item.getKnowledgePointId() == null ? 0L : item.getKnowledgePointId(),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((key, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("knowledgePointId", key);
            item.put("knowledgePointName", resolveKnowledgePointName(key));
            item.put("totalCount", value.size());
            item.put("correctCount", value.stream().filter(answer -> "1".equals(answer.getIsCorrect())).count());
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildTopWrongQuestions(List<ScExamAnswer> answers) {
        Map<Long, List<ScExamAnswer>> grouped = answers.stream()
                .filter(item -> "0".equals(item.getIsCorrect()))
                .collect(Collectors.groupingBy(item -> item.getQuestionId() == null ? 0L : item.getQuestionId(),
                        LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("questionId", entry.getKey());
                    item.put("wrongCount", entry.getValue().size());
                    item.put("stem", entry.getValue().get(0).getStem());
                    item.put("questionType", entry.getValue().get(0).getQuestionType());
                    return item;
                }).collect(Collectors.toList());
    }

    private Map<String, Object> buildSessionOverview(List<ScExamSession> sessions) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sessionCount", sessions == null ? 0 : sessions.size());
        result.put("submittedSessionCount", sessions == null ? 0
                : sessions.stream().filter(item -> "SUBMITTED".equals(item.getSessionStatus())).count());
        result.put("avgFocusLossCount", sessions == null || sessions.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(sessions.stream().map(ScExamSession::getFocusLossCount)
                        .filter(item -> item != null).mapToInt(Integer::intValue).average().orElse(0D))
                        .setScale(2, RoundingMode.HALF_UP));
        result.put("avgFlaggedCount", sessions == null || sessions.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(sessions.stream().map(ScExamSession::getFlaggedCount)
                        .filter(item -> item != null).mapToInt(Integer::intValue).average().orElse(0D))
                        .setScale(2, RoundingMode.HALF_UP));
        result.put("avgElapsedMinutes", sessions == null || sessions.isEmpty()
                ? BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
                : averageSessionDurationMinutes(sessions));
        return result;
    }

    private List<Map<String, Object>> buildSessionPaperStats(List<ScExamSessionPaper> sessionPapers) {
        if (sessionPapers == null || sessionPapers.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<ScExamSessionPaper>> grouped = sessionPapers.stream()
                // Sub-paper stats should only describe real layered sub-papers.
                // For single-layer exams, the root paper itself is recorded in session papers,
                // but it must not be surfaced as a "子卷" entry to the student UI.
                .filter(item -> item.getPaperId() != null)
                .filter(item -> "SUB".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getPaperLevel(), "")))
                .collect(Collectors.groupingBy(ScExamSessionPaper::getPaperId, LinkedHashMap::new, Collectors.toList()));
        if (grouped.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((paperId, items) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            ScExamSessionPaper first = items.get(0);
            row.put("paperId", paperId);
            row.put("parentPaperId", first.getParentPaperId());
            row.put("paperLevel", first.getPaperLevel());
            row.put("answerMode", first.getAnswerMode());
            row.put("paperWeight", first.getPaperWeight());
            row.put("sortNo", first.getSortNo());
            ScExamPaper paper = resolvePaper(paperId);
            row.put("paperName", paper == null ? "试卷 " + paperId : paper.getPaperName());
            row.put("sessionCount", items.size());
            row.put("submittedCount", items.stream().filter(item -> "SUBMITTED".equals(item.getPaperStatus())).count());
            row.put("skippedCount", items.stream().filter(item -> "SKIPPED".equals(item.getPaperStatus())).count());
            row.put("avgScore", average(items.stream().map(ScExamSessionPaper::getScore).collect(Collectors.toList())));
            row.put("avgCorrectRate", average(items.stream().map(ScExamSessionPaper::getCorrectRate).collect(Collectors.toList())));
            row.put("avgElapsedMinutes", averageSessionPaperDurationMinutes(items));
            row.put("latestSubmitTime", items.stream().map(ScExamSessionPaper::getSubmitTime).filter(Objects::nonNull)
                    .max(Comparator.naturalOrder()).orElse(null));
            result.add(row);
        });
        return result;
    }

    private List<Map<String, Object>> buildSessionPaperAnswerDetail(List<ScExamSessionPaper> sessionPapers, List<ScExamAnswer> answers) {
        if (sessionPapers == null || sessionPapers.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<ScExamAnswer>> answerMap = answers == null ? Collections.emptyMap()
                : answers.stream()
                        .filter(item -> item.getSessionPaperId() != null)
                        .collect(Collectors.groupingBy(ScExamAnswer::getSessionPaperId, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sessionPaper", sessionPaper);
            row.put("paper", resolvePaper(sessionPaper.getPaperId()));
            row.put("answers", answerMap.getOrDefault(sessionPaper.getId(), Collections.emptyList()));
            result.add(row);
        }
        return result;
    }

    private Map<Long, Object> buildAnswerMap(List<ScExamAnswer> answers) {
        Map<Long, Object> result = new LinkedHashMap<>();
        if (answers == null) {
            return result;
        }
        for (ScExamAnswer answer : answers) {
            if (answer != null && answer.getQuestionId() != null) {
                result.put(answer.getQuestionId(), answer);
            }
        }
        return result;
    }

    private Map<Long, Object> buildDraftMap(List<ScExamAnswerDraft> drafts) {
        Map<Long, Object> result = new LinkedHashMap<>();
        if (drafts == null) {
            return result;
        }
        for (ScExamAnswerDraft draft : drafts) {
            if (draft != null && draft.getQuestionId() != null) {
                result.put(draft.getQuestionId(), draft);
            }
        }
        return result;
    }

    private BigDecimal averageSessionDurationMinutes(List<ScExamSession> sessions) {
        List<Long> durations = sessions.stream()
                .map(item -> resolveElapsedSeconds(item == null ? null : item.getStartTime(),
                        item == null ? null : item.getSubmitTime(), false))
                .filter(item -> item > 0)
                .collect(Collectors.toList());
        if (durations.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        long totalSeconds = durations.stream().reduce(0L, Long::sum);
        return BigDecimal.valueOf(totalSeconds)
                .divide(BigDecimal.valueOf(durations.size() * 60D), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal averageSessionPaperDurationMinutes(List<ScExamSessionPaper> sessionPapers) {
        List<Long> durations = sessionPapers.stream()
                .map(item -> resolveElapsedSeconds(item == null ? null : item.getStartTime(),
                        item == null ? null : item.getSubmitTime(), false))
                .filter(item -> item > 0)
                .collect(Collectors.toList());
        if (durations.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }
        long totalSeconds = durations.stream().reduce(0L, Long::sum);
        return BigDecimal.valueOf(totalSeconds)
                .divide(BigDecimal.valueOf(durations.size() * 60D), 1, RoundingMode.HALF_UP);
    }

    private long resolveElapsedSeconds(Date startTime, Date endTime, boolean runtimeMode) {
        if (startTime == null) {
            return 0L;
        }
        Date safeEndTime = endTime == null && runtimeMode ? new Date() : endTime;
        if (safeEndTime == null || safeEndTime.before(startTime)) {
            return 0L;
        }
        return Math.max(0L, (safeEndTime.getTime() - startTime.getTime()) / 1000L);
    }

    private ScExamSessionPaper resolveCurrentSessionPaper(List<ScExamSessionPaper> sessionPapers) {
        if (sessionPapers == null || sessionPapers.isEmpty()) {
            return null;
        }
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            if (sessionPaper != null && "ONGOING".equals(sessionPaper.getPaperStatus())) {
                return sessionPaper;
            }
        }
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            if (sessionPaper != null && "PENDING".equals(sessionPaper.getPaperStatus())) {
                return sessionPaper;
            }
        }
        return sessionPapers.get(0);
    }

    private ScExamSessionPaper resolveNextSessionPaper(List<ScExamSessionPaper> sessionPapers) {
        if (sessionPapers == null || sessionPapers.isEmpty()) {
            return null;
        }
        boolean currentFound = false;
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            if (sessionPaper == null) {
                continue;
            }
            if ("ONGOING".equals(sessionPaper.getPaperStatus())) {
                currentFound = true;
                continue;
            }
            if (currentFound && "PENDING".equals(sessionPaper.getPaperStatus())) {
                return sessionPaper;
            }
        }
        return null;
    }

    private ScExamPaper resolvePaper(Long paperId) {
        return paperId == null ? null : scExamPaperMapper.selectScExamPaperByPaperId(paperId);
    }

    private List<Map<String, Object>> buildWrongQuestionTypeStats(List<ScWrongQuestionBook> wrongList) {
        Map<String, List<ScWrongQuestionBook>> grouped = wrongList.stream()
                .collect(Collectors.groupingBy(item -> StringUtils.defaultIfEmpty(item.getQuestionType(), "unknown"),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((key, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("questionType", key);
            item.put("wrongQuestionCount", value.size());
            item.put("wrongTimes", value.stream().map(ScWrongQuestionBook::getWrongCount).filter(count -> count != null)
                    .reduce(0, Integer::sum));
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildWrongCatalogStats(List<ScWrongQuestionBook> wrongList) {
        Map<Long, List<ScWrongQuestionBook>> grouped = wrongList.stream()
                .filter(item -> item.getCatalogId() != null && item.getCatalogId() > 0)
                .collect(Collectors.groupingBy(ScWrongQuestionBook::getCatalogId, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((key, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("catalogId", key);
            item.put("wrongQuestionCount", value.size());
            item.put("wrongTimes", value.stream().map(ScWrongQuestionBook::getWrongCount).filter(count -> count != null)
                    .reduce(0, Integer::sum));
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildWrongKnowledgePointStats(List<ScWrongQuestionBook> wrongList) {
        Map<Long, List<ScWrongQuestionBook>> grouped = wrongList.stream()
                .filter(item -> item.getKnowledgePointId() != null && item.getKnowledgePointId() > 0)
                .collect(Collectors.groupingBy(item -> item.getKnowledgePointId() == null ? 0L : item.getKnowledgePointId(),
                        LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((key, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("knowledgePointId", key);
            item.put("knowledgePointName", resolveKnowledgePointName(key));
            item.put("wrongQuestionCount", value.size());
            item.put("wrongTimes", value.stream().map(ScWrongQuestionBook::getWrongCount).filter(count -> count != null)
                    .reduce(0, Integer::sum));
            result.add(item);
        });
        return result;
    }

    private String resolveKnowledgePointName(Long knowledgePointId) {
        if (knowledgePointId == null || knowledgePointId <= 0) {
            return "-";
        }
        ScKnowledgePoint knowledgePoint = scKnowledgePointMapper
                .selectScKnowledgePointByKnowledgePointId(knowledgePointId);
        return knowledgePoint == null || StringUtils.isEmpty(knowledgePoint.getKnowledgeName())
                ? "知识点 " + knowledgePointId
                : knowledgePoint.getKnowledgeName();
    }

    private List<Map<String, Object>> buildTopWrongBookList(List<ScWrongQuestionBook> wrongList) {
        return wrongList.stream()
                .sorted((a, b) -> Integer.compare(b.getWrongCount() == null ? 0 : b.getWrongCount(),
                        a.getWrongCount() == null ? 0 : a.getWrongCount()))
                .limit(5)
                .map(item -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("id", item.getId());
                    result.put("questionId", item.getQuestionId());
                    result.put("stem", item.getStem());
                    result.put("questionType", item.getQuestionType());
                    result.put("wrongCount", item.getWrongCount());
                    return result;
                }).collect(Collectors.toList());
    }
}
