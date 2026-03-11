package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamPaperQuestion;
import com.smart.system.mapper.ScExamPaperQuestionMapper;
import com.smart.system.service.IScExamPaperQuestionService;

@Service
public class ScExamPaperQuestionServiceImpl implements IScExamPaperQuestionService
{
    @Autowired
    private ScExamPaperQuestionMapper scExamPaperQuestionMapper;
    public ScExamPaperQuestion selectScExamPaperQuestionById(Long id) { return scExamPaperQuestionMapper.selectScExamPaperQuestionById(id); }
    public List<ScExamPaperQuestion> selectScExamPaperQuestionList(ScExamPaperQuestion scExamPaperQuestion) { return scExamPaperQuestionMapper.selectScExamPaperQuestionList(scExamPaperQuestion); }
    public List<ScExamPaperQuestion> selectScExamPaperQuestionByPaperId(Long paperId) { return scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(paperId); }
    public int insertScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion) { return scExamPaperQuestionMapper.insertScExamPaperQuestion(scExamPaperQuestion); }
    public int updateScExamPaperQuestion(ScExamPaperQuestion scExamPaperQuestion) { return scExamPaperQuestionMapper.updateScExamPaperQuestion(scExamPaperQuestion); }
    public int deleteScExamPaperQuestionByIds(Long[] ids) { return scExamPaperQuestionMapper.deleteScExamPaperQuestionByIds(ids); }
    public int deleteScExamPaperQuestionById(Long id) { return scExamPaperQuestionMapper.deleteScExamPaperQuestionById(id); }
    public int deleteScExamPaperQuestionByPaperId(Long paperId) { return scExamPaperQuestionMapper.deleteScExamPaperQuestionByPaperId(paperId); }
}
