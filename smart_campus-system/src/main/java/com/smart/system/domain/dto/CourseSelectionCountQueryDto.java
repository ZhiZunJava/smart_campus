package com.smart.system.domain.dto;

import java.util.List;

public class CourseSelectionCountQueryDto
{
    private Long userId;

    /**
     * 前端按“lessonIds”传递，实际映射到教学班 ID（sc_class_course.id）
     */
    private List<Long> lessonIds;

    /**
     * 兼容已有命名，便于其他调用方直接按教学班 ID 传参
     */
    private List<Long> classCourseIds;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public List<Long> getLessonIds()
    {
        return lessonIds;
    }

    public void setLessonIds(List<Long> lessonIds)
    {
        this.lessonIds = lessonIds;
    }

    public List<Long> getClassCourseIds()
    {
        return classCourseIds;
    }

    public void setClassCourseIds(List<Long> classCourseIds)
    {
        this.classCourseIds = classCourseIds;
    }
}
