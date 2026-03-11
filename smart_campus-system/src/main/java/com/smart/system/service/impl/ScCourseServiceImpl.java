package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScCourse;
import com.smart.system.mapper.ScCourseMapper;
import com.smart.system.service.IScCourseService;

@Service
public class ScCourseServiceImpl implements IScCourseService
{
    @Autowired
    private ScCourseMapper scCourseMapper;

    @Override
    public ScCourse selectScCourseByCourseId(Long courseId)
    {
        return scCourseMapper.selectScCourseByCourseId(courseId);
    }

    @Override
    public List<ScCourse> selectScCourseList(ScCourse scCourse)
    {
        return scCourseMapper.selectScCourseList(scCourse);
    }

    @Override
    public int insertScCourse(ScCourse scCourse)
    {
        return scCourseMapper.insertScCourse(scCourse);
    }

    @Override
    public int updateScCourse(ScCourse scCourse)
    {
        return scCourseMapper.updateScCourse(scCourse);
    }

    @Override
    public int deleteScCourseByCourseIds(Long[] courseIds)
    {
        return scCourseMapper.deleteScCourseByCourseIds(courseIds);
    }

    @Override
    public int deleteScCourseByCourseId(Long courseId)
    {
        return scCourseMapper.deleteScCourseByCourseId(courseId);
    }
}
