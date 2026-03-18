package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 试卷对象 sc_exam_paper
 *
 * @author can
 */
public class ScExamPaper extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long paperId;
    private String paperName;
    private Long courseId;
    private String paperType;
    private BigDecimal totalScore;
    private Integer durationMinutes;
    private String status;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("paperId", getPaperId())
                .append("paperName", getPaperName())
                .append("courseId", getCourseId())
                .append("paperType", getPaperType())
                .append("totalScore", getTotalScore())
                .append("durationMinutes", getDurationMinutes())
                .append("status", getStatus())
                .toString();
    }
}
