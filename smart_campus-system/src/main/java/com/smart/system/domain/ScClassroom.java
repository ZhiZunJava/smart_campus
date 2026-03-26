package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.annotation.Excel;
import com.smart.common.core.domain.BaseEntity;

public class ScClassroom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long classroomId;
    @Excel(name = "教室名称", sort = 10, prompt = "必填，例如 512 / 机房A / 报告厅")
    private String classroomName;
    @Excel(name = "教学楼", sort = 20, dictType = "campus_building_type", comboReadDict = true, prompt = "建议填写系统中的教学楼字典值")
    private String buildingName;
    private Long deptId;
    @Excel(name = "所属部门", sort = 30, prompt = "选填，请填写系统中的部门名称")
    private String deptName;
    @Excel(name = "校区", sort = 40, dictType = "campus_area_type", comboReadDict = true, prompt = "选填，可从下拉中选择")
    private String campusName;
    @Excel(name = "容量", sort = 50, cellType = Excel.ColumnType.NUMERIC, prompt = "选填，填写大于等于 0 的整数")
    private Integer capacity;
    @Excel(name = "类型", sort = 60, dictType = "classroom_room_type", comboReadDict = true, prompt = "选填，可从下拉中选择")
    private String roomType;
    @Excel(name = "排序", sort = 70, cellType = Excel.ColumnType.NUMERIC, prompt = "选填，默认 0")
    private Integer sortOrder;
    @Excel(name = "状态", sort = 80, dictType = "sys_normal_disable", comboReadDict = true, defaultValue = "正常", prompt = "可选 正常 / 停用")
    private String status;
    @Excel(name = "备注", sort = 90, prompt = "选填")
    private String remark;

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("classroomId", getClassroomId())
                .append("classroomName", getClassroomName())
                .append("buildingName", getBuildingName())
                .append("deptId", getDeptId())
                .append("deptName", getDeptName())
                .append("campusName", getCampusName())
                .append("capacity", getCapacity())
                .append("roomType", getRoomType())
                .append("sortOrder", getSortOrder())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
