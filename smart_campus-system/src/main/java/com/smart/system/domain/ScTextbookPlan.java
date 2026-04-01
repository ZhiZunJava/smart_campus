package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScTextbookPlan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long planId;
    private Long termId;
    private Long textbookId;
    private Long courseId;
    private Long gradeId;
    private Long classId;
    private Integer planQuantity;
    private Integer distributedQuantity;
    private String planStatus;

    /* ---------- 联查冗余字段（列表展示用） ---------- */
    private String termName;
    private String textbookName;
    private String courseName;
    private String gradeName;
    private String className;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
    public Long getTextbookId() { return textbookId; }
    public void setTextbookId(Long textbookId) { this.textbookId = textbookId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public Integer getPlanQuantity() { return planQuantity; }
    public void setPlanQuantity(Integer planQuantity) { this.planQuantity = planQuantity; }
    public Integer getDistributedQuantity() { return distributedQuantity; }
    public void setDistributedQuantity(Integer distributedQuantity) { this.distributedQuantity = distributedQuantity; }
    public String getPlanStatus() { return planStatus; }
    public void setPlanStatus(String planStatus) { this.planStatus = planStatus; }
    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }
    public String getTextbookName() { return textbookName; }
    public void setTextbookName(String textbookName) { this.textbookName = textbookName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("planId", getPlanId())
                .append("termId", getTermId())
                .append("textbookId", getTextbookId())
                .append("planStatus", getPlanStatus())
                .toString();
    }
}
