package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

public interface IScLearningWarningService
{
    ScLearningWarning selectScLearningWarningByWarningId(Long warningId);
    List<ScLearningWarning> selectScLearningWarningList(ScLearningWarning scLearningWarning);
    int insertScLearningWarning(ScLearningWarning scLearningWarning);
    int updateScLearningWarning(ScLearningWarning scLearningWarning);
    int deleteScLearningWarningByWarningIds(Long[] warningIds);
    int deleteScLearningWarningByWarningId(Long warningId);
    ScLearningWarning buildWarning(Long userId, Long courseId);
}
