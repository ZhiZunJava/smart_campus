package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamPaperQuestion;

public interface ScExamPaperQuestionMapper {
    ScExamPaperQuestion selectScExamPaperQuestionById(Long id);

    List<ScExamPaperQuestion> selectScExamPaperQuestionList(ScExamPaperQuestion scExamPaperQuestion);

    List<ScExamPaperQuestion> selectScExamPaperQuestionByPaperId(Long paperId);

    int insertScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion);

    int updateScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion);

    int deleteScExamPaperQuestionById(Long id);

    int deleteScExamPaperQuestionByIds(Long[] ids);

    int deleteScExamPaperQuestionByPaperId(Long paperId);
}
