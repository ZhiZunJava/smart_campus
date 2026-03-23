package com.smart.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.system.domain.*;
import com.smart.system.domain.campusvo.*;
import com.smart.system.service.*;

@Service
public class CampusDetailServiceImpl implements ICampusDetailService {
    @Autowired
    private IScResourceService scResourceService;
    @Autowired
    private IScQuestionBankService scQuestionBankService;
    @Autowired
    private IScQuestionOptionService scQuestionOptionService;
    @Autowired
    private IScExamPaperService scExamPaperService;
    @Autowired
    private IScExamPaperQuestionService scExamPaperQuestionService;
    @Autowired
    private IScQaSessionService scQaSessionService;
    @Autowired
    private IScQaMessageService scQaMessageService;

    @Override
    public ResourceDetailVo getResourceDetail(Long resourceId) {
        ScResource resource = scResourceService.selectScResourceByResourceId(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }
        ResourceDetailVo vo = new ResourceDetailVo();
        vo.setResourceId(resource.getResourceId());
        vo.setResourceName(resource.getResourceName());
        vo.setResourceType(resource.getResourceType());
        vo.setCourseId(resource.getCourseId());
        vo.setChapterId(resource.getChapterId());
        vo.setUploaderId(resource.getUploaderId());
        vo.setFileUrl(resource.getFileUrl());
        vo.setCoverUrl(resource.getCoverUrl());
        vo.setContentText(resource.getContentText());
        vo.setSummary(resource.getSummary());
        vo.setAuditStatus(resource.getAuditStatus());
        vo.setViewCount(resource.getViewCount());
        vo.setDownloadCount(resource.getDownloadCount());
        vo.setFavoriteCount(resource.getFavoriteCount());
        vo.setQualityScore(resource.getQualityScore());
        vo.setStatus(resource.getStatus());
        return vo;
    }

    @Override
    public QuestionDetailVo getQuestionDetail(Long questionId) {
        ScQuestionBank question = scQuestionBankService.selectScQuestionBankByQuestionId(questionId);
        if (question == null) {
            throw new ServiceException("题目不存在");
        }
        QuestionDetailVo vo = new QuestionDetailVo();
        vo.setQuestionId(question.getQuestionId());
        vo.setCourseId(question.getCourseId());
        vo.setChapterId(question.getChapterId());
        vo.setKnowledgePointId(question.getKnowledgePointId());
        vo.setQuestionType(question.getQuestionType());
        vo.setDifficultyLevel(question.getDifficultyLevel());
        vo.setStem(question.getStem());
        vo.setSource(question.getSource());
        vo.setQualityScore(question.getQualityScore());
        vo.setStatus(question.getStatus());
        List<QuestionOptionVo> options = scQuestionOptionService.selectScQuestionOptionByQuestionId(questionId)
                .stream().map(this::toQuestionOptionVo).collect(Collectors.toList());
        vo.setOptions(options);
        return vo;
    }

    @Override
    public ExamPaperDetailVo getExamPaperDetail(Long paperId) {
        ScExamPaper paper = scExamPaperService.selectScExamPaperByPaperId(paperId);
        if (paper == null) {
            throw new ServiceException("试卷不存在");
        }
        ExamPaperDetailVo vo = new ExamPaperDetailVo();
        vo.setPaperId(paper.getPaperId());
        vo.setPaperName(paper.getPaperName());
        vo.setCourseId(paper.getCourseId());
        vo.setPaperType(paper.getPaperType());
        vo.setTotalScore(paper.getTotalScore());
        vo.setDurationMinutes(paper.getDurationMinutes());
        vo.setStatus(paper.getStatus());
        List<ExamPaperQuestionDetailVo> questions = scExamPaperQuestionService
                .selectScExamPaperQuestionByPaperId(paperId)
                .stream().map(this::toExamPaperQuestionDetailVo).collect(Collectors.toList());
        vo.setQuestions(questions);
        return vo;
    }

    @Override
    public QaSessionDetailVo getQaSessionDetail(Long sessionId) {
        ScQaSession session = scQaSessionService.selectScQaSessionBySessionId(sessionId);
        if (session == null) {
            throw new ServiceException("问答会话不存在");
        }
        QaSessionDetailVo vo = new QaSessionDetailVo();
        vo.setSessionId(session.getSessionId());
        vo.setUserId(session.getUserId());
        vo.setCourseId(session.getCourseId());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setSourceType(session.getSourceType());
        vo.setStatus(session.getStatus());
        ScQaMessage query = new ScQaMessage();
        query.setSessionId(sessionId);
        vo.setMessages(scQaMessageService.selectScQaMessageList(query));
        return vo;
    }

    private QuestionOptionVo toQuestionOptionVo(ScQuestionOption option) {
        QuestionOptionVo vo = new QuestionOptionVo();
        vo.setOptionId(option.getOptionId());
        vo.setOptionKey(option.getOptionKey());
        vo.setOptionContent(option.getOptionContent());
        return vo;
    }

    private ExamPaperQuestionDetailVo toExamPaperQuestionDetailVo(ScExamPaperQuestion paperQuestion) {
        ExamPaperQuestionDetailVo vo = new ExamPaperQuestionDetailVo();
        vo.setId(paperQuestion.getId());
        vo.setPaperId(paperQuestion.getPaperId());
        vo.setQuestionId(paperQuestion.getQuestionId());
        vo.setScore(paperQuestion.getScore());
        vo.setSortNo(paperQuestion.getSortNo());
        vo.setQuestion(getQuestionDetail(paperQuestion.getQuestionId()));
        return vo;
    }
}
