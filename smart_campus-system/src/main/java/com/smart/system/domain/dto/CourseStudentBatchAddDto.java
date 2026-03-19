package com.smart.system.domain.dto;

public class CourseStudentBatchAddDto
{
    private Long courseId;
    private Long classId;
    private Long[] studentUserIds;
    private String status;
    private String remark;

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public Long getClassId()
    {
        return classId;
    }

    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long[] getStudentUserIds()
    {
        return studentUserIds;
    }

    public void setStudentUserIds(Long[] studentUserIds)
    {
        this.studentUserIds = studentUserIds;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
