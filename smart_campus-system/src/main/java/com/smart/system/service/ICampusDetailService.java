package com.smart.system.service;

import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.campusvo.QaSessionDetailVo;
import com.smart.system.domain.campusvo.QuestionDetailVo;
import com.smart.system.domain.campusvo.ResourceDetailVo;

/**
 * 校园详情聚合服务
 *
 * @author can
 */
public interface ICampusDetailService {
    ResourceDetailVo getResourceDetail(Long resourceId);

    QuestionDetailVo getQuestionDetail(Long questionId);

    ExamPaperDetailVo getExamPaperDetail(Long paperId);

    QaSessionDetailVo getQaSessionDetail(Long sessionId);
}
