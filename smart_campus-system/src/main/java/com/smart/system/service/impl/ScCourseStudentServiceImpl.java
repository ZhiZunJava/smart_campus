package com.smart.system.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.CourseStudentBatchAddDto;
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

    public Map<String, Object> batchAddCourseStudents(CourseStudentBatchAddDto batchDto) {
        if (batchDto == null || batchDto.getCourseId() == null || batchDto.getStudentUserIds() == null
                || batchDto.getStudentUserIds().length == 0) {
            throw new ServiceException("请先选择课程和学生");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> skipped = new ArrayList<>();
        int successCount = 0;
        for (Long studentUserId : Arrays.stream(batchDto.getStudentUserIds()).filter(Objects::nonNull).toList()) {
            ScCourseStudent item = new ScCourseStudent();
            item.setCourseId(batchDto.getCourseId());
            item.setStudentUserId(studentUserId);
            item.setClassId(batchDto.getClassId());
            item.setStatus(StringUtils.defaultIfEmpty(batchDto.getStatus(), "0"));
            item.setRemark(batchDto.getRemark());
            fillDefaultClass(item);
            ScCourseStudent duplicate = scCourseStudentMapper.selectDuplicateScCourseStudent(item);
            if (duplicate != null) {
                Map<String, Object> duplicateItem = new LinkedHashMap<>();
                duplicateItem.put("studentUserId", duplicate.getStudentUserId());
                duplicateItem.put("studentName", duplicate.getStudentName());
                duplicateItem.put("studentNo", duplicate.getStudentNo());
                duplicateItem.put("courseName", duplicate.getCourseName());
                duplicateItem.put("className", duplicate.getClassName());
                skipped.add(duplicateItem);
                continue;
            }
            if (item.getJoinTime() == null) {
                item.setJoinTime(DateUtils.getNowDate());
            }
            scCourseStudentMapper.insertScCourseStudent(item);
            successCount++;
        }
        result.put("successCount", successCount);
        result.put("skippedCount", skipped.size());
        result.put("skippedStudents", skipped);
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
