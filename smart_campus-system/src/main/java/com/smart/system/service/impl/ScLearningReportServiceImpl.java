package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScLearningReportMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.service.IScLearningReportService;

@Service
public class ScLearningReportServiceImpl implements IScLearningReportService
{
    @Autowired
    private ScLearningReportMapper scLearningReportMapper;
    @Autowired
    private ScLearningProfileMapper scLearningProfileMapper;
    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    public ScLearningReport selectScLearningReportByReportId(Long reportId) { return scLearningReportMapper.selectScLearningReportByReportId(reportId); }
    public List<ScLearningReport> selectScLearningReportList(ScLearningReport scLearningReport) { return scLearningReportMapper.selectScLearningReportList(scLearningReport); }
    public int insertScLearningReport(ScLearningReport scLearningReport) { return scLearningReportMapper.insertScLearningReport(scLearningReport); }
    public int updateScLearningReport(ScLearningReport scLearningReport) { return scLearningReportMapper.updateScLearningReport(scLearningReport); }
    public int deleteScLearningReportByReportIds(Long[] reportIds) { return scLearningReportMapper.deleteScLearningReportByReportIds(reportIds); }
    public int deleteScLearningReportByReportId(Long reportId) { return scLearningReportMapper.deleteScLearningReportByReportId(reportId); }

    public ScLearningReport generateReport(Long userId, String reportType)
    {
        ScStudyRecord recordQuery = new ScStudyRecord();
        recordQuery.setUserId(userId);
        List<ScStudyRecord> records = scStudyRecordMapper.selectScStudyRecordList(recordQuery);

        ScLearningProfile profileQuery = new ScLearningProfile();
        profileQuery.setUserId(userId);
        List<ScLearningProfile> profiles = scLearningProfileMapper.selectScLearningProfileList(profileQuery);
        ScLearningProfile profile = profiles.isEmpty() ? null : profiles.get(0);

        int behaviorCount = records.size();
        String summary = profile == null
            ? "当前暂无画像数据，建议先进行学习行为采集与画像计算。"
            : String.format("本阶段共记录学习行为%d次，当前能力等级为%s，活跃度%s，掌握度%s，风险分%s。",
                behaviorCount,
                profile.getAbilityLevel(),
                profile.getActiveScore(),
                profile.getMasteryScore(),
                profile.getRiskScore());

        ScLearningReport report = new ScLearningReport();
        report.setUserId(userId);
        report.setReportType(reportType == null ? "weekly" : reportType);
        report.setReportContent(summary);
        report.setReportJson(String.format("{\"behaviorCount\":%d,\"hasProfile\":%s}", behaviorCount, profile != null));
        report.setCreateBy("system");
        scLearningReportMapper.insertScLearningReport(report);
        return report;
    }
}
