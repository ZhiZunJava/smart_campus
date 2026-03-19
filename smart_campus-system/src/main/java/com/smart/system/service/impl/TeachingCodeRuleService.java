package com.smart.system.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourse;
import com.smart.system.mapper.ScClassCourseMapper;
import com.smart.system.service.IScCourseService;

@Service
public class TeachingCodeRuleService
{
    private static final String COURSE_PREFIX = "KC";
    private static final String TEACHING_CLASS_PREFIX = "TB";
    /** 课程编码：KC + 3位流水号，如 KC001 */
    private static final int COURSE_SEQ_WIDTH = 3;
    /** 教学班编码：TB + 4位流水号，如 TB0001 */
    private static final int TEACHING_CLASS_SEQ_WIDTH = 4;

    @Autowired
    private IScCourseService scCourseService;

    @Autowired
    private ScClassCourseMapper scClassCourseMapper;

    public Map<String, Object> generateCourseCode(ScCourse course)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        List<ScCourse> existingCourses = scCourseService.selectScCourseList(new ScCourse());
        int seq = nextSequence(existingCourses.stream()
            .map(ScCourse::getCourseCode)
            .filter(StringUtils::isNotEmpty)
            .filter(code -> code.startsWith(COURSE_PREFIX))
            .collect(Collectors.toList()), COURSE_PREFIX, COURSE_SEQ_WIDTH);

        String generatedCode = COURSE_PREFIX + String.format(Locale.ROOT, "%0" + COURSE_SEQ_WIDTH + "d", seq);
        result.put("courseCode", generatedCode);
        result.put("rule", "KC + 三位流水号");
        result.put("preview", generatedCode);
        return result;
    }

    public Map<String, Object> generateTeachingClassCode(ScClassCourse classCourse)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        List<ScClassCourse> existingClassCourses = scClassCourseMapper.selectScClassCourseList(new ScClassCourse());
        int seq = nextSequence(existingClassCourses.stream()
            .map(ScClassCourse::getTeachingClassCode)
            .filter(StringUtils::isNotEmpty)
            .filter(code -> code.startsWith(TEACHING_CLASS_PREFIX))
            .collect(Collectors.toList()), TEACHING_CLASS_PREFIX, TEACHING_CLASS_SEQ_WIDTH);

        String generatedCode = TEACHING_CLASS_PREFIX + String.format(Locale.ROOT, "%0" + TEACHING_CLASS_SEQ_WIDTH + "d", seq);
        result.put("teachingClassCode", generatedCode);
        result.put("rule", "TB + 四位流水号");
        result.put("preview", generatedCode);
        return result;
    }

    private int nextSequence(List<String> existingCodes, String prefix, int width)
    {
        int max = 0;
        for (String code : existingCodes)
        {
            if (StringUtils.isEmpty(code) || !code.startsWith(prefix))
            {
                continue;
            }
            String suffix = code.substring(prefix.length());
            if (!StringUtils.isNumeric(suffix))
            {
                continue;
            }
            max = Math.max(max, Integer.parseInt(suffix));
        }
        return Math.max(1, max + 1);
    }
}
