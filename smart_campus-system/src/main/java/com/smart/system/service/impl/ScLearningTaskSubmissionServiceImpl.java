package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLearningTaskSubmission;
import com.smart.system.mapper.ScLearningTaskSubmissionMapper;
import com.smart.system.service.IScLearningTaskSubmissionService;

@Service
public class ScLearningTaskSubmissionServiceImpl implements IScLearningTaskSubmissionService {
    @Autowired
    private ScLearningTaskSubmissionMapper scLearningTaskSubmissionMapper;

    @Override
    public ScLearningTaskSubmission selectScLearningTaskSubmissionBySubmissionId(Long submissionId) {
        return scLearningTaskSubmissionMapper.selectScLearningTaskSubmissionBySubmissionId(submissionId);
    }

    @Override
    public List<ScLearningTaskSubmission> selectScLearningTaskSubmissionList(ScLearningTaskSubmission scLearningTaskSubmission) {
        return scLearningTaskSubmissionMapper.selectScLearningTaskSubmissionList(scLearningTaskSubmission);
    }

    @Override
    public int insertScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission) {
        return scLearningTaskSubmissionMapper.insertScLearningTaskSubmission(scLearningTaskSubmission);
    }

    @Override
    public int updateScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission) {
        return scLearningTaskSubmissionMapper.updateScLearningTaskSubmission(scLearningTaskSubmission);
    }

    @Override
    public int deleteScLearningTaskSubmissionBySubmissionIds(Long[] submissionIds) {
        return scLearningTaskSubmissionMapper.deleteScLearningTaskSubmissionBySubmissionIds(submissionIds);
    }

    @Override
    public int deleteScLearningTaskSubmissionBySubmissionId(Long submissionId) {
        return scLearningTaskSubmissionMapper.deleteScLearningTaskSubmissionBySubmissionId(submissionId);
    }

    @Override
    public ScLearningTaskSubmission selectByDispatchId(Long dispatchId) {
        return scLearningTaskSubmissionMapper.selectByDispatchId(dispatchId);
    }
}
