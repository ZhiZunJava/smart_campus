package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScExamAnswer;

public interface IScExamAnswerService {
    ScExamAnswer selectScExamAnswerByAnswerId(Long answerId);

    List<ScExamAnswer> selectScExamAnswerList(ScExamAnswer scExamAnswer);

    List<ScExamAnswer> selectScExamAnswerByRecordId(Long recordId);

    int insertScExamAnswer(ScExamAnswer scExamAnswer);

    int updateScExamAnswer(ScExamAnswer scExamAnswer);

    int deleteScExamAnswerByAnswerIds(Long[] answerIds);

    int deleteScExamAnswerByAnswerId(Long answerId);

    int deleteScExamAnswerByRecordId(Long recordId);
}
