package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScExamAnswerDraft;
import com.smart.system.domain.ScExamSessionPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.dto.ExamAnswerDraftSaveDto;
import com.smart.system.domain.dto.ExamQuestionSubmitDto;
import com.smart.system.domain.dto.ExamSubPaperSubmitDto;

public interface IScExamRecordService {
    ScExamRecord selectScExamRecordByRecordId(Long recordId);

    List<ScExamRecord> selectScExamRecordList(ScExamRecord scExamRecord);

    int insertScExamRecord(ScExamRecord scExamRecord);

    int updateScExamRecord(ScExamRecord scExamRecord);

    int deleteScExamRecordByRecordIds(Long[] recordIds);

    int deleteScExamRecordByRecordId(Long recordId);

    ScExamRecord startExam(Long paperId, Long userId);

    ScExamAnswerDraft saveAnswerDraft(ExamAnswerDraftSaveDto dto, Long userId, String operator);

    List<ScExamAnswerDraft> listAnswerDrafts(Long recordId, Long userId);

    ScExamSessionPaper submitQuestionAnswer(ExamQuestionSubmitDto dto, Long userId, String operator);

    ScExamSessionPaper submitSubPaper(ExamSubPaperSubmitDto dto, Long userId, String operator);

    ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers);

    ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers, Integer focusLossCount, Integer flaggedCount);
}
