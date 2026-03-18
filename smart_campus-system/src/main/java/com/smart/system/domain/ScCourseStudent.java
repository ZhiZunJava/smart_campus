package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 课程学生关系对象 sc_course_student
 *
 * @author can
 */
public class ScCourseStudent extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long courseId;
    private String courseName;
    private Long studentUserId;
    private String studentName;
    private String studentNo;
    private Long classId;
    private String className;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
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
                .append("id", getId())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("studentUserId", getStudentUserId())
                .append("studentName", getStudentName())
                .append("studentNo", getStudentNo())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("joinTime", getJoinTime())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
