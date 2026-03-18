package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScClassroom;
import com.smart.system.mapper.ScClassroomMapper;
import com.smart.system.service.IScClassroomService;

@Service
public class ScClassroomServiceImpl implements IScClassroomService {
    @Autowired
    private ScClassroomMapper scClassroomMapper;

    public ScClassroom selectScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.selectScClassroomByClassroomId(classroomId);
    }

    public List<ScClassroom> selectScClassroomList(ScClassroom scClassroom) {
        return scClassroomMapper.selectScClassroomList(scClassroom);
    }

    public int insertScClassroom(ScClassroom scClassroom) {
        return scClassroomMapper.insertScClassroom(scClassroom);
    }

    public int updateScClassroom(ScClassroom scClassroom) {
        return scClassroomMapper.updateScClassroom(scClassroom);
    }

    public int deleteScClassroomByClassroomIds(Long[] classroomIds) {
        return scClassroomMapper.deleteScClassroomByClassroomIds(classroomIds);
    }

    public int deleteScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.deleteScClassroomByClassroomId(classroomId);
    }
}
