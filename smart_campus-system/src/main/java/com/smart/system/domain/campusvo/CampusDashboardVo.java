package com.smart.system.domain.campusvo;

import java.util.List;
import com.smart.system.domain.ScLearningWarning;

/**
 * 校园首页概览 VO（管理端增强版）
 *
 * @author can
 */
public class CampusDashboardVo {
    private Long userId;
    private Integer resourceCount;
    private Integer studyRecordCount;
    private Integer warningCount;
    private Integer examRecordCount;
    private LearningProfileOverviewVo profile;
    private List<RecommendationItemVo> recommendations;
    private List<ScLearningWarning> warnings;

    // ===== 管理端聚合字段 =====

    /** 用户统计 */
    private Integer userTotal;
    private Integer studentCount;
    private Integer teacherCount;
    private Integer parentCount;
    private Integer adminCount;

    /** 教学统计 */
    private Integer gradeCount;
    private Integer classCount;
    private Integer courseCount;
    private Integer classroomCount;
    private Integer termCount;
    private Integer enabledTermCount;

    /** 排课统计 */
    private Integer classCourseCount;
    private Integer arrangedClassCourseCount;
    private Integer pendingArrangeCount;
    private Integer scheduleCount;

    /** 学习与资源 */
    private Integer learningTaskCount;
    private Integer questionBankCount;

    /** 当前学期 */
    private String currentTermName;

    /** 趋势数据（近 7 天） */
    private List<DailyCountVo> examTrend;
    private List<DailyCountVo> loginTrend;

    // ===== 内部类 =====

    public static class DailyCountVo {
        private String date;
        private Integer count;

        public DailyCountVo() {}

        public DailyCountVo(String date, Integer count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    // ===== 原有 getter/setter =====

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
    }

    public Integer getStudyRecordCount() {
        return studyRecordCount;
    }

    public void setStudyRecordCount(Integer studyRecordCount) {
        this.studyRecordCount = studyRecordCount;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public Integer getExamRecordCount() {
        return examRecordCount;
    }

    public void setExamRecordCount(Integer examRecordCount) {
        this.examRecordCount = examRecordCount;
    }

    public LearningProfileOverviewVo getProfile() {
        return profile;
    }

    public void setProfile(LearningProfileOverviewVo profile) {
        this.profile = profile;
    }

    public List<RecommendationItemVo> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItemVo> recommendations) {
        this.recommendations = recommendations;
    }

    public List<ScLearningWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ScLearningWarning> warnings) {
        this.warnings = warnings;
    }

    // ===== 管理端新增 getter/setter =====

    public Integer getUserTotal() { return userTotal; }
    public void setUserTotal(Integer userTotal) { this.userTotal = userTotal; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public Integer getTeacherCount() { return teacherCount; }
    public void setTeacherCount(Integer teacherCount) { this.teacherCount = teacherCount; }

    public Integer getParentCount() { return parentCount; }
    public void setParentCount(Integer parentCount) { this.parentCount = parentCount; }

    public Integer getAdminCount() { return adminCount; }
    public void setAdminCount(Integer adminCount) { this.adminCount = adminCount; }

    public Integer getGradeCount() { return gradeCount; }
    public void setGradeCount(Integer gradeCount) { this.gradeCount = gradeCount; }

    public Integer getClassCount() { return classCount; }
    public void setClassCount(Integer classCount) { this.classCount = classCount; }

    public Integer getCourseCount() { return courseCount; }
    public void setCourseCount(Integer courseCount) { this.courseCount = courseCount; }

    public Integer getClassroomCount() { return classroomCount; }
    public void setClassroomCount(Integer classroomCount) { this.classroomCount = classroomCount; }

    public Integer getTermCount() { return termCount; }
    public void setTermCount(Integer termCount) { this.termCount = termCount; }

    public Integer getEnabledTermCount() { return enabledTermCount; }
    public void setEnabledTermCount(Integer enabledTermCount) { this.enabledTermCount = enabledTermCount; }

    public Integer getClassCourseCount() { return classCourseCount; }
    public void setClassCourseCount(Integer classCourseCount) { this.classCourseCount = classCourseCount; }

    public Integer getArrangedClassCourseCount() { return arrangedClassCourseCount; }
    public void setArrangedClassCourseCount(Integer arrangedClassCourseCount) { this.arrangedClassCourseCount = arrangedClassCourseCount; }

    public Integer getPendingArrangeCount() { return pendingArrangeCount; }
    public void setPendingArrangeCount(Integer pendingArrangeCount) { this.pendingArrangeCount = pendingArrangeCount; }

    public Integer getScheduleCount() { return scheduleCount; }
    public void setScheduleCount(Integer scheduleCount) { this.scheduleCount = scheduleCount; }

    public Integer getLearningTaskCount() { return learningTaskCount; }
    public void setLearningTaskCount(Integer learningTaskCount) { this.learningTaskCount = learningTaskCount; }

    public Integer getQuestionBankCount() { return questionBankCount; }
    public void setQuestionBankCount(Integer questionBankCount) { this.questionBankCount = questionBankCount; }

    public String getCurrentTermName() { return currentTermName; }
    public void setCurrentTermName(String currentTermName) { this.currentTermName = currentTermName; }

    public List<DailyCountVo> getExamTrend() { return examTrend; }
    public void setExamTrend(List<DailyCountVo> examTrend) { this.examTrend = examTrend; }

    public List<DailyCountVo> getLoginTrend() { return loginTrend; }
    public void setLoginTrend(List<DailyCountVo> loginTrend) { this.loginTrend = loginTrend; }
}
