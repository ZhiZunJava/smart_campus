package com.smart.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLearningTaskDispatch;
import com.smart.system.mapper.ScLearningTaskMapper;
import com.smart.system.mapper.ScLearningTaskDispatchMapper;
import com.smart.system.service.IScLearningTaskDispatchService;

@Service
public class ScLearningTaskDispatchServiceImpl implements IScLearningTaskDispatchService {
    @Autowired
    private ScLearningTaskDispatchMapper scLearningTaskDispatchMapper;

    @Autowired
    private ScLearningTaskMapper scLearningTaskMapper;

    @Override
    public ScLearningTaskDispatch selectScLearningTaskDispatchByDispatchId(Long dispatchId) {
        return scLearningTaskDispatchMapper.selectScLearningTaskDispatchByDispatchId(dispatchId);
    }

    @Override
    public List<ScLearningTaskDispatch> selectScLearningTaskDispatchList(ScLearningTaskDispatch scLearningTaskDispatch) {
        return scLearningTaskDispatchMapper.selectScLearningTaskDispatchList(scLearningTaskDispatch);
    }

    @Override
    public int insertScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch) {
        return scLearningTaskDispatchMapper.insertScLearningTaskDispatch(scLearningTaskDispatch);
    }

    @Override
    public int updateScLearningTaskDispatch(ScLearningTaskDispatch scLearningTaskDispatch) {
        return scLearningTaskDispatchMapper.updateScLearningTaskDispatch(scLearningTaskDispatch);
    }

    @Override
    public int deleteScLearningTaskDispatchByDispatchIds(Long[] dispatchIds) {
        return scLearningTaskDispatchMapper.deleteScLearningTaskDispatchByDispatchIds(dispatchIds);
    }

    @Override
    public int deleteScLearningTaskDispatchByDispatchId(Long dispatchId) {
        return scLearningTaskDispatchMapper.deleteScLearningTaskDispatchByDispatchId(dispatchId);
    }

    @Override
    public int markDispatchRead(Long dispatchId, Long userId, String operator) {
        ScLearningTaskDispatch dispatch = scLearningTaskDispatchMapper.selectScLearningTaskDispatchByDispatchId(dispatchId);
        if (dispatch == null || userId == null || !userId.equals(dispatch.getUserId())) {
            return 0;
        }
        if (dispatch.getReadTime() == null) {
            dispatch.setReadTime(new Date());
        }
        if ("PENDING".equals(dispatch.getCompletionStatus())) {
            dispatch.setCompletionStatus("IN_PROGRESS");
        }
        dispatch.setUpdateBy(operator);
        return scLearningTaskDispatchMapper.updateScLearningTaskDispatch(dispatch);
    }

    @Override
    public int completeDispatch(Long dispatchId, Long userId, String completionRemark, String operator) {
        ScLearningTaskDispatch dispatch = scLearningTaskDispatchMapper.selectScLearningTaskDispatchByDispatchId(dispatchId);
        if (dispatch == null || userId == null || !userId.equals(dispatch.getUserId())) {
            return 0;
        }
        dispatch.setReadTime(dispatch.getReadTime() == null ? new Date() : dispatch.getReadTime());
        dispatch.setCompletionStatus("COMPLETED");
        dispatch.setCompletedTime(new Date());
        dispatch.setCompletionRemark(completionRemark);
        dispatch.setUpdateBy(operator);
        return scLearningTaskDispatchMapper.updateScLearningTaskDispatch(dispatch);
    }
}
