package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamAnswer;

public interface ScExamAnswerMapper
{
    ScExamAnswer selectScExamAnswerByAnswerId(Long answerId);
    List<ScExamAnswer> selectScExamAnswerList(ScExamAnswer scExamAnswer);
    List<ScExamAnswer> selectScExamAnswerByRecordId(Long recordId);
    int insertScExamAnswer(ScExamAnswer scExamAnswer);
    int updateScExamAnswer(ScExamAnswer scExamAnswer);
    int deleteScExamAnswerByAnswerId(Long answerId);
    int deleteScExamAnswerByAnswerIds(Long[] answerIds);
    int deleteScExamAnswerByRecordId(Long recordId);
}
