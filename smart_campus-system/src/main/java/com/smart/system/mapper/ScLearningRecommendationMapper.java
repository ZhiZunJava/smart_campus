package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningRecommendation;

public interface ScLearningRecommendationMapper {
    ScLearningRecommendation selectScLearningRecommendationByRecommendId(Long recommendId);

    List<ScLearningRecommendation> selectScLearningRecommendationList(
            ScLearningRecommendation scLearningRecommendation);

    int insertScLearningRecommendation(ScLearningRecommendation scLearningRecommendation);

    int updateScLearningRecommendation(ScLearningRecommendation scLearningRecommendation);

    int deleteScLearningRecommendationByRecommendId(Long recommendId);

    int deleteScLearningRecommendationByRecommendIds(Long[] recommendIds);
}
