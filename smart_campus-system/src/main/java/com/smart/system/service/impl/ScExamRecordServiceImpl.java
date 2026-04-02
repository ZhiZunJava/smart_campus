package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.smart.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamAnswer;
import com.smart.system.domain.ScExamAnswerDraft;
import com.smart.system.domain.ScExamBehaviorLog;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamPaperQuestion;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScExamSession;
import com.smart.system.domain.ScExamSessionPaper;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.common.utils.StringUtils;
import com.smart.system.mapper.ScExamAnswerMapper;
import com.smart.system.mapper.ScExamAnswerDraftMapper;
import com.smart.system.mapper.ScExamBehaviorLogMapper;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamPaperQuestionMapper;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScExamSessionMapper;
import com.smart.system.mapper.ScExamSessionPaperMapper;
import com.smart.system.mapper.ScQuestionBankMapper;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScLearningProfileService;
import com.smart.system.service.IScLearningReportService;
import com.smart.system.service.IScUserGrowthService;
import com.smart.system.service.IScLearningWarningService;
import com.smart.system.domain.dto.ExamQuestionSubmitDto;
import com.smart.system.domain.dto.ExamSubPaperSubmitDto;

@Service
public class ScExamRecordServiceImpl implements IScExamRecordService {
    @Autowired
    private ScExamRecordMapper scExamRecordMapper;
    @Autowired
    private ScExamPaperQuestionMapper scExamPaperQuestionMapper;
    @Autowired
    private ScQuestionBankMapper scQuestionBankMapper;
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
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;
    @Autowired
    private ScExamPaperMapper scExamPaperMapper;
    @Autowired
    private IScLearningProfileService scLearningProfileService;
    @Autowired
    private IScLearningWarningService scLearningWarningService;
    @Autowired
    private IScLearningReportService scLearningReportService;
    @Autowired
    private IScUserGrowthService scUserGrowthService;

    @Override
    public ScExamRecord selectScExamRecordByRecordId(Long recordId) {
        return scExamRecordMapper.selectScExamRecordByRecordId(recordId);
    }

    @Override
    public List<ScExamRecord> selectScExamRecordList(ScExamRecord scExamRecord) {
        return scExamRecordMapper.selectScExamRecordList(scExamRecord);
    }

    @Override
    public int insertScExamRecord(ScExamRecord scExamRecord) {
        return scExamRecordMapper.insertScExamRecord(scExamRecord);
    }

    @Override
    public int updateScExamRecord(ScExamRecord scExamRecord) {
        return scExamRecordMapper.updateScExamRecord(scExamRecord);
    }

    @Override
    public int deleteScExamRecordByRecordIds(Long[] recordIds) {
        return scExamRecordMapper.deleteScExamRecordByRecordIds(recordIds);
    }

    @Override
    public int deleteScExamRecordByRecordId(Long recordId) {
        return scExamRecordMapper.deleteScExamRecordByRecordId(recordId);
    }

    @Override
    public ScExamRecord startExam(Long paperId, Long userId) {
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(paperId);
        if (paper == null) {
            throw new ServiceException("试卷不存在");
        }
        ScExamRecord query = new ScExamRecord();
        query.setPaperId(paperId);
        query.setUserId(userId);
        query.setExamStatus("ONGOING");
        List<ScExamRecord> ongoingRecords = scExamRecordMapper.selectScExamRecordList(query);
        if (ongoingRecords != null && !ongoingRecords.isEmpty()) {
            return ongoingRecords.get(0);
        }
        int maxAttemptCount = paper.getMaxAttemptCount() == null ? 0 : Math.max(0, paper.getMaxAttemptCount());
        if (maxAttemptCount > 0) {
            ScExamRecord attemptQuery = new ScExamRecord();
            attemptQuery.setPaperId(paperId);
            attemptQuery.setUserId(userId);
            List<ScExamRecord> attempts = scExamRecordMapper.selectScExamRecordList(attemptQuery);
            if (attempts != null && attempts.size() >= maxAttemptCount) {
                throw new ServiceException("该试卷可参加次数已用完");
            }
        }
        ScExamRecord record = new ScExamRecord();
        record.setPaperId(paperId);
        record.setUserId(userId);
        record.setStartTime(new Date());
        record.setExamStatus("ONGOING");
        record.setCheatFlag("0");
        record.setScore(BigDecimal.ZERO);
        record.setCorrectRate(BigDecimal.ZERO);
        Long sessionId = initExamSession(paperId, userId);
        record.setSessionId(sessionId);
        scExamRecordMapper.insertScExamRecord(record);
        return record;
    }

