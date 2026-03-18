package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScClassroom;

public interface ScClassroomMapper {
    ScClassroom selectScClassroomByClassroomId(Long classroomId);

    List<ScClassroom> selectScClassroomList(ScClassroom scClassroom);

    int insertScClassroom(ScClassroom scClassroom);

    int updateScClassroom(ScClassroom scClassroom);

    int deleteScClassroomByClassroomId(Long classroomId);

    int deleteScClassroomByClassroomIds(Long[] classroomIds);
}
