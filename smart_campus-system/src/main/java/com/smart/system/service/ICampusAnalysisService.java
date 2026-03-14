package com.smart.system.service;

import com.smart.system.domain.campusvo.AnalysisMetaVo;
import com.smart.system.domain.campusvo.LearningDiagnosisVo;
import com.smart.system.domain.campusvo.LearningWorkbenchVo;

public interface ICampusAnalysisService
{
    LearningDiagnosisVo diagnoseLearning(Long userId, Long courseId, Integer recommendLimit, Boolean autoGenerate);
    LearningWorkbenchVo getLearningWorkbench(Long userId, Long courseId, Integer limit);
    AnalysisMetaVo getAnalysisMeta();
}