    @Override
    public ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers) {
        return submitExam(recordId, answers, null, null);
    }

    @Override
    public ScExamAnswerDraft saveAnswerDraft(com.smart.system.domain.dto.ExamAnswerDraftSaveDto dto, Long userId,
            String operator) {
        if (dto == null || dto.getRecordId() == null) {
            return null;
        }
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || userId == null || !userId.equals(record.getUserId())) {
            return null;
        }
        syncFocusLossCount(record, dto.getFocusLossCount(), dto.getFocusLossSource(), dto.getFocusLossOccurredAt(),
                operator);
        if (dto.getQuestionId() == null) {
            return null;
        }
        ScExamAnswerDraft draft = scExamAnswerDraftMapper.selectScExamAnswerDraftByRecordAndQuestion(dto.getRecordId(),
                dto.getQuestionId());
        Date now = new Date();
        if (draft == null) {
            draft = new ScExamAnswerDraft();
            draft.setRecordId(dto.getRecordId());
            draft.setSessionId(record.getSessionId());
            draft.setSessionPaperId(resolveDraftSessionPaperId(record, dto.getQuestionId(), dto.getSessionPaperId()));
            draft.setQuestionId(dto.getQuestionId());
            draft.setUserId(userId);
            draft.setUserAnswer(dto.getUserAnswer());
            draft.setSaveCount(1);
            draft.setLastSaveTime(now);
            draft.setStatus("0");
            draft.setCreateBy(operator);
            scExamAnswerDraftMapper.insertScExamAnswerDraft(draft);
        } else {
            draft.setSessionId(record.getSessionId());
            draft.setSessionPaperId(resolveDraftSessionPaperId(record, dto.getQuestionId(), dto.getSessionPaperId()));
            draft.setUserId(userId);
            draft.setUserAnswer(dto.getUserAnswer());
            draft.setSaveCount((draft.getSaveCount() == null ? 0 : draft.getSaveCount()) + 1);
            draft.setLastSaveTime(now);
            draft.setUpdateBy(operator);
            scExamAnswerDraftMapper.updateScExamAnswerDraft(draft);
        }
        insertBehaviorLog(record, "AUTO_SAVE", 1, "{\"questionId\":" + dto.getQuestionId() + "}");
        return draft;
    }

    private void syncFocusLossCount(ScExamRecord record, Integer latestFocusLossCount, String focusLossSource,
            Long focusLossOccurredAt, String operator) {
        if (record == null || record.getSessionId() == null || latestFocusLossCount == null) {
            return;
        }
        int safeLatestCount = Math.max(0, latestFocusLossCount);
        ScExamSession session = scExamSessionMapper.selectScExamSessionBySessionId(record.getSessionId());
        if (session == null) {
            return;
        }
        int currentCount = session.getFocusLossCount() == null ? 0 : Math.max(0, session.getFocusLossCount());
        if (safeLatestCount <= currentCount) {
            return;
        }
        session.setSessionId(record.getSessionId());
        session.setFocusLossCount(safeLatestCount);
        session.setUpdateBy(operator);
        scExamSessionMapper.updateScExamSession(session);
        for (int count = currentCount + 1; count <= safeLatestCount; count++) {
            long occurredAt = focusLossOccurredAt == null ? System.currentTimeMillis() : focusLossOccurredAt;
            String behaviorData = String.format(
                    "{\"focusLossCount\":%d,\"sourceEvent\":\"%s\",\"occurredAt\":%d}",
                    count,
                    StringUtils.defaultIfEmpty(focusLossSource, "unknown"),
                    occurredAt);
            insertBehaviorLog(record, "FOCUS_LOSS", 1, behaviorData, new Date(occurredAt));
        }
    }

    @Override
    public List<ScExamAnswerDraft> listAnswerDrafts(Long recordId, Long userId) {
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null || userId == null || !userId.equals(record.getUserId())) {
            return new ArrayList<>();
        }
        ScExamAnswerDraft query = new ScExamAnswerDraft();
        query.setRecordId(recordId);
        query.setUserId(userId);
        query.setStatus("0");
        return scExamAnswerDraftMapper.selectScExamAnswerDraftList(query);
    }

    @Override
    public ScExamSessionPaper submitQuestionAnswer(ExamQuestionSubmitDto dto, Long userId, String operator) {
        if (dto == null || dto.getRecordId() == null || dto.getQuestionId() == null) {
            return null;
        }
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || userId == null || !userId.equals(record.getUserId())) {
            return null;
        }
        ScQuestionBank question = scQuestionBankMapper.selectScQuestionBankByQuestionId(dto.getQuestionId());
        if (question == null) {
            return null;
        }
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record);
        Long sessionPaperId = dto.getSessionPaperId() != null ? dto.getSessionPaperId()
                : resolveSessionPaperId(sessionPapers, dto.getQuestionId());
        if (sessionPaperId == null) {
            return null;
        }
        ScExamSessionPaper sessionPaper = scExamSessionPaperMapper.selectScExamSessionPaperById(sessionPaperId);
        if (sessionPaper == null) {
            return null;
        }
        if (!"ONGOING".equals(sessionPaper.getPaperStatus())) {
            sessionPaper.setPaperStatus("ONGOING");
            sessionPaper.setStartTime(sessionPaper.getStartTime() == null ? new Date() : sessionPaper.getStartTime());
            sessionPaper.setUpdateBy(operator);
            scExamSessionPaperMapper.updateScExamSessionPaper(sessionPaper);
        }

        removeQuestionAnswer(record.getRecordId(), dto.getQuestionId(), sessionPaperId);
        BigDecimal itemScore = findQuestionScore(
                scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(sessionPaper.getPaperId()), dto.getQuestionId());
        boolean correct = isAnswerCorrect(question.getQuestionType(), dto.getUserAnswer(), question.getAnswer());
        ScExamAnswer answer = new ScExamAnswer();
        answer.setRecordId(record.getRecordId());
        answer.setSessionId(record.getSessionId());
        answer.setSessionPaperId(sessionPaperId);
        answer.setQuestionId(dto.getQuestionId());
        answer.setUserAnswer(StringUtils.defaultIfEmpty(dto.getUserAnswer(), ""));
        answer.setIsCorrect(correct ? "1" : "0");
        answer.setScore(correct ? itemScore : BigDecimal.ZERO);
        answer.setKnowledgePointId(question.getKnowledgePointId());
        scExamAnswerMapper.insertScExamAnswer(answer);
        scExamAnswerDraftMapper.deleteScExamAnswerDraftByRecordId(record.getRecordId());
        insertBehaviorLog(record, "QUESTION_SUBMIT", 1, "{\"questionId\":" + dto.getQuestionId() + "}");
        return refreshSessionPaperStats(sessionPaper, operator);
    }

    @Override
    public ScExamSessionPaper submitSubPaper(ExamSubPaperSubmitDto dto, Long userId, String operator) {
        if (dto == null || dto.getRecordId() == null) {
            return null;
        }
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || userId == null || !userId.equals(record.getUserId())) {
            return null;
        }
        Long sessionPaperId = dto.getSessionPaperId();
        if (sessionPaperId == null && dto.getPaperId() != null && record.getSessionId() != null) {
            ScExamSessionPaper temp = scExamSessionPaperMapper.selectScExamSessionPaperBySessionAndPaper(record.getSessionId(),
                    dto.getPaperId());
            sessionPaperId = temp == null ? null : temp.getId();
        }
        if (sessionPaperId == null) {
            return null;
        }
        ScExamSessionPaper sessionPaper = scExamSessionPaperMapper.selectScExamSessionPaperById(sessionPaperId);
        if (sessionPaper == null) {
            return null;
        }
        if (dto.getAnswers() != null) {
            for (com.smart.system.domain.dto.ExamAnswerSubmitDto item : dto.getAnswers()) {
                if (item == null || item.getQuestionId() == null) {
                    continue;
                }
                ExamQuestionSubmitDto questionDto = new ExamQuestionSubmitDto();
                questionDto.setRecordId(dto.getRecordId());
                questionDto.setQuestionId(item.getQuestionId());
                questionDto.setUserAnswer(item.getUserAnswer());
                questionDto.setSessionPaperId(sessionPaperId);
                submitQuestionAnswer(questionDto, userId, operator);
            }
        }
        sessionPaper = scExamSessionPaperMapper.selectScExamSessionPaperById(sessionPaperId);
        sessionPaper.setStartTime(sessionPaper.getStartTime() == null ? new Date() : sessionPaper.getStartTime());
        sessionPaper.setPaperStatus("SUBMITTED");
        sessionPaper.setSubmitTime(new Date());
        sessionPaper.setUpdateBy(operator);
        scExamSessionPaperMapper.updateScExamSessionPaper(sessionPaper);
        insertBehaviorLog(record, "SUB_PAPER_SUBMIT", 1, "{\"sessionPaperId\":" + sessionPaperId + "}");
        return refreshSessionPaperStats(sessionPaper, operator);
    }

    @Override
    public ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers, Integer focusLossCount,
            Integer flaggedCount) {
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null) {
            return null;
        }
        List<Map<String, Object>> mergedAnswers = mergeSubmittedAnswers(recordId, answers);
        scExamAnswerMapper.deleteScExamAnswerByRecordId(recordId);
        List<ScExamPaperQuestion> paperQuestions = scExamPaperQuestionMapper
                .selectScExamPaperQuestionByPaperId(record.getPaperId());
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record);
        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;
        List<Long> answeredQuestionIds = new ArrayList<>();

        for (Map<String, Object> item : mergedAnswers) {
            Long questionId = item.get("questionId") == null ? null
                    : Long.valueOf(String.valueOf(item.get("questionId")));
            String userAnswer = item.get("userAnswer") == null ? "" : String.valueOf(item.get("userAnswer"));
            if (questionId == null) {
                continue;
            }
            ScQuestionBank question = scQuestionBankMapper.selectScQuestionBankByQuestionId(questionId);
            if (question == null) {
                continue;
            }
            BigDecimal itemScore = findQuestionScore(paperQuestions, questionId);
            boolean correct = isAnswerCorrect(question.getQuestionType(), userAnswer, question.getAnswer());
            ScExamAnswer answer = new ScExamAnswer();
            answer.setRecordId(recordId);
            answer.setSessionId(record.getSessionId());
            answer.setQuestionId(questionId);
            answer.setUserAnswer(userAnswer);
            answer.setIsCorrect(correct ? "1" : "0");
            answer.setScore(correct ? itemScore : BigDecimal.ZERO);
            answer.setKnowledgePointId(question.getKnowledgePointId());
            answer.setSessionPaperId(resolveSessionPaperId(sessionPapers, questionId));
            scExamAnswerMapper.insertScExamAnswer(answer);
            answeredQuestionIds.add(questionId);

            if (correct) {
                totalScore = totalScore.add(itemScore);
                correctCount++;
            } else {
                saveWrongBook(record.getUserId(), question);
            }
        }

        record.setSubmitTime(new Date());
        record.setScore(totalScore.setScale(2, RoundingMode.HALF_UP));
        record.setCorrectRate(paperQuestions.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(correctCount * 100.0 / paperQuestions.size()).setScale(2, RoundingMode.HALF_UP));
        record.setExamStatus("SUBMITTED");
        record.setAnalysisJson(buildAnalysisJson(paperQuestions.size(), correctCount, totalScore, focusLossCount,
                flaggedCount));
        scExamRecordMapper.updateScExamRecord(record);
        scExamAnswerDraftMapper.deleteScExamAnswerDraftByRecordId(recordId);
        syncExamSessionAfterSubmit(record, paperQuestions, answeredQuestionIds, focusLossCount, flaggedCount);

        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(record.getPaperId());
        Long courseId = paper == null ? null : paper.getCourseId();
        if (courseId != null) {
            scLearningProfileService.rebuildProfile(record.getUserId(), courseId);
            scLearningWarningService.buildWarning(record.getUserId(), courseId);
        }
        scLearningReportService.generateReport(record.getUserId(), "exam_after_submit");
        scUserGrowthService.rewardExamSubmit(record.getUserId(), recordId, "system");
        return record;
    }

    private List<Map<String, Object>> mergeSubmittedAnswers(Long recordId, List<Map<String, Object>> answers) {
        Map<Long, String> mergedMap = new LinkedHashMap<>();
        List<ScExamAnswer> existingAnswers = scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
        for (ScExamAnswer answer : existingAnswers) {
            if (answer == null || answer.getQuestionId() == null) {
                continue;
            }
            mergedMap.put(answer.getQuestionId(), StringUtils.defaultIfEmpty(answer.getUserAnswer(), ""));
        }
        if (answers != null) {
            for (Map<String, Object> item : answers) {
                if (item == null || item.get("questionId") == null) {
                    continue;
                }
                Long questionId = Long.valueOf(String.valueOf(item.get("questionId")));
                String userAnswer = item.get("userAnswer") == null ? "" : String.valueOf(item.get("userAnswer"));
                mergedMap.put(questionId, userAnswer);
            }
        }
        List<Map<String, Object>> mergedAnswers = new ArrayList<>();
        mergedMap.forEach((questionId, userAnswer) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("questionId", questionId);
            row.put("userAnswer", userAnswer);
            mergedAnswers.add(row);
        });
        return mergedAnswers;
    }

    private Long initExamSession(Long paperId, Long userId) {
        ScExamSession query = new ScExamSession();
        query.setRootPaperId(paperId);
        query.setUserId(userId);
        query.setSessionStatus("ONGOING");
        List<ScExamSession> sessions = scExamSessionMapper.selectScExamSessionList(query);
        if (sessions != null && !sessions.isEmpty()) {
            return sessions.get(0).getSessionId();
        }
        ScExamSession session = new ScExamSession();
        session.setRootPaperId(paperId);
        session.setUserId(userId);
        session.setStartTime(new Date());
        session.setSessionStatus("ONGOING");
        session.setTotalScore(BigDecimal.ZERO);
        session.setCorrectRate(BigDecimal.ZERO);
        session.setFocusLossCount(0);
        session.setFlaggedCount(0);
        scExamSessionMapper.insertScExamSession(session);
        initSessionPapers(session.getSessionId(), paperId);
        return session.getSessionId();
    }

    private void initSessionPapers(Long sessionId, Long rootPaperId) {
        ScExamPaper rootPaper = scExamPaperMapper.selectScExamPaperByPaperId(rootPaperId);
        if (rootPaper == null) {
            return;
        }
        saveSessionPaper(sessionId, rootPaper, "ONGOING");
        ScExamPaper query = new ScExamPaper();
        query.setParentPaperId(rootPaperId);
        List<ScExamPaper> subPapers = scExamPaperMapper.selectScExamPaperList(query);
        if (subPapers != null) {
            for (ScExamPaper subPaper : subPapers) {
                saveSessionPaper(sessionId, subPaper, "PENDING");
            }
        }
    }

    private void saveSessionPaper(Long sessionId, ScExamPaper paper, String status) {
        if (sessionId == null || paper == null || paper.getPaperId() == null) {
            return;
        }
        ScExamSessionPaper exists = scExamSessionPaperMapper.selectScExamSessionPaperBySessionAndPaper(sessionId,
                paper.getPaperId());
        if (exists != null) {
            return;
        }
        ScExamSessionPaper sessionPaper = new ScExamSessionPaper();
        sessionPaper.setSessionId(sessionId);
        sessionPaper.setPaperId(paper.getPaperId());
        sessionPaper.setParentPaperId(paper.getParentPaperId());
        sessionPaper.setPaperLevel(paper.getPaperLevel());
        sessionPaper.setAnswerMode(paper.getAnswerMode());
        sessionPaper.setPaperWeight(paper.getPaperWeight());
        sessionPaper.setSortNo(paper.getSortNo());
        sessionPaper.setPaperStatus(status);
        sessionPaper.setStartTime("ONGOING".equals(status) ? new Date() : null);
        sessionPaper.setScore(BigDecimal.ZERO);
        sessionPaper.setCorrectRate(BigDecimal.ZERO);
        scExamSessionPaperMapper.insertScExamSessionPaper(sessionPaper);
    }

    private List<ScExamSessionPaper> loadSessionPapers(ScExamRecord record) {
        if (record == null || record.getSessionId() == null) {
            return new ArrayList<>();
        }
        ScExamSessionPaper query = new ScExamSessionPaper();
        query.setSessionId(record.getSessionId());
        return scExamSessionPaperMapper.selectScExamSessionPaperList(query);
    }

    private Long resolveSessionPaperId(List<ScExamSessionPaper> sessionPapers, Long questionId) {
        if (questionId == null || sessionPapers == null || sessionPapers.isEmpty()) {
            return null;
        }
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            if (sessionPaper == null || sessionPaper.getPaperId() == null) {
                continue;
            }
            BigDecimal score = findQuestionScore(
                    scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(sessionPaper.getPaperId()), questionId);
            if (score.compareTo(BigDecimal.ZERO) > 0) {
                return sessionPaper.getId();
            }
        }
        return null;
    }

    private void syncExamSessionAfterSubmit(ScExamRecord record, List<ScExamPaperQuestion> rootQuestions,
            List<Long> answeredQuestionIds, Integer focusLossCount, Integer flaggedCount) {
        if (record == null || record.getSessionId() == null) {
            return;
        }
        ScExamSession session = new ScExamSession();
        session.setSessionId(record.getSessionId());
        session.setSubmitTime(record.getSubmitTime());
        session.setSessionStatus("SUBMITTED");
        session.setTotalScore(record.getScore());
        session.setCorrectRate(record.getCorrectRate());
        session.setFocusLossCount(focusLossCount == null ? 0 : focusLossCount);
        session.setFlaggedCount(flaggedCount == null ? 0 : flaggedCount);
        session.setAnalysisJson(record.getAnalysisJson());
        scExamSessionMapper.updateScExamSession(session);

        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record);
        for (ScExamSessionPaper sessionPaper : sessionPapers) {
            updateSessionPaperScore(sessionPaper, answeredQuestionIds);
        }
        writeBehaviorLog(record, focusLossCount, flaggedCount);
    }

    private void updateSessionPaperScore(ScExamSessionPaper sessionPaper, List<Long> answeredQuestionIds) {
        if (sessionPaper == null || sessionPaper.getId() == null || sessionPaper.getPaperId() == null) {
            return;
        }
        List<ScExamPaperQuestion> questions = scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(sessionPaper.getPaperId());
        if (questions == null || questions.isEmpty()) {
            sessionPaper.setPaperStatus("SUBMITTED");
            sessionPaper.setSubmitTime(new Date());
            sessionPaper.setScore(BigDecimal.ZERO);
            sessionPaper.setCorrectRate(BigDecimal.ZERO);
            scExamSessionPaperMapper.updateScExamSessionPaper(sessionPaper);
            return;
        }
        ScExamAnswer query = new ScExamAnswer();
        query.setSessionPaperId(sessionPaper.getId());
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerList(query);
        BigDecimal score = BigDecimal.ZERO;
        int correctCount = 0;
        for (ScExamAnswer answer : answers) {
            score = score.add(answer.getScore() == null ? BigDecimal.ZERO : answer.getScore());
            if ("1".equals(answer.getIsCorrect())) {
                correctCount++;
            }
        }
        sessionPaper.setPaperStatus(isPaperAnswered(questions, answeredQuestionIds) ? "SUBMITTED" : "SKIPPED");
        sessionPaper.setSubmitTime(new Date());
        sessionPaper.setScore(score.setScale(2, RoundingMode.HALF_UP));
        sessionPaper.setCorrectRate(questions.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(correctCount * 100.0 / questions.size()).setScale(2, RoundingMode.HALF_UP));
        scExamSessionPaperMapper.updateScExamSessionPaper(sessionPaper);
    }

    private boolean isPaperAnswered(List<ScExamPaperQuestion> questions, List<Long> answeredQuestionIds) {
        if (questions == null || questions.isEmpty()) {
            return false;
        }
        for (ScExamPaperQuestion question : questions) {
            if (question != null && question.getQuestionId() != null && answeredQuestionIds.contains(question.getQuestionId())) {
                return true;
            }
        }
        return false;
    }

    private void writeBehaviorLog(ScExamRecord record, Integer focusLossCount, Integer flaggedCount) {
        if (record == null || record.getSessionId() == null) {
            return;
        }
        insertBehaviorLog(record, "MANUAL_SUBMIT", 1, record.getAnalysisJson());
        if (flaggedCount != null && flaggedCount > 0) {
            insertBehaviorLog(record, "FLAGGED", flaggedCount, record.getAnalysisJson());
        }
    }

    private void insertBehaviorLog(ScExamRecord record, String behaviorType, Integer behaviorCount, String behaviorData) {
        insertBehaviorLog(record, behaviorType, behaviorCount, behaviorData, new Date());
    }

    private void insertBehaviorLog(ScExamRecord record, String behaviorType, Integer behaviorCount, String behaviorData,
            Date behaviorTime) {
        ScExamBehaviorLog log = new ScExamBehaviorLog();
        log.setSessionId(record.getSessionId());
        log.setRecordId(record.getRecordId());
        log.setBehaviorType(behaviorType);
        log.setBehaviorCount(behaviorCount == null ? 1 : behaviorCount);
        log.setBehaviorTime(behaviorTime == null ? new Date() : behaviorTime);
        log.setBehaviorData(behaviorData);
        scExamBehaviorLogMapper.insertScExamBehaviorLog(log);
    }

    private void removeQuestionAnswer(Long recordId, Long questionId, Long sessionPaperId) {
        ScExamAnswer query = new ScExamAnswer();
        query.setRecordId(recordId);
        query.setQuestionId(questionId);
        query.setSessionPaperId(sessionPaperId);
        List<ScExamAnswer> exists = scExamAnswerMapper.selectScExamAnswerList(query);
        for (ScExamAnswer item : exists) {
            if (item != null && item.getAnswerId() != null) {
                scExamAnswerMapper.deleteScExamAnswerByAnswerId(item.getAnswerId());
            }
        }
    }

    private ScExamSessionPaper refreshSessionPaperStats(ScExamSessionPaper sessionPaper, String operator) {
        if (sessionPaper == null || sessionPaper.getId() == null) {
            return null;
        }
        List<ScExamPaperQuestion> questions = scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(sessionPaper.getPaperId());
        ScExamAnswer query = new ScExamAnswer();
        query.setSessionPaperId(sessionPaper.getId());
        List<ScExamAnswer> answers = scExamAnswerMapper.selectScExamAnswerList(query);
        BigDecimal score = BigDecimal.ZERO;
        int correctCount = 0;
        for (ScExamAnswer answer : answers) {
            score = score.add(answer.getScore() == null ? BigDecimal.ZERO : answer.getScore());
            if ("1".equals(answer.getIsCorrect())) {
                correctCount++;
            }
        }
        sessionPaper.setScore(score.setScale(2, RoundingMode.HALF_UP));
        sessionPaper.setCorrectRate(questions == null || questions.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(correctCount * 100.0 / questions.size()).setScale(2, RoundingMode.HALF_UP));
        sessionPaper.setUpdateBy(operator);
        scExamSessionPaperMapper.updateScExamSessionPaper(sessionPaper);
        return scExamSessionPaperMapper.selectScExamSessionPaperById(sessionPaper.getId());
    }

    private Long resolveDraftSessionPaperId(ScExamRecord record, Long questionId, Long providedSessionPaperId) {
        if (providedSessionPaperId != null) {
            return providedSessionPaperId;
        }
        if (record == null || record.getSessionId() == null || questionId == null) {
            return null;
        }
        List<ScExamSessionPaper> sessionPapers = loadSessionPapers(record);
        return resolveSessionPaperId(sessionPapers, questionId);
    }

    private BigDecimal findQuestionScore(List<ScExamPaperQuestion> paperQuestions, Long questionId) {
        for (ScExamPaperQuestion paperQuestion : paperQuestions) {
            if (questionId.equals(paperQuestion.getQuestionId())) {
                return paperQuestion.getScore() == null ? BigDecimal.ZERO : paperQuestion.getScore();
            }
        }
        return BigDecimal.ZERO;
    }

    private void saveWrongBook(Long userId, ScQuestionBank question) {
        ScWrongQuestionBook wrong = scWrongQuestionBookMapper.selectScWrongQuestionBookByUserAndQuestion(userId,
                question.getQuestionId());
        if (wrong == null) {
            wrong = new ScWrongQuestionBook();
            wrong.setUserId(userId);
            wrong.setQuestionId(question.getQuestionId());
            wrong.setCourseId(question.getCourseId());
            wrong.setWrongCount(1);
            wrong.setLastWrongTime(new Date());
            wrong.setMasteryStatus("0");
            scWrongQuestionBookMapper.insertScWrongQuestionBook(wrong);
        } else {
            wrong.setWrongCount((wrong.getWrongCount() == null ? 0 : wrong.getWrongCount()) + 1);
            wrong.setLastWrongTime(new Date());
            wrong.setMasteryStatus("0");
            scWrongQuestionBookMapper.updateScWrongQuestionBook(wrong);
        }
    }

    private String buildAnalysisJson(int total, int correct, BigDecimal score, Integer focusLossCount,
            Integer flaggedCount) {
        int safeFocusLossCount = focusLossCount == null ? 0 : Math.max(0, focusLossCount);
        int safeFlaggedCount = flaggedCount == null ? 0 : Math.max(0, flaggedCount);
        String warningLevel = safeFocusLossCount >= 3 ? "HIGH" : safeFocusLossCount >= 1 ? "MEDIUM" : "LOW";
        return String.format(
                "{\"total\":%d,\"correct\":%d,\"score\":%s,\"focusLossCount\":%d,\"flaggedCount\":%d,\"warningLevel\":\"%s\"}",
                total, correct, score.setScale(2, RoundingMode.HALF_UP).toPlainString(), safeFocusLossCount,
                safeFlaggedCount, warningLevel);
    }

    private boolean isAnswerCorrect(String questionType, String userAnswer, String standardAnswer) {
        String type = StringUtils.defaultIfEmpty(questionType, "").trim().toLowerCase(Locale.ROOT);
        if ("multiple".equals(type)) {
            return normalizeMultipleAnswer(userAnswer).equals(normalizeMultipleAnswer(standardAnswer));
        }
        if ("judge".equals(type)) {
            return normalizeJudgeAnswer(userAnswer).equals(normalizeJudgeAnswer(standardAnswer));
        }
        if ("fill".equals(type)) {
            return normalizeFillAnswer(userAnswer).equals(normalizeFillAnswer(standardAnswer));
        }
        return normalizeCommonAnswer(userAnswer).equals(normalizeCommonAnswer(standardAnswer));
    }

    private String normalizeMultipleAnswer(String answer) {
        Set<String> values = new LinkedHashSet<>();
        String normalized = StringUtils.defaultIfEmpty(answer, "")
                .replace("，", ",")
                .replace("、", ",")
                .replace(";", ",")
                .replace("；", ",");
        Arrays.stream(normalized.split(","))
                .map(item -> StringUtils.defaultIfEmpty(item, "").trim().toUpperCase(Locale.ROOT))
                .filter(StringUtils::isNotEmpty)
                .sorted()
                .forEach(values::add);
        return String.join(",", values);
    }

    private String normalizeJudgeAnswer(String answer) {
        String normalized = StringUtils.defaultIfEmpty(answer, "").trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "1":
            case "y":
            case "yes":
            case "true":
            case "对":
            case "正确":
            case "是":
                return "true";
            case "0":
            case "n":
            case "no":
            case "false":
            case "错":
            case "错误":
            case "否":
                return "false";
            default:
                return normalized;
        }
    }

    private String normalizeFillAnswer(String answer) {
        return normalizeCommonAnswer(answer).replace(" ", "");
    }

    private String normalizeCommonAnswer(String answer) {
        return StringUtils.defaultIfEmpty(answer, "").trim().toLowerCase(Locale.ROOT);
    }
}
