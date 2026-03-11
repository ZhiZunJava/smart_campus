package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScExamPaperQuestion;

public interface IScExamPaperQuestionService
{
    ScExamPaperQuestion selectScExamPaperQuestionById(Long id);
    List<ScExamPaperQuestion> selectScExamPaperQuestionList(ScExamPaperQuestion scExamPaperQuestion);
    List<ScExamPaperQuestion> selectScExamPaperQuestionByPaperId(Long paperId);
    int insertScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion);
    int updateScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion);
    int deleteScExamPaperQuestionByIds(Long[] ids);
    int deleteScExamPaperQuestionById(Long id);
    int deleteScExamPaperQuestionByPaperId(Long paperId);
}
