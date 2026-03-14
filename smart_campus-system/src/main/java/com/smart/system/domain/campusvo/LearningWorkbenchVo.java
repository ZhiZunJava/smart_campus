package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.domain.ScStudyRecord;

public class LearningWorkbenchVo
{
    private Long userId;
    private Long courseId;
    private LearningProfileOverviewVo profile;
    private List<ScStudyRecord> recentStudyRecords;
    private List<ScExamRecord> recentExamRecords;
    private List<ScLearningWarning> recentWarnings;
    private List<ScLearningReport> recentReports;
    private List<RecommendationItemVo> activeRecommendations;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public LearningProfileOverviewVo getProfile()
    {
        return profile;
    }

    public void setProfile(LearningProfileOverviewVo profile)
    {
        this.profile = profile;
    }

    public List<ScStudyRecord> getRecentStudyRecords()
    {
        return recentStudyRecords;
    }

    public void setRecentStudyRecords(List<ScStudyRecord> recentStudyRecords)
    {
        this.recentStudyRecords = recentStudyRecords;
    }

    public List<ScExamRecord> getRecentExamRecords()
    {
        return recentExamRecords;
    }

    public void setRecentExamRecords(List<ScExamRecord> recentExamRecords)
    {
        this.recentExamRecords = recentExamRecords;
    }

    public List<ScLearningWarning> getRecentWarnings()
    {
        return recentWarnings;
    }

    public void setRecentWarnings(List<ScLearningWarning> recentWarnings)
    {
        this.recentWarnings = recentWarnings;
    }

    public List<ScLearningReport> getRecentReports()
    {
        return recentReports;
    }

    public void setRecentReports(List<ScLearningReport> recentReports)
    {
        this.recentReports = recentReports;
    }

    public List<RecommendationItemVo> getActiveRecommendations()
    {
        return activeRecommendations;
    }

    public void setActiveRecommendations(List<RecommendationItemVo> activeRecommendations)
    {
        this.activeRecommendations = activeRecommendations;
    }
}
