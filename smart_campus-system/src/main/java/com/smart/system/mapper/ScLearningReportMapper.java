package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningReport;

public interface ScLearningReportMapper
{
    ScLearningReport selectScLearningReportByReportId(Long reportId);
    List<ScLearningReport> selectScLearningReportList(ScLearningReport scLearningReport);
    int insertScLearningReport(ScLearningReport scLearningReport);
    int updateScLearningReport(ScLearningReport scLearningReport);
    int deleteScLearningReportByReportId(Long reportId);
    int deleteScLearningReportByReportIds(Long[] reportIds);
}
