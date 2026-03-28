package com.smart.system.domain.dto;

import jakarta.validation.constraints.NotNull;

public class CourseSelectionOperateDto
{
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "教学班ID不能为空")
    private Long classCourseId;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getClassCourseId()
    {
        return classCourseId;
    }

    public void setClassCourseId(Long classCourseId)
    {
        this.classCourseId = classCourseId;
    }
}
