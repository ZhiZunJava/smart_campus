package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQaFeedback;

public interface IScQaFeedbackService {
    ScQaFeedback selectScQaFeedbackById(Long id);

    List<ScQaFeedback> selectScQaFeedbackList(ScQaFeedback scQaFeedback);

    int insertScQaFeedback(ScQaFeedback scQaFeedback);

    int updateScQaFeedback(ScQaFeedback scQaFeedback);

    int deleteScQaFeedbackByIds(Long[] ids);

    int deleteScQaFeedbackById(Long id);
}
