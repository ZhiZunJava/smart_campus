package com.smart.web.controller.campus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.campusvo.TeachingAiAssistVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.TeachingAiAssistDto;
import com.smart.system.service.ai.IAiGatewayService;

@RestController
@RequestMapping("/campus/ai/teaching")
public class ScTeachingAiController extends BaseController {
    private static final Map<String, ModuleConfig> MODULE_CONFIGS = buildModuleConfigs();

    @Autowired
    private IAiGatewayService aiGatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("@ss.hasAnyPermi('campus:grade:edit,campus:class:edit,campus:course:edit,campus:courseChapter:edit,campus:courseStudent:edit,campus:knowledgePoint:edit')")
    @PostMapping("/assist")
    public AjaxResult assist(@RequestBody TeachingAiAssistDto assistDto) {
        ModuleConfig moduleConfig = MODULE_CONFIGS.get(assistDto == null ? null : assistDto.getModuleKey());
        if (moduleConfig == null) {
            throw new ServiceException("不支持的教学模块");
        }

        String actionType = StringUtils.isEmpty(assistDto.getActionType()) ? "generate"
                : assistDto.getActionType().trim().toLowerCase();
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setBizType("teaching_admin");
        requestDto.setSystemPrompt(buildSystemPrompt(moduleConfig));
        requestDto.setUserPrompt(buildUserPrompt(moduleConfig, actionType, assistDto));
        requestDto.setStream(Boolean.FALSE);
        requestDto.setDeepThinking(Boolean.FALSE);

        AiChatResponseVo aiResponse = aiGatewayService.chat(requestDto);
        TeachingAiAssistVo vo = parseResponse(moduleConfig, actionType,
                aiResponse == null ? "" : aiResponse.getContent());
        vo.setRawContent(aiResponse == null ? "" : aiResponse.getContent());
        return success(vo);
    }

    private String buildSystemPrompt(ModuleConfig moduleConfig) {
        return "你是智慧校园后台的教学基础配置助手，负责帮助管理员生成和优化"
                + moduleConfig.moduleLabel
                + "的表单配置。"
                + "你必须只返回 JSON，不允许返回 markdown，不允许解释。"
                + "JSON 结构固定为："
                + "{\"assistantTitle\":\"...\",\"formDraft\":{},\"highlights\":[\"...\"],\"warnings\":[\"...\"],\"tips\":[\"...\"]}。"
                + "formDraft 只允许包含这些字段："
                + String.join(",", moduleConfig.fieldKeys)
                + "。"
                + "如果某个字段没有把握，可以不返回该字段。"
                + "状态字段 status 默认使用 0 表示正常、1 表示停用。"
                + "回答要适合中国校园教务场景，配置清晰、易维护、能直接落地。";
    }

