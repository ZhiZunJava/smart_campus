package com.smart.system.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.ScLearningTask;
import com.smart.system.domain.ScLearningTaskDispatch;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.mapper.ScCourseStudentMapper;
import com.smart.system.mapper.ScLearningTaskDispatchMapper;
import com.smart.system.mapper.ScLearningTaskMapper;
import com.smart.system.mapper.ScUserProfileMapper;
import com.smart.system.service.IScLearningTaskService;

@Service
public class ScLearningTaskServiceImpl implements IScLearningTaskService {
    @Autowired
    private ScLearningTaskMapper scLearningTaskMapper;

    @Autowired
    private ScLearningTaskDispatchMapper scLearningTaskDispatchMapper;

    @Autowired
    private ScUserProfileMapper scUserProfileMapper;

    @Autowired
    private ScCourseStudentMapper scCourseStudentMapper;

    @Override
    public ScLearningTask selectScLearningTaskByTaskId(Long taskId) {
        return scLearningTaskMapper.selectScLearningTaskByTaskId(taskId);
    }

    @Override
    public List<ScLearningTask> selectScLearningTaskList(ScLearningTask scLearningTask) {
        return scLearningTaskMapper.selectScLearningTaskList(scLearningTask);
    }

    @Override
    public int insertScLearningTask(ScLearningTask scLearningTask) {
        normalizeTask(scLearningTask);
        return scLearningTaskMapper.insertScLearningTask(scLearningTask);
    }

    @Override
    public int updateScLearningTask(ScLearningTask scLearningTask) {
        normalizeTask(scLearningTask);
        return scLearningTaskMapper.updateScLearningTask(scLearningTask);
    }

    @Override
    public int deleteScLearningTaskByTaskIds(Long[] taskIds) {
        return scLearningTaskMapper.deleteScLearningTaskByTaskIds(taskIds);
    }

    @Override
    public int deleteScLearningTaskByTaskId(Long taskId) {
        return scLearningTaskMapper.deleteScLearningTaskByTaskId(taskId);
    }

    @Override
    public int dispatchTask(Long taskId, Long[] userIds, String operator) {
        if (taskId == null || userIds == null || userIds.length == 0) {
            return 0;
        }
        int rows = 0;
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            ScLearningTaskDispatch existing = scLearningTaskDispatchMapper.selectByTaskIdAndUserId(taskId, userId);
            if (existing != null) {
                if (!"0".equals(existing.getDispatchStatus())) {
                    existing.setDispatchStatus("0");
                    existing.setUpdateBy(operator);
                    rows += scLearningTaskDispatchMapper.updateScLearningTaskDispatch(existing);
                }
                continue;
            }
            ScLearningTaskDispatch dispatch = new ScLearningTaskDispatch();
            dispatch.setTaskId(taskId);
            dispatch.setUserId(userId);
            dispatch.setDispatchStatus("0");
            dispatch.setCompletionStatus("PENDING");
            dispatch.setCreateBy(operator);
            dispatch.setCreateTime(new Date());
            rows += scLearningTaskDispatchMapper.insertScLearningTaskDispatch(dispatch);
        }
        return rows;
    }

    @Override
    public int dispatchTaskToClass(Long taskId, Long classId, String operator) {
        if (taskId == null || classId == null) {
            return 0;
        }
        ScUserProfile query = new ScUserProfile();
        query.setClassId(classId);
        List<ScUserProfile> profiles = scUserProfileMapper.selectScUserProfileList(query);
        Long[] userIds = profiles.stream()
                .map(ScUserProfile::getUserId)
                .filter(item -> item != null)
                .distinct()
                .toArray(Long[]::new);
        return dispatchTask(taskId, userIds, operator);
    }

    @Override
    public int dispatchTaskToCourse(Long taskId, Long courseId, String operator) {
        if (taskId == null || courseId == null) {
            return 0;
        }
        ScCourseStudent query = new ScCourseStudent();
        query.setCourseId(courseId);
        List<ScCourseStudent> list = scCourseStudentMapper.selectScCourseStudentList(query);
        Set<Long> userIds = new LinkedHashSet<>();
        for (ScCourseStudent item : list) {
            if (item.getStudentUserId() != null) {
                userIds.add(item.getStudentUserId());
            }
        }
        return dispatchTask(taskId, userIds.toArray(new Long[0]), operator);
    }

    @Override
    public List<Map<String, Object>> selectUserDispatchedTasks(Long userId, Long termId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return scLearningTaskMapper.selectUserDispatchedTasks(userId, termId);
    }

    private void normalizeTask(ScLearningTask task) {
        if (task == null) {
            return;
        }
        if (StringUtils.isEmpty(task.getTaskType())) {
            task.setTaskType("COURSE");
        }
        if (StringUtils.isEmpty(task.getPriorityLevel())) {
            task.setPriorityLevel("NORMAL");
        }
        if (StringUtils.isEmpty(task.getStatus())) {
            task.setStatus("0");
        }
        if (task.getPublishTime() == null) {
            task.setPublishTime(new Date());
        }
    }
}
