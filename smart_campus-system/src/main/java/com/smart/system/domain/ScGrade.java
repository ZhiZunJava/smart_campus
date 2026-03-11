package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 年级对象 sc_grade
 *
 * @author Codex
 */
public class ScGrade extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long gradeId;

    private String gradeName;

    private String schoolYear;

    private String status;

    public Long getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Long gradeId)
    {
        this.gradeId = gradeId;
    }

    public String getGradeName()
    {
        return gradeName;
    }

    public void setGradeName(String gradeName)
    {
        this.gradeName = gradeName;
    }

    public String getSchoolYear()
    {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear)
    {
        this.schoolYear = schoolYear;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("gradeId", getGradeId())
            .append("gradeName", getGradeName())
            .append("schoolYear", getSchoolYear())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
