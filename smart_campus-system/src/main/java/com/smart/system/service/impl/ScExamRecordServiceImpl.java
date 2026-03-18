package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamAnswer;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamPaperQuestion;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.mapper.ScExamAnswerMapper;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamPaperQuestionMapper;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScQuestionBankMapper;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScLearningProfileService;
import com.smart.system.service.IScLearningReportService;
import com.smart.system.service.IScLearningWarningService;

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
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;
    @Autowired
    private ScExamPaperMapper scExamPaperMapper;
    @Autowired
    private IScLearningProfileService scLearningProfileService;
    @Autowired
    private IScLearningWarningService scLearningWarningService;
    @Autowired
    private IScLearningReportService scLearningReportService;

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
        ScExamRecord record = new ScExamRecord();
        record.setPaperId(paperId);
        record.setUserId(userId);
        record.setStartTime(new Date());
        record.setExamStatus("ONGOING");
        record.setCheatFlag("0");
        record.setScore(BigDecimal.ZERO);
        record.setCorrectRate(BigDecimal.ZERO);
        scExamRecordMapper.insertScExamRecord(record);
        return record;
    }

    @Override
    public ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers) {
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null) {
            return null;
        }
        scExamAnswerMapper.deleteScExamAnswerByRecordId(recordId);
        List<ScExamPaperQuestion> paperQuestions = scExamPaperQuestionMapper
                .selectScExamPaperQuestionByPaperId(record.getPaperId());
        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;

        for (Map<String, Object> item : answers) {
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
            boolean correct = userAnswer.trim()
                    .equalsIgnoreCase(question.getAnswer() == null ? "" : question.getAnswer().trim());
            ScExamAnswer answer = new ScExamAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(questionId);
            answer.setUserAnswer(userAnswer);
            answer.setIsCorrect(correct ? "1" : "0");
            answer.setScore(correct ? itemScore : BigDecimal.ZERO);
            answer.setKnowledgePointId(question.getKnowledgePointId());
            scExamAnswerMapper.insertScExamAnswer(answer);

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
        record.setAnalysisJson(buildAnalysisJson(paperQuestions.size(), correctCount, totalScore));
        scExamRecordMapper.updateScExamRecord(record);

        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(record.getPaperId());
        Long courseId = paper == null ? null : paper.getCourseId();
        if (courseId != null) {
            scLearningProfileService.rebuildProfile(record.getUserId(), courseId);
            scLearningWarningService.buildWarning(record.getUserId(), courseId);
        }
        scLearningReportService.generateReport(record.getUserId(), "exam_after_submit");
        return record;
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

    private String buildAnalysisJson(int total, int correct, BigDecimal score) {
        return String.format("{\"total\":%d,\"correct\":%d,\"score\":%s}", total, correct,
                score.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
