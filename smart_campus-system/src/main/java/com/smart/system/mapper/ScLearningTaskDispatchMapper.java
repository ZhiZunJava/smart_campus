package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningTaskDispatch;

public interface ScLearningTaskDispatchMapper {
    ScLearningTaskDispatch selectScLearningTaskDispatchByDispatchId(Long dispatchId);
    List<ScLearningTaskDispatch> selectScLearningTaskDispatchList(ScLearningTaskDispatch scLearningTaskDispatch);
    int insertScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch);
    int updateScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch);
    int deleteScLearningTaskDispatchByDispatchId(Long dispatchId);
    int deleteScLearningTaskDispatchByDispatchIds(Long[] dispatchIds);
    ScLearningTaskDispatch selectByTaskIdAndUserId(Long taskId, Long userId);
}
