package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScLearningTask;

public interface IScLearningTaskService {
    ScLearningTask selectScLearningTaskByTaskId(Long taskId);
    List<ScLearningTask> selectScLearningTaskList(ScLearningTask scLearningTask);
    int insertScLearningTask(ScLearningTask scLearningTask);
    int updateScLearningTask(ScLearningTask scLearningTask);
    int deleteScLearningTaskByTaskIds(Long[] taskIds);
    int deleteScLearningTaskByTaskId(Long taskId);
    int dispatchTask(Long taskId, Long[] userIds, String operator);
    int dispatchTaskToClass(Long taskId, Long classId, String operator);
    int dispatchTaskToCourse(Long taskId, Long courseId, String operator);
    List<Map<String, Object>> selectUserDispatchedTasks(Long userId, Long termId);
}
