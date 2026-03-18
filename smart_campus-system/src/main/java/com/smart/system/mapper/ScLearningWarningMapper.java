package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

public interface ScLearningWarningMapper {
    ScLearningWarning selectScLearningWarningByWarningId(Long warningId);

    List<ScLearningWarning> selectScLearningWarningList(ScLearningWarning scLearningWarning);

    int insertScLearningWarning(ScLearningWarning scLearningWarning);

    int updateScLearningWarning(ScLearningWarning scLearningWarning);

    int deleteScLearningWarningByWarningId(Long warningId);

    int deleteScLearningWarningByWarningIds(Long[] warningIds);
}
