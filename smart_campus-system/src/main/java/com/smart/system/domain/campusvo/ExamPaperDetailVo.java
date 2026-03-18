package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 试卷详情 VO
 *
 * @author can
 */
public class ExamPaperDetailVo {
    private Long paperId;
    private String paperName;
    private Long courseId;
    private String paperType;
    private BigDecimal totalScore;
    private Integer durationMinutes;
    private String status;
    private List<ExamPaperQuestionDetailVo> questions;

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExamPaperQuestionDetailVo> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamPaperQuestionDetailVo> questions) {
        this.questions = questions;
    }
}