    private String buildUserPrompt(ModuleConfig moduleConfig, String actionType, TeachingAiAssistDto assistDto) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("moduleKey", moduleConfig.moduleKey);
        payload.put("moduleLabel", moduleConfig.moduleLabel);
        payload.put("actionType", actionType);
        payload.put("actionLabel", "optimize".equals(actionType) ? "优化已有配置" : "生成新配置");
        payload.put("userInstruction",
                StringUtils.isEmpty(assistDto.getUserInstruction()) ? "" : assistDto.getUserInstruction().trim());
        payload.put("fieldDescriptions", moduleConfig.fieldDescriptions);
        payload.put("currentForm",
                assistDto.getCurrentForm() == null ? Collections.emptyMap() : assistDto.getCurrentForm());
        payload.put("selectedRows",
                assistDto.getSelectedRows() == null ? Collections.emptyList() : assistDto.getSelectedRows());
        payload.put("availableOptions",
                assistDto.getAvailableOptions() == null ? Collections.emptyMap() : assistDto.getAvailableOptions());
        payload.put("goal", "请返回适合直接回填表单的 formDraft，并补充 highlights、warnings、tips。");
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new ServiceException("AI 请求组装失败");
        }
    }

    private TeachingAiAssistVo parseResponse(ModuleConfig moduleConfig, String actionType, String rawContent) {
        TeachingAiAssistVo vo = new TeachingAiAssistVo();
        vo.setModuleKey(moduleConfig.moduleKey);
        vo.setActionType(actionType);
        vo.setAssistantTitle(moduleConfig.moduleLabel + ("optimize".equals(actionType) ? "优化建议" : "智能配置建议"));
        vo.setFormDraft(new LinkedHashMap<>());
        vo.setHighlights(new ArrayList<>());
        vo.setWarnings(new ArrayList<>());
        vo.setTips(new ArrayList<>());

        if (StringUtils.isEmpty(rawContent)) {
            return vo;
        }

        try {
            String normalizedJson = extractJsonObject(rawContent);
            JsonNode root = objectMapper.readTree(normalizedJson);
            if (root.has("assistantTitle")) {
                vo.setAssistantTitle(root.path("assistantTitle").asText(vo.getAssistantTitle()));
            }
            if (root.has("formDraft") && root.path("formDraft").isObject()) {
                Map<String, Object> draft = objectMapper.convertValue(root.path("formDraft"), Map.class);
                Map<String, Object> sanitizedDraft = new LinkedHashMap<>();
                for (String fieldKey : moduleConfig.fieldKeys) {
                    if (draft.containsKey(fieldKey) && draft.get(fieldKey) != null) {
                        sanitizedDraft.put(fieldKey, draft.get(fieldKey));
                    }
                }
                vo.setFormDraft(sanitizedDraft);
            }
            vo.setHighlights(readStringList(root.path("highlights")));
            vo.setWarnings(readStringList(root.path("warnings")));
            vo.setTips(readStringList(root.path("tips")));
            return vo;
        } catch (Exception ignored) {
            vo.setHighlights(Collections.singletonList(rawContent));
            return vo;
        }
    }

    private List<String> readStringList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (item != null && StringUtils.isNotEmpty(item.asText())) {
                result.add(item.asText());
            }
        }
        return result;
    }

    private String extractJsonObject(String rawContent) {
        String normalized = rawContent.trim();
        if (normalized.startsWith("```")) {
            int firstBrace = normalized.indexOf('{');
            int lastBrace = normalized.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                return normalized.substring(firstBrace, lastBrace + 1);
            }
        }
        int firstBrace = normalized.indexOf('{');
        int lastBrace = normalized.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return normalized.substring(firstBrace, lastBrace + 1);
        }
        return normalized;
    }

    private static Map<String, ModuleConfig> buildModuleConfigs() {
        Map<String, ModuleConfig> configs = new LinkedHashMap<>();
        configs.put("grade", new ModuleConfig("grade", "年级管理",
                Arrays.asList("gradeName", "schoolYear", "status", "remark"),
                Arrays.asList("gradeName: 年级名称，如高一、高二", "schoolYear: 学年，如2025-2026", "status: 0正常 1停用",
                        "remark: 补充说明")));
        configs.put("class", new ModuleConfig("class", "班级管理",
                Arrays.asList("gradeId", "className", "headTeacherId", "status", "remark"),
                Arrays.asList("gradeId: 所属年级ID", "className: 班级名称", "headTeacherId: 班主任用户ID", "status: 0正常 1停用",
                        "remark: 班级备注")));
        configs.put("course", new ModuleConfig("course", "课程管理",
                Arrays.asList("courseName", "courseCode", "subjectType", "gradeId", "intro", "status"),
                Arrays.asList("courseName: 课程名称", "courseCode: 课程编码", "subjectType: 学科类型", "gradeId: 年级ID",
                        "intro: 课程简介", "status: 0正常 1停用")));
        configs.put("courseChapter", new ModuleConfig("courseChapter", "课程章节",
                Arrays.asList("courseId", "parentId", "chapterName", "chapterOrder", "status", "remark"),
                Arrays.asList("courseId: 课程ID", "parentId: 父章节ID，顶级一般为0", "chapterName: 章节名称", "chapterOrder: 章节排序",
                        "status: 0正常 1停用", "remark: 章节说明")));
        configs.put("courseStudent", new ModuleConfig("courseStudent", "选课关系",
                Arrays.asList("classCourseId", "studentUserId", "status", "remark"),
                Arrays.asList("classCourseId: 班级课程ID/教学班ID", "studentUserId: 学生用户ID", "status: 0正常 1停用",
                        "remark: 选课备注")));
        configs.put("knowledgePoint", new ModuleConfig("knowledgePoint", "知识点管理",
                Arrays.asList("courseId", "parentId", "knowledgeName", "difficultyLevel", "keyword", "description",
                        "status", "remark"),
                Arrays.asList("courseId: 课程ID", "parentId: 父级知识点ID，顶级一般为0", "knowledgeName: 知识点名称",
                        "difficultyLevel: 1-5", "keyword: 检索关键词", "description: 知识点描述", "status: 0正常 1停用",
                        "remark: 备注")));
        configs.put("schoolTerm", new ModuleConfig("schoolTerm", "学年学期",
                Arrays.asList("termName", "schoolYear", "termCode", "sortOrder", "isCurrent", "status", "remark"),
                Arrays.asList("termName: 学期名称", "schoolYear: 学年，如2025-2026", "termCode: 学期编码，如2025-2026-1",
                        "sortOrder: 排序", "isCurrent: 1当前学期 0非当前", "status: 0正常 1停用", "remark: 备注")));
        configs.put("classCourse", new ModuleConfig("classCourse", "班级课程",
                Arrays.asList("classId", "courseId", "teacherId", "termId", "weeklyHours", "studentLimit", "status",
                        "remark"),
                Arrays.asList("classId: 班级ID", "courseId: 课程ID", "teacherId: 任课教师ID", "termId: 学期ID",
                        "weeklyHours: 周学时", "studentLimit: 人数上限，建议不低于班级实有人数", "status: 0正常 1停用", "remark: 备注")));
        configs.put("courseSchedule", new ModuleConfig("courseSchedule", "排课表",
                Arrays.asList("termId", "classCourseId", "weekDay", "startSection", "endSection", "classroom",
                        "weeksText", "status", "remark"),
                Arrays.asList("termId: 学期ID", "classCourseId: 班级课程ID", "weekDay: 星期1-7", "startSection: 开始节次",
                        "endSection: 结束节次", "classroom: 教室", "weeksText: 周次文本，由周次多选自动生成，如1-4,6,8-16周",
                        "status: 0正常 1停用", "remark: 备注")));
        return configs;
    }

    private static class ModuleConfig {
        private final String moduleKey;
        private final String moduleLabel;
        private final List<String> fieldKeys;
        private final List<String> fieldDescriptions;

        private ModuleConfig(String moduleKey, String moduleLabel, List<String> fieldKeys,
                List<String> fieldDescriptions) {
            this.moduleKey = moduleKey;
            this.moduleLabel = moduleLabel;
            this.fieldKeys = fieldKeys;
            this.fieldDescriptions = fieldDescriptions;
        }
    }
}
