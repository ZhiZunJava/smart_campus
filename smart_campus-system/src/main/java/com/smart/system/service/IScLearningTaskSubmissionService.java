package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningTaskSubmission;

public interface IScLearningTaskSubmissionService {
    ScLearningTaskSubmission selectScLearningTaskSubmissionBySubmissionId(Long submissionId);
    List<ScLearningTaskSubmission> selectScLearningTaskSubmissionList(ScLearningTaskSubmission scLearningTaskSubmission);
    int insertScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission);
    int updateScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission);
    int deleteScLearningTaskSubmissionBySubmissionIds(Long[] submissionIds);
    int deleteScLearningTaskSubmissionBySubmissionId(Long submissionId);
    ScLearningTaskSubmission selectByDispatchId(Long dispatchId);
}
