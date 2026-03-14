package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        int totalDuration = 0;
        BigDecimal totalProgress = BigDecimal.ZERO;
        for (ScStudyRecord record : records)
        {
            totalDuration += record.getDurationSeconds() == null ? 0 : record.getDurationSeconds();
            totalProgress = totalProgress.add(record.getProgressRate() == null ? BigDecimal.ZERO : record.getProgressRate());
        }
        BigDecimal avgProgress = behaviorCount == 0 ? BigDecimal.ZERO : totalProgress.divide(BigDecimal.valueOf(behaviorCount), 2, RoundingMode.HALF_UP);
        String summary = profile == null
            ? "当前暂无画像数据，建议先进行学习行为采集与画像计算。"
            : String.format("本阶段共记录学习行为%d次，累计学习时长%d秒，当前能力等级为%s，活跃度%s，掌握度%s，平均进度%s，风险分%s。",
                behaviorCount,
                totalDuration,
                profile.getAbilityLevel(),
                profile.getActiveScore(),
                profile.getMasteryScore(),
                avgProgress,
                profile.getRiskScore());

        ScLearningReport report = new ScLearningReport();
        report.setUserId(userId);
        report.setReportType(reportType == null ? "weekly" : reportType);
        report.setReportContent(summary);
        report.setReportJson(buildReportJson(behaviorCount, totalDuration, avgProgress, profile));
        report.setCreateBy("system");
        scLearningReportMapper.insertScLearningReport(report);
        return report;
    }

    private String buildReportJson(int behaviorCount, int totalDuration, BigDecimal avgProgress, ScLearningProfile profile)
    {
        return String.format("{\"behaviorCount\":%d,\"totalDurationSeconds\":%d,\"avgProgress\":%s,\"hasProfile\":%s,\"abilityLevel\":\"%s\",\"activeScore\":%s,\"masteryScore\":%s,\"riskScore\":%s}",
            behaviorCount,
            totalDuration,
            avgProgress.setScale(2, RoundingMode.HALF_UP).toPlainString(),
            profile != null,
            profile == null || profile.getAbilityLevel() == null ? "" : profile.getAbilityLevel(),
            profile == null || profile.getActiveScore() == null ? "null" : profile.getActiveScore().setScale(2, RoundingMode.HALF_UP).toPlainString(),
            profile == null || profile.getMasteryScore() == null ? "null" : profile.getMasteryScore().setScale(2, RoundingMode.HALF_UP).toPlainString(),
            profile == null || profile.getRiskScore() == null ? "null" : profile.getRiskScore().setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
