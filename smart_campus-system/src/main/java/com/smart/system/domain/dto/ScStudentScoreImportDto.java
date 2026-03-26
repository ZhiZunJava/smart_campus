package com.smart.system.domain.dto;

import java.math.BigDecimal;
import com.smart.common.annotation.Excel;

/**
 * 课程成绩导入 DTO
 */
public class ScStudentScoreImportDto {
    @Excel(name = "学生用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long studentUserId;

    @Excel(name = "学号")
    private String studentNo;

    @Excel(name = "学生姓名")
    private String studentName;

    @Excel(name = "平时分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal usualScore;

    @Excel(name = "考勤分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal attendanceScore;

    @Excel(name = "作业分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal homeworkScore;

    @Excel(name = "实验分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal labScore;

    @Excel(name = "考试分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal examScore;

    @Excel(name = "加分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal bonusScore;

    @Excel(name = "扣分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal penaltyScore;

    @Excel(name = "教师评语")
    private String teacherComment;

    @Excel(name = "备注")
    private String remark;

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
