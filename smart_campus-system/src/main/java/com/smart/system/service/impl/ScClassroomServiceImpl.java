package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.mapper.ScClassroomMapper;
import com.smart.system.mapper.ScCourseScheduleMapper;
import com.smart.system.service.IScClassroomService;

@Service
public class ScClassroomServiceImpl implements IScClassroomService {
    @Autowired
    private ScClassroomMapper scClassroomMapper;
    @Autowired
    private ScCourseScheduleMapper scCourseScheduleMapper;

    public ScClassroom selectScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.selectScClassroomByClassroomId(classroomId);
    }

    public List<ScClassroom> selectScClassroomList(ScClassroom scClassroom) {
        return scClassroomMapper.selectScClassroomList(scClassroom);
    }

    public int insertScClassroom(ScClassroom scClassroom) {
        return scClassroomMapper.insertScClassroom(scClassroom);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateScClassroom(ScClassroom scClassroom) {
        int rows = scClassroomMapper.updateScClassroom(scClassroom);
        syncScheduleSnapshots(scClassroom);
        return rows;
    }

    public int deleteScClassroomByClassroomIds(Long[] classroomIds) {
        return scClassroomMapper.deleteScClassroomByClassroomIds(classroomIds);
    }

    public int deleteScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.deleteScClassroomByClassroomId(classroomId);
    }

    private void syncScheduleSnapshots(ScClassroom scClassroom) {
        if (scClassroom == null || scClassroom.getClassroomId() == null) {
            return;
        }
        ScClassroom latest = scClassroomMapper.selectScClassroomByClassroomId(scClassroom.getClassroomId());
        if (latest == null) {
            return;
        }
        ScCourseSchedule schedule = new ScCourseSchedule();
        schedule.setClassroomId(latest.getClassroomId());
        schedule.setClassroom(latest.getClassroomName());
        schedule.setClassroomName(latest.getClassroomName());
        schedule.setBuildingName(latest.getBuildingName());
        schedule.setCampusName(latest.getCampusName());
        schedule.setUpdateBy(scClassroom.getUpdateBy());
        scCourseScheduleMapper.syncClassroomSnapshotByClassroomId(schedule);
    }
}
