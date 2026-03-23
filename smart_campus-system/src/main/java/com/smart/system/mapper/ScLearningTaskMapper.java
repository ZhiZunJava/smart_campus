package com.smart.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.smart.system.domain.ScLearningTask;

public interface ScLearningTaskMapper {
    ScLearningTask selectScLearningTaskByTaskId(Long taskId);
    List<ScLearningTask> selectScLearningTaskList(ScLearningTask scLearningTask);
    int insertScLearningTask(ScLearningTask scLearningTask);
    int updateScLearningTask(ScLearningTask scLearningTask);
    int deleteScLearningTaskByTaskId(Long taskId);
    int deleteScLearningTaskByTaskIds(Long[] taskIds);
    List<Map<String, Object>> selectUserDispatchedTasks(@Param("userId") Long userId, @Param("termId") Long termId);
}
