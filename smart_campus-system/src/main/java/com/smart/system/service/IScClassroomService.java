package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScClassroom;

public interface IScClassroomService {
    ScClassroom selectScClassroomByClassroomId(Long classroomId);

    List<ScClassroom> selectScClassroomList(ScClassroom scClassroom);

    int insertScClassroom(ScClassroom scClassroom);

    int updateScClassroom(ScClassroom scClassroom);

    int deleteScClassroomByClassroomIds(Long[] classroomIds);

    int deleteScClassroomByClassroomId(Long classroomId);

    Map<String, Object> importScClassroom(List<ScClassroom> classroomList, boolean updateSupport, String operator);
}
