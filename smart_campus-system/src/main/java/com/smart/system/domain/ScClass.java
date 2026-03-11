package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 班级对象 sc_class
 *
 * @author Codex
 */
public class ScClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long classId;

    private Long gradeId;

    private String className;

    private Long headTeacherId;

    private String status;

    public Long getClassId()
    {
        return classId;
    }

    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Long gradeId)
    {
        this.gradeId = gradeId;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public Long getHeadTeacherId()
    {
        return headTeacherId;
    }

    public void setHeadTeacherId(Long headTeacherId)
    {
        this.headTeacherId = headTeacherId;
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
            .append("classId", getClassId())
            .append("gradeId", getGradeId())
            .append("className", getClassName())
            .append("headTeacherId", getHeadTeacherId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
