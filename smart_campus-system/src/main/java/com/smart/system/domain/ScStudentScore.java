package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学生成绩台账对象 sc_student_score
 */
public class ScStudentScore extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long scoreId;
    private Long classCourseId;
    private String teachingClassCode;
    private Long termId;
    private String termName;
    private Long courseId;
    private String courseName;
    private Long classId;
    private String className;
    private Long studentUserId;
    private String studentName;
    private String studentNo;
    private String assessmentType;
    private BigDecimal credits;
    private BigDecimal usualScore;
    private BigDecimal attendanceScore;
    private BigDecimal homeworkScore;
    private BigDecimal labScore;
    private BigDecimal examScore;
    private BigDecimal examAvgScore;
    private BigDecimal bonusScore;
    private BigDecimal penaltyScore;
    private BigDecimal usualWeight;
    private BigDecimal attendanceWeight;
    private BigDecimal homeworkWeight;
    private BigDecimal labWeight;
    private BigDecimal examWeight;
    private BigDecimal totalScore;
    private BigDecimal gradePoint;
    private String gradeLevel;
    private String passFlag;
    private Integer rankNo;
    private BigDecimal percentile;
    private Integer examRecordCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
    private String publishStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    private String publishBy;
    private String teacherComment;
    private String status;

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Long classCourseId) {
        this.classCourseId = classCourseId;
    }

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public BigDecimal getUsualScore() {
        return usualScore;
    }

    public void setUsualScore(BigDecimal usualScore) {
        this.usualScore = usualScore;
    }

    public BigDecimal getAttendanceScore() {
        return attendanceScore;
    }

    public void setAttendanceScore(BigDecimal attendanceScore) {
        this.attendanceScore = attendanceScore;
    }

    public BigDecimal getHomeworkScore() {
        return homeworkScore;
    }

    public void setHomeworkScore(BigDecimal homeworkScore) {
        this.homeworkScore = homeworkScore;
    }

    public BigDecimal getLabScore() {
        return labScore;
    }

    public void setLabScore(BigDecimal labScore) {
        this.labScore = labScore;
    }

    public BigDecimal getExamScore() {
        return examScore;
    }

    public void setExamScore(BigDecimal examScore) {
        this.examScore = examScore;
    }

    public BigDecimal getExamAvgScore() {
        return examAvgScore;
    }

    public void setExamAvgScore(BigDecimal examAvgScore) {
        this.examAvgScore = examAvgScore;
    }

    public BigDecimal getBonusScore() {
        return bonusScore;
    }

    public void setBonusScore(BigDecimal bonusScore) {
        this.bonusScore = bonusScore;
    }

    public BigDecimal getPenaltyScore() {
        return penaltyScore;
    }

    public void setPenaltyScore(BigDecimal penaltyScore) {
        this.penaltyScore = penaltyScore;
    }

    public BigDecimal getUsualWeight() {
        return usualWeight;
    }

    public void setUsualWeight(BigDecimal usualWeight) {
        this.usualWeight = usualWeight;
    }

    public BigDecimal getAttendanceWeight() {
        return attendanceWeight;
    }

    public void setAttendanceWeight(BigDecimal attendanceWeight) {
        this.attendanceWeight = attendanceWeight;
    }

    public BigDecimal getHomeworkWeight() {
        return homeworkWeight;
    }

    public void setHomeworkWeight(BigDecimal homeworkWeight) {
        this.homeworkWeight = homeworkWeight;
    }

    public BigDecimal getLabWeight() {
        return labWeight;
    }

    public void setLabWeight(BigDecimal labWeight) {
        this.labWeight = labWeight;
    }

    public BigDecimal getExamWeight() {
        return examWeight;
    }

    public void setExamWeight(BigDecimal examWeight) {
        this.examWeight = examWeight;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(BigDecimal gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getPassFlag() {
        return passFlag;
    }

    public void setPassFlag(String passFlag) {
        this.passFlag = passFlag;
    }

    public Integer getRankNo() {
        return rankNo;
    }

    public void setRankNo(Integer rankNo) {
        this.rankNo = rankNo;
    }

    public BigDecimal getPercentile() {
        return percentile;
    }

    public void setPercentile(BigDecimal percentile) {
        this.percentile = percentile;
    }

    public Integer getExamRecordCount() {
        return examRecordCount;
    }

    public void setExamRecordCount(Integer examRecordCount) {
        this.examRecordCount = examRecordCount;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishBy() {
        return publishBy;
    }

    public void setPublishBy(String publishBy) {
        this.publishBy = publishBy;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("scoreId", getScoreId())
                .append("classCourseId", getClassCourseId())
                .append("teachingClassCode", getTeachingClassCode())
                .append("termId", getTermId())
                .append("termName", getTermName())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("studentUserId", getStudentUserId())
                .append("studentName", getStudentName())
                .append("studentNo", getStudentNo())
                .append("assessmentType", getAssessmentType())
                .append("credits", getCredits())
                .append("usualScore", getUsualScore())
                .append("attendanceScore", getAttendanceScore())
                .append("homeworkScore", getHomeworkScore())
                .append("labScore", getLabScore())
                .append("examScore", getExamScore())
                .append("examAvgScore", getExamAvgScore())
                .append("bonusScore", getBonusScore())
                .append("penaltyScore", getPenaltyScore())
                .append("usualWeight", getUsualWeight())
                .append("attendanceWeight", getAttendanceWeight())
                .append("homeworkWeight", getHomeworkWeight())
                .append("labWeight", getLabWeight())
                .append("examWeight", getExamWeight())
                .append("totalScore", getTotalScore())
                .append("gradePoint", getGradePoint())
                .append("gradeLevel", getGradeLevel())
                .append("passFlag", getPassFlag())
                .append("rankNo", getRankNo())
                .append("percentile", getPercentile())
                .append("examRecordCount", getExamRecordCount())
                .append("syncTime", getSyncTime())
                .append("publishStatus", getPublishStatus())
                .append("publishTime", getPublishTime())
                .append("publishBy", getPublishBy())
                .append("teacherComment", getTeacherComment())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
