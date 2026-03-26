package com.smart.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.core.domain.entity.SysDept;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassroom;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.mapper.ScClassroomMapper;
import com.smart.system.mapper.ScCourseScheduleMapper;
import com.smart.system.service.IScClassroomService;
import com.smart.system.service.ISysDeptService;

@Service
public class ScClassroomServiceImpl implements IScClassroomService {
    private static final Logger log = LoggerFactory.getLogger(ScClassroomServiceImpl.class);

    @Autowired
    private ScClassroomMapper scClassroomMapper;
    @Autowired
    private ScCourseScheduleMapper scCourseScheduleMapper;
    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public ScClassroom selectScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.selectScClassroomByClassroomId(classroomId);
    }

    @Override
    public List<ScClassroom> selectScClassroomList(ScClassroom scClassroom) {
        return scClassroomMapper.selectScClassroomList(scClassroom);
    }

    @Override
    public int insertScClassroom(ScClassroom scClassroom) {
        return scClassroomMapper.insertScClassroom(scClassroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateScClassroom(ScClassroom scClassroom) {
        int rows = scClassroomMapper.updateScClassroom(scClassroom);
        syncScheduleSnapshots(scClassroom);
        return rows;
    }

    @Override
    public int deleteScClassroomByClassroomIds(Long[] classroomIds) {
        return scClassroomMapper.deleteScClassroomByClassroomIds(classroomIds);
    }

    @Override
    public int deleteScClassroomByClassroomId(Long classroomId) {
        return scClassroomMapper.deleteScClassroomByClassroomId(classroomId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importScClassroom(List<ScClassroom> classroomList, boolean updateSupport, String operator) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        int insertCount = 0;
        int updateCount = 0;
        if (classroomList == null || classroomList.isEmpty()) {
            errors.add("导入数据不能为空");
            result.put("successCount", 0);
            result.put("insertCount", 0);
            result.put("updateCount", 0);
            result.put("skipCount", 1);
            result.put("errors", errors);
            return result;
        }

        Map<String, List<SysDept>> deptNameIndex = buildDeptNameIndex();
        Map<String, List<ScClassroom>> classroomIndex = buildClassroomIndex();

        for (int i = 0; i < classroomList.size(); i++) {
            ScClassroom item = classroomList.get(i);
            int rowNumber = i + 2;
            if (isImportRowEmpty(item)) {
                continue;
            }
            try {
                normalizeImportRow(item);
                applyDefaults(item);
                applyDept(item, deptNameIndex);
                validateImportRow(item);

                String uniqueKey = buildUniqueKey(item);
                List<ScClassroom> existingList = classroomIndex.getOrDefault(uniqueKey, Collections.emptyList());
                if (!existingList.isEmpty()) {
                    if (existingList.size() > 1) {
                        throw new ServiceException("系统中存在多条相同位置的教室，请先清理重复数据后再导入");
                    }
                    if (!updateSupport) {
                        errors.add("第" + rowNumber + "行：教室【" + buildDisplayName(item) + "】已存在");
                        continue;
                    }
                    ScClassroom existing = existingList.get(0);
                    item.setClassroomId(existing.getClassroomId());
                    item.setUpdateBy(operator);
                    updateScClassroom(item);
                    updateCount++;
                    classroomIndex.put(uniqueKey, new ArrayList<>(Collections.singletonList(item)));
                    continue;
                }

                item.setCreateBy(operator);
                insertScClassroom(item);
                insertCount++;
                classroomIndex.put(uniqueKey, new ArrayList<>(Collections.singletonList(item)));
            } catch (Exception ex) {
                String message = ex.getMessage();
                errors.add("第" + rowNumber + "行：" + (StringUtils.isEmpty(message) ? "导入失败" : message));
                log.error("导入教室数据失败，row=" + rowNumber, ex);
            }
        }

        result.put("successCount", insertCount + updateCount);
        result.put("insertCount", insertCount);
        result.put("updateCount", updateCount);
        result.put("skipCount", errors.size());
        result.put("errors", errors);
        return result;
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

    private Map<String, List<SysDept>> buildDeptNameIndex() {
        SysDept query = new SysDept();
        List<SysDept> deptList = sysDeptService.selectDeptList(query);
        return deptList.stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getDeptName()))
                .collect(Collectors.groupingBy(item -> normalizeText(item.getDeptName()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<ScClassroom>> buildClassroomIndex() {
        List<ScClassroom> allClassrooms = scClassroomMapper.selectScClassroomList(new ScClassroom());
        return allClassrooms.stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getClassroomName()))
                .collect(Collectors.groupingBy(this::buildUniqueKey, LinkedHashMap::new, Collectors.toList()));
    }

    private void applyDept(ScClassroom item, Map<String, List<SysDept>> deptNameIndex) {
        if (StringUtils.isEmpty(item.getDeptName())) {
            item.setDeptName(null);
            item.setDeptId(null);
            return;
        }
        List<SysDept> matched = deptNameIndex.get(normalizeText(item.getDeptName()));
        if (matched == null || matched.isEmpty()) {
            throw new ServiceException("所属部门不存在【" + item.getDeptName() + "】");
        }
        if (matched.size() > 1) {
            throw new ServiceException("所属部门名称重复【" + item.getDeptName() + "】，请改为系统内唯一名称后再导入");
        }
        item.setDeptId(matched.get(0).getDeptId());
        item.setDeptName(matched.get(0).getDeptName());
    }

    private void validateImportRow(ScClassroom item) {
        if (StringUtils.isEmpty(item.getClassroomName())) {
            throw new ServiceException("教室名称不能为空");
        }
        if (item.getCapacity() != null && item.getCapacity() < 0) {
            throw new ServiceException("容量不能小于 0");
        }
        if (item.getSortOrder() != null && item.getSortOrder() < 0) {
            throw new ServiceException("排序不能小于 0");
        }
        if (StringUtils.isNotEmpty(item.getStatus())
                && !"0".equals(item.getStatus())
                && !"1".equals(item.getStatus())) {
            throw new ServiceException("状态仅支持“正常”或“停用”");
        }
    }

    private void applyDefaults(ScClassroom item) {
        if (item.getCapacity() == null) {
            item.setCapacity(0);
        }
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
        }
        if (StringUtils.isEmpty(item.getStatus())) {
            item.setStatus("0");
        }
    }

    private void normalizeImportRow(ScClassroom item) {
        item.setClassroomName(cleanText(item.getClassroomName()));
        item.setBuildingName(cleanText(item.getBuildingName()));
        item.setDeptName(cleanText(item.getDeptName()));
        item.setCampusName(cleanText(item.getCampusName()));
        item.setRoomType(cleanText(item.getRoomType()));
        item.setRemark(cleanText(item.getRemark()));
    }

    private boolean isImportRowEmpty(ScClassroom item) {
        return item == null
                || (StringUtils.isEmpty(item.getClassroomName())
                && StringUtils.isEmpty(item.getBuildingName())
                && StringUtils.isEmpty(item.getDeptName())
                && StringUtils.isEmpty(item.getCampusName())
                && item.getCapacity() == null
                && StringUtils.isEmpty(item.getRoomType())
                && item.getSortOrder() == null
                && StringUtils.isEmpty(item.getStatus())
                && StringUtils.isEmpty(item.getRemark()));
    }

    private String buildUniqueKey(ScClassroom item) {
        return buildUniqueKey(item == null ? null : item.getCampusName(),
                item == null ? null : item.getBuildingName(),
                item == null ? null : item.getClassroomName());
    }

    private String buildUniqueKey(String campusName, String buildingName, String classroomName) {
        return normalizeText(campusName) + "|" + normalizeText(buildingName) + "|" + normalizeText(classroomName);
    }

    private String buildDisplayName(ScClassroom item) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotEmpty(item.getCampusName())) {
            parts.add(item.getCampusName());
        }
        if (StringUtils.isNotEmpty(item.getBuildingName())) {
            parts.add(item.getBuildingName());
        }
        if (StringUtils.isNotEmpty(item.getClassroomName())) {
            parts.add(item.getClassroomName());
        }
        return parts.isEmpty() ? "未命名教室" : String.join(" / ", parts);
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        return text.isEmpty() ? null : text;
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
