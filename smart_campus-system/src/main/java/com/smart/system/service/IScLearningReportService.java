package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningReport;

public interface IScLearningReportService {
    ScLearningReport selectScLearningReportByReportId(Long reportId);

    List<ScLearningReport> selectScLearningReportList(ScLearningReport scLearningReport);

    int insertScLearningReport(ScLearningReport scLearningReport);

    int updateScLearningReport(ScLearningReport scLearningReport);

    int deleteScLearningReportByReportIds(Long[] reportIds);

    int deleteScLearningReportByReportId(Long reportId);

    ScLearningReport generateReport(Long userId, String reportType);
}
