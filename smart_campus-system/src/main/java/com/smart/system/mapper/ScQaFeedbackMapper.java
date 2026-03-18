package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQaFeedback;

public interface ScQaFeedbackMapper {
    ScQaFeedback selectScQaFeedbackById(Long id);

    List<ScQaFeedback> selectScQaFeedbackList(ScQaFeedback scQaFeedback);

    int insertScQaFeedback(ScQaFeedback scQaFeedback);

    int updateScQaFeedback(ScQaFeedback scQaFeedback);

    int deleteScQaFeedbackById(Long id);

    int deleteScQaFeedbackByIds(Long[] ids);
}
