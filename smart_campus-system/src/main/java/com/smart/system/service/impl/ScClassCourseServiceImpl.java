package com.smart.system.service.impl;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.mapper.ScClassCourseMapper;
import com.smart.system.mapper.ScCourseScheduleMapper;
import com.smart.system.service.IScClassCourseService;

@Service
public class ScClassCourseServiceImpl implements IScClassCourseService {
    @Autowired
    private ScClassCourseMapper scClassCourseMapper;
    @Autowired
    private ScCourseScheduleMapper scCourseScheduleMapper;

    public ScClassCourse selectScClassCourseById(Long id) {
        ScClassCourse result = scClassCourseMapper.selectScClassCourseById(id);
        enrichLoadMetrics(result);
        return result;
    }

    public List<ScClassCourse> selectScClassCourseList(ScClassCourse scClassCourse) {
        List<ScClassCourse> list = scClassCourseMapper.selectScClassCourseList(scClassCourse);
        list.forEach(this::enrichLoadMetrics);
        return list;
    }

    public int insertScClassCourse(ScClassCourse scClassCourse) {
        normalizeTeachingLoad(scClassCourse);
        validateDuplicate(scClassCourse);
        return scClassCourseMapper.insertScClassCourse(scClassCourse);
    }

    public int updateScClassCourse(ScClassCourse scClassCourse) {
        normalizeTeachingLoad(scClassCourse);
        validateDuplicate(scClassCourse);
        return scClassCourseMapper.updateScClassCourse(scClassCourse);
    }

    public int deleteScClassCourseByIds(Long[] ids) {
        return scClassCourseMapper.deleteScClassCourseByIds(ids);
    }

    public int deleteScClassCourseById(Long id) {
        return scClassCourseMapper.deleteScClassCourseById(id);
    }

    private void normalizeTeachingLoad(ScClassCourse scClassCourse) {
        if (scClassCourse == null) {
            return;
        }
        Integer totalHours = positive(scClassCourse.getTotalHours());
        Integer requiredWeeks = positive(scClassCourse.getRequiredWeeks());
        Integer weeklyHours = positive(scClassCourse.getWeeklyHours());

        if (totalHours != null && weeklyHours != null) {
            scClassCourse.setRequiredWeeks(Math.max(1, (int) Math.ceil(totalHours * 1D / weeklyHours)));
        } else if (totalHours != null && requiredWeeks != null) {
            scClassCourse.setWeeklyHours(Math.max(1, (int) Math.ceil(totalHours * 1D / requiredWeeks)));
        } else if (weeklyHours != null && requiredWeeks != null) {
            scClassCourse.setTotalHours(weeklyHours * requiredWeeks);
        }
    }

    private void enrichLoadMetrics(ScClassCourse scClassCourse) {
        if (scClassCourse == null) {
            return;
        }
        normalizeTeachingLoad(scClassCourse);
        scClassCourse.setArrangedHours(calculateArrangedHours(scClassCourse));
    }

    private Integer calculateArrangedHours(ScClassCourse scClassCourse) {
        if (scClassCourse == null || scClassCourse.getId() == null) {
            return 0;
        }
        ScCourseSchedule query = new ScCourseSchedule();
        query.setClassCourseId(scClassCourse.getId());
        query.setTermId(scClassCourse.getTermId());
        query.setStatus("0");
        List<ScCourseSchedule> schedules = scCourseScheduleMapper.selectScCourseScheduleList(query);
        int arrangedHours = 0;
        for (ScCourseSchedule item : schedules) {
            int duration = Math.max(1, (item.getEndSection() == null ? item.getStartSection() : item.getEndSection())
                    - (item.getStartSection() == null ? 1 : item.getStartSection()) + 1);
            int weekCount = extractWeeks(item.getWeeksText()).size();
            arrangedHours += duration * (weekCount > 0 ? weekCount : 1);
        }
        return arrangedHours;
    }

    private Integer positive(Integer value) {
        return value == null || value <= 0 ? null : value;
    }

    private void validateDuplicate(ScClassCourse scClassCourse) {
        if (scClassCourse == null || scClassCourse.getClassId() == null || scClassCourse.getCourseId() == null
                || scClassCourse.getTermId() == null) {
            return;
        }
        ScClassCourse query = new ScClassCourse();
        query.setClassId(scClassCourse.getClassId());
        query.setCourseId(scClassCourse.getCourseId());
        query.setTermId(scClassCourse.getTermId());
        List<ScClassCourse> existingList = scClassCourseMapper.selectScClassCourseList(query);
        for (ScClassCourse existing : existingList) {
            if (scClassCourse.getId() != null && scClassCourse.getId().equals(existing.getId())) {
                continue;
            }
            throw new ServiceException("班级课程重复：同一班级、课程、学期已存在绑定关系");
        }
    }

    private Set<Integer> extractWeeks(String weeksText) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        for (String segment : normalizeWeekSegments(weeksText)) {
            weekSet.addAll(expandWeekSegment(segment));
        }
        return weekSet;
    }

    private List<String> normalizeWeekSegments(String weeksText) {
        if (StringUtils.isEmpty(weeksText)) {
            return Collections.emptyList();
        }
        String normalized = weeksText.trim()
                .replace("第", "")
                .replace("周次", "")
                .replace("，", ",")
                .replace("、", ",")
                .replace("；", ",")
                .replace(";", ",")
                .replace("（", "(")
                .replace("）", ")")
                .replace("～", "-")
                .replace("~", "-")
                .replace("至", "-")
                .replace("到", "-");
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replace("周", "");
        if (StringUtils.isEmpty(normalized)) {
            return Collections.emptyList();
        }
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private Set<Integer> expandWeekSegment(String segment) {
        Set<Integer> weekSet = new LinkedHashSet<>();
        if (StringUtils.isEmpty(segment)) {
            return weekSet;
        }
        boolean oddOnly = segment.contains("单");
        boolean evenOnly = segment.contains("双");
        String cleaned = segment
                .replace("(单)", "")
                .replace("(双)", "")
                .replace("单周", "")
                .replace("双周", "")
                .replace("单", "")
                .replace("双", "")
                .replace("(", "")
                .replace(")", "");
        if (StringUtils.isEmpty(cleaned)) {
            return weekSet;
        }
        if (cleaned.contains("-")) {
            String[] range = cleaned.split("-");
            if (range.length == 2 && StringUtils.isNumeric(range[0].trim()) && StringUtils.isNumeric(range[1].trim())) {
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                if (start > end) {
                    int temp = start;
                    start = end;
                    end = temp;
                }
                int step = (oddOnly || evenOnly) ? 2 : 1;
                if (oddOnly && start % 2 == 0) {
                    start++;
                }
                if (evenOnly && start % 2 != 0) {
                    start++;
                }
                for (int week = start; week <= end; week += step) {
                    weekSet.add(week);
                }
            }
            return weekSet;
        }
        if (StringUtils.isNumeric(cleaned)) {
            int week = Integer.parseInt(cleaned);
            if ((!oddOnly && !evenOnly)
                    || (oddOnly && week % 2 != 0)
                    || (evenOnly && week % 2 == 0)) {
                weekSet.add(week);
            }
        }
        return weekSet;
    }
}
