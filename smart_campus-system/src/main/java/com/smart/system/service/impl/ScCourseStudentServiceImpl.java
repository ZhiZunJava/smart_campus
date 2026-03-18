package com.smart.system.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.mapper.ScCourseStudentMapper;
import com.smart.system.service.IScCourseStudentService;
import com.smart.system.service.IScUserProfileService;

@Service
public class ScCourseStudentServiceImpl implements IScCourseStudentService {
    @Autowired
    private ScCourseStudentMapper scCourseStudentMapper;

    @Autowired
    private IScUserProfileService scUserProfileService;

    public ScCourseStudent selectScCourseStudentById(Long id) {
        return scCourseStudentMapper.selectScCourseStudentById(id);
    }

    public List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent) {
        return scCourseStudentMapper.selectScCourseStudentList(scCourseStudent);
    }

    public Map<String, Object> checkDuplicate(ScCourseStudent scCourseStudent) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("duplicate", false);
        if (scCourseStudent == null || scCourseStudent.getCourseId() == null
                || scCourseStudent.getStudentUserId() == null) {
            return result;
        }
        ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(scCourseStudent);
        if (duplicate != null) {
            result.put("duplicate", true);
            result.put("id", duplicate.getId());
            result.put("courseId", duplicate.getCourseId());
            result.put("courseName", duplicate.getCourseName());
            result.put("studentUserId", duplicate.getStudentUserId());
            result.put("studentName", duplicate.getStudentName());
            result.put("studentNo", duplicate.getStudentNo());
            result.put("classId", duplicate.getClassId());
            result.put("className", duplicate.getClassName());
        }
        return result;
    }

    public int insertScCourseStudent(ScCourseStudent scCourseStudent) {
        fillDefaultClass(scCourseStudent);
        validateDuplicate(scCourseStudent);
        if (scCourseStudent.getJoinTime() == null) {
            scCourseStudent.setJoinTime(DateUtils.getNowDate());
        }
        return scCourseStudentMapper.insertScCourseStudent(scCourseStudent);
    }

    public int updateScCourseStudent(ScCourseStudent scCourseStudent) {
        fillDefaultClass(scCourseStudent);
        validateDuplicate(scCourseStudent);
        return scCourseStudentMapper.updateScCourseStudent(scCourseStudent);
    }

    public int deleteScCourseStudentByIds(Long[] ids) {
        return scCourseStudentMapper.deleteScCourseStudentByIds(ids);
    }

    public int deleteScCourseStudentById(Long id) {
        return scCourseStudentMapper.deleteScCourseStudentById(id);
    }

    private void fillDefaultClass(ScCourseStudent scCourseStudent) {
        if (scCourseStudent == null || scCourseStudent.getStudentUserId() == null
                || scCourseStudent.getClassId() != null) {
            return;
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(scCourseStudent.getStudentUserId());
        if (profile != null && profile.getClassId() != null) {
            scCourseStudent.setClassId(profile.getClassId());
        }
    }

    private void validateDuplicate(ScCourseStudent scCourseStudent) {
        ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(scCourseStudent);
        if (duplicate == null) {
            return;
        }
        String studentLabel = StringUtils.defaultIfEmpty(duplicate.getStudentName(),
                String.valueOf(duplicate.getStudentUserId()));
        String courseLabel = StringUtils.defaultIfEmpty(duplicate.getCourseName(),
                String.valueOf(duplicate.getCourseId()));
        throw new ServiceException("选课关系重复：" + studentLabel + " 已经选过课程《" + courseLabel + "》");
    }
}
