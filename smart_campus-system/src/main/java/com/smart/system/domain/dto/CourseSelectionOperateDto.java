package com.smart.system.domain.dto;

public class CourseSelectionOperateDto
{
    private Long userId;
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
