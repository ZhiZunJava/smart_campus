package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningTaskDispatch;

public interface IScLearningTaskDispatchService {
    ScLearningTaskDispatch selectScLearningTaskDispatchByDispatchId(Long dispatchId);
    List<ScLearningTaskDispatch> selectScLearningTaskDispatchList(ScLearningTaskDispatch scLearningTaskDispatch);
    int insertScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch);
    int updateScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch);
    int deleteScLearningTaskDispatchByDispatchIds(Long[] dispatchIds);
    int deleteScLearningTaskDispatchByDispatchId(Long dispatchId);
    int markDispatchRead(Long dispatchId, Long userId, String operator);
    int completeDispatch(Long dispatchId, Long userId, String completionRemark, String operator);
}
