package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.dto.RecommendationFeedbackDto;

public interface IScLearningRecommendationService
{
    ScLearningRecommendation selectScLearningRecommendationByRecommendId(Long recommendId);
    List<ScLearningRecommendation> selectScLearningRecommendationList(ScLearningRecommendation scLearningRecommendation);
    int insertScLearningRecommendation(ScLearningRecommendation scLearningRecommendation);
    int updateScLearningRecommendation(ScLearningRecommendation scLearningRecommendation);
    int deleteScLearningRecommendationByRecommendIds(Long[] recommendIds);
    int deleteScLearningRecommendationByRecommendId(Long recommendId);
    List<ScLearningRecommendation> generateRecommendations(Long userId, String sceneCode, Integer limit);
    List<ScLearningRecommendation> listActiveRecommendations(Long userId, String sceneCode, Integer limit);
    int feedbackRecommendation(RecommendationFeedbackDto feedbackDto);
}
