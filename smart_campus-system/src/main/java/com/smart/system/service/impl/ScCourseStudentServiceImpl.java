package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.DateUtils;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.mapper.ScCourseStudentMapper;
import com.smart.system.service.IScCourseStudentService;

@Service
public class ScCourseStudentServiceImpl implements IScCourseStudentService
{
    @Autowired
    private ScCourseStudentMapper scCourseStudentMapper;

    public ScCourseStudent selectScCourseStudentById(Long id) { return scCourseStudentMapper.selectScCourseStudentById(id); }
    public List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent) { return scCourseStudentMapper.selectScCourseStudentList(scCourseStudent); }
    public int insertScCourseStudent(ScCourseStudent scCourseStudent) {
        if (scCourseStudent.getJoinTime() == null) { scCourseStudent.setJoinTime(DateUtils.getNowDate()); }
        return scCourseStudentMapper.insertScCourseStudent(scCourseStudent);
    }
    public int updateScCourseStudent(ScCourseStudent scCourseStudent) { return scCourseStudentMapper.updateScCourseStudent(scCourseStudent); }
    public int deleteScCourseStudentByIds(Long[] ids) { return scCourseStudentMapper.deleteScCourseStudentByIds(ids); }
    public int deleteScCourseStudentById(Long id) { return scCourseStudentMapper.deleteScCourseStudentById(id); }
}
