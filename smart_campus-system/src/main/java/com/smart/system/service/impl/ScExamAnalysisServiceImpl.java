package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
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
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
        ScExamSession session = record.getSessionId() == null ? null
                : scExamSessionMapper.selectScExamSessionBySessionId(record.getSessionId());
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record.getSessionId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", record);
        result.put("session", session);
        result.put("answers", answers);
        result.put("questionTypeStats", buildQuestionTypeStats(answers));
        result.put("knowledgePointStats", buildKnowledgePointStats(answers));
        result.put("subPaperStats", buildSessionPaperStats(sessionPapers));
        result.put("subPaperAnswers", buildSessionPaperAnswerDetail(sessionPapers, answers));
        result.put("behaviorLogs", loadBehaviorLogs(record.getSessionId()));
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
        ExamPaperDetailVo paperDetail = scExamManageService.selectPaperDetail(record.getPaperId());
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record.getSessionId());
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
        List<ScExamAnswerDraft> drafts = loadDrafts(recordId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", record);
        result.put("session", session);
        result.put("paperDetail", paperDetail);
        result.put("sessionPapers", sessionPapers);
        result.put("answers", answers);
        result.put("drafts", drafts);
        result.put("answerMap", buildAnswerMap(answers));
        result.put("draftMap", buildDraftMap(drafts));
        result.put("currentSessionPaper", resolveCurrentSessionPaper(sessionPapers));
        result.put("nextSessionPaper", resolveNextSessionPaper(sessionPapers));
        result.put("behaviorLogs", loadBehaviorLogs(record.getSessionId()));
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
        result.put("question", scExamManageService.selectQuestionDetail(wrong.getQuestionId()));
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
        PaperUpsertDto paperUpsertDto = new PaperUpsertDto();
        paperUpsertDto.setPaperName(StringUtils.defaultIfEmpty(dto.getPaperName(), "错题回练-" + System.currentTimeMillis()));
        paperUpsertDto.setCourseId(dto.getCourseId());
        paperUpsertDto.setPaperType("fixed");
        paperUpsertDto.setDurationMinutes(dto.getDurationMinutes() == null ? 45 : dto.getDurationMinutes());
        paperUpsertDto.setPublishStatus("draft");
        paperUpsertDto.setStatus("0");
        paperUpsertDto.setRemark("错题回练自动生成");
        List<PaperQuestionConfigDto> questions = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i++) {
            PaperQuestionConfigDto config = new PaperQuestionConfigDto();
            config.setQuestionId(questionIds.get(i));
            config.setScore(BigDecimal.TEN);
            config.setSortNo(i + 1);
            questions.add(config);
        }
        paperUpsertDto.setQuestions(questions);
        if (!Boolean.FALSE.equals(dto.getSavePaper())) {
            scExamManageService.savePaper(paperUpsertDto, operator);
        }
        return buildPreviewPaper(paperUpsertDto);
    }

    private ExamPaperDetailVo buildPreviewPaper(PaperUpsertDto dto) {
        ExamPaperDetailVo result = new ExamPaperDetailVo();
        result.setPaperName(dto.getPaperName());
        result.setCourseId(dto.getCourseId());
        result.setPaperType(dto.getPaperType());
        result.setDurationMinutes(dto.getDurationMinutes());
        result.setPublishStatus(dto.getPublishStatus());
        result.setStatus(dto.getStatus());
        result.setTotalScore(dto.getQuestions().stream().map(PaperQuestionConfigDto::getScore).reduce(BigDecimal.ZERO, BigDecimal::add));
        List<com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo> details = new ArrayList<>();
        for (PaperQuestionConfigDto item : dto.getQuestions()) {
            com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo detailVo = new com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo();
            detailVo.setQuestionId(item.getQuestionId());
            detailVo.setScore(item.getScore());
            detailVo.setSortNo(item.getSortNo());
            detailVo.setQuestion(scExamManageService.selectQuestionDetail(item.getQuestionId()));
            details.add(detailVo);
        }
        result.setQuestions(details);
        return result;
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
        return result;
    }

    private List<Map<String, Object>> buildSessionPaperStats(List<ScExamSessionPaper> sessionPapers) {
        if (sessionPapers == null || sessionPapers.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<ScExamSessionPaper>> grouped = sessionPapers.stream()
                .filter(item -> item.getPaperId() != null)
                .collect(Collectors.groupingBy(ScExamSessionPaper::getPaperId, LinkedHashMap::new, Collectors.toList()));
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
