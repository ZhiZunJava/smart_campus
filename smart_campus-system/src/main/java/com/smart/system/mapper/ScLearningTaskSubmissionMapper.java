package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningTaskSubmission;

public interface ScLearningTaskSubmissionMapper {
    ScLearningTaskSubmission selectScLearningTaskSubmissionBySubmissionId(Long submissionId);
    List<ScLearningTaskSubmission> selectScLearningTaskSubmissionList(ScLearningTaskSubmission scLearningTaskSubmission);
    int insertScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission);
    int updateScLearningTaskSubmission(ScLearningTaskSubmission scLearningTaskSubmission);
    int deleteScLearningTaskSubmissionBySubmissionId(Long submissionId);
    int deleteScLearningTaskSubmissionBySubmissionIds(Long[] submissionIds);
    ScLearningTaskSubmission selectByDispatchId(Long dispatchId);
}
