package com.smart.system.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.core.domain.entity.SysDept;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScClass;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.mapper.ScClassCourseMapper;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScCourseService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.ISysDeptService;

@Service
public class TeachingCodeRuleService
{
    private static final Pattern NON_ALNUM = Pattern.compile("[^A-Za-z0-9]");

    @Autowired
    private IScCourseService scCourseService;

    @Autowired
    private IScClassService scClassService;

    @Autowired
    private IScSchoolTermService scSchoolTermService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private ScClassCourseMapper scClassCourseMapper;

    public Map<String, Object> generateCourseCode(ScCourse course)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        String gradeSegment = padNumber(course == null ? null : course.getGradeId(), 2, "00");
        String subjectSegment = buildAlphaSegment(
            course == null ? null : StringUtils.defaultIfEmpty(course.getSubjectType(), course.getCourseName()),
            4,
            "GEN");
        String prefix = "KC" + gradeSegment + subjectSegment;

        ScCourse query = new ScCourse();
        List<ScCourse> existingCourses = scCourseService.selectScCourseList(query);
        int seq = nextSequence(existingCourses.stream()
            .map(ScCourse::getCourseCode)
            .filter(StringUtils::isNotEmpty)
            .filter(code -> code.startsWith(prefix))
            .collect(Collectors.toList()), prefix, 3);

        String generatedCode = prefix + String.format(Locale.ROOT, "%03d", seq);
        result.put("courseCode", generatedCode);
        result.put("rule", "KC + 年级两位 + 学科/课程缩写四位 + 三位流水号");
        result.put("preview", generatedCode);
        return result;
    }

    public Map<String, Object> generateTeachingClassCode(ScClassCourse classCourse)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        String termSegment = buildTermSegment(classCourse == null ? null : classCourse.getTermId());
        Long resolvedDeptId = resolveTeachingDeptId(classCourse);
        String deptSegment = padNumber(resolvedDeptId, 3, "000");
        String classSegment = padNumber(classCourse == null ? null : classCourse.getClassId(), 3, "000");
        String courseSegment = padNumber(classCourse == null ? null : classCourse.getCourseId(), 3, "000");
        String majorSegment = buildAlphaSegment(classCourse == null ? null : classCourse.getMajor(), 2, "ZY");
        String prefix = "TB" + termSegment + deptSegment + majorSegment + classSegment + courseSegment;

        ScClassCourse query = new ScClassCourse();
        List<ScClassCourse> existingClassCourses = scClassCourseMapper.selectScClassCourseList(query);
        int seq = nextSequence(existingClassCourses.stream()
            .map(ScClassCourse::getTeachingClassCode)
            .filter(StringUtils::isNotEmpty)
            .filter(code -> code.startsWith(prefix))
            .collect(Collectors.toList()), prefix, 2);

        String generatedCode = prefix + String.format(Locale.ROOT, "%02d", seq);
        result.put("teachingClassCode", generatedCode);
        result.put("rule", "TB + 学期段 + 班级所属部门三位(优先) + 专业缩写两位 + 班级三位 + 课程三位 + 两位流水号");
        result.put("preview", generatedCode);
        result.put("termLabel", resolveTermName(classCourse == null ? null : classCourse.getTermId()));
        result.put("deptLabel", resolveDeptName(resolvedDeptId));
        result.put("classLabel", resolveClassName(classCourse == null ? null : classCourse.getClassId()));
        return result;
    }

    private Long resolveTeachingDeptId(ScClassCourse classCourse)
    {
        if (classCourse == null)
        {
            return null;
        }
        ScClass scClass = classCourse.getClassId() == null ? null : scClassService.selectScClassByClassId(classCourse.getClassId());
        if (scClass != null && scClass.getDeptId() != null)
        {
            return scClass.getDeptId();
        }
        return classCourse.getOpenDeptId();
    }

    private String buildTermSegment(Long termId)
    {
        ScSchoolTerm term = termId == null ? null : scSchoolTermService.selectScSchoolTermByTermId(termId);
        if (term == null || StringUtils.isEmpty(term.getTermCode()))
        {
            return "00000";
        }
        String normalized = NON_ALNUM.matcher(term.getTermCode().toUpperCase(Locale.ROOT)).replaceAll("");
        if (normalized.length() >= 5)
        {
            return normalized.substring(Math.max(0, normalized.length() - 5));
        }
        return StringUtils.leftPad(normalized, 5, "0");
    }

    private String resolveTermName(Long termId)
    {
        ScSchoolTerm term = termId == null ? null : scSchoolTermService.selectScSchoolTermByTermId(termId);
        return term == null ? null : term.getTermName();
    }

    private String resolveDeptName(Long deptId)
    {
        SysDept dept = deptId == null ? null : sysDeptService.selectDeptById(deptId);
        return dept == null ? null : dept.getDeptName();
    }

    private String resolveClassName(Long classId)
    {
        ScClass scClass = classId == null ? null : scClassService.selectScClassByClassId(classId);
        return scClass == null ? null : scClass.getClassName();
    }

    private String buildAlphaSegment(String value, int length, String fallback)
    {
        if (StringUtils.isEmpty(value))
        {
            return fallback;
        }
        String normalized = NON_ALNUM.matcher(value.toUpperCase(Locale.ROOT)).replaceAll("");
        if (StringUtils.isEmpty(normalized))
        {
            String ascii = value.chars()
                .filter(ch -> Character.isLetterOrDigit(ch) && ch < 128)
                .mapToObj(ch -> String.valueOf((char) ch).toUpperCase(Locale.ROOT))
                .collect(Collectors.joining());
            normalized = StringUtils.defaultIfEmpty(ascii, fallback);
        }
        if (normalized.length() >= length)
        {
            return normalized.substring(0, length);
        }
        return StringUtils.rightPad(normalized, length, "X");
    }

    private String padNumber(Long value, int length, String fallback)
    {
        if (value == null)
        {
            return fallback;
        }
        String raw = String.valueOf(value);
        if (raw.length() >= length)
        {
            return raw.substring(raw.length() - length);
        }
        return StringUtils.leftPad(raw, length, "0");
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
