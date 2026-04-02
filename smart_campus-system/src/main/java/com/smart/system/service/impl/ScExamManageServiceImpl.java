package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.domain.ScAiTaskLog;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.domain.ScCourse;
import com.smart.system.domain.ScCourseChapter;
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamPaperQuestion;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.ScQuestionItem;
import com.smart.system.domain.ScQuestionOption;
import com.smart.system.domain.campusvo.AiAsyncTaskSubmitVo;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.campusvo.AiQuestionGenerateResultVo;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.campusvo.ExamPaperQuestionDetailVo;
import com.smart.system.domain.campusvo.QuestionDetailVo;
import com.smart.system.domain.campusvo.QuestionOptionVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiGenerateQuestionDto;
import com.smart.system.domain.dto.ExamPaperAssembleDto;
import com.smart.system.domain.dto.PaperAssembleRuleDto;
import com.smart.system.domain.dto.PaperQuestionConfigDto;
import com.smart.system.domain.dto.PaperUpsertDto;
import com.smart.system.domain.dto.QuestionImportDto;
import com.smart.system.domain.dto.QuestionItemUpsertDto;
import com.smart.system.domain.dto.QuestionOptionEditDto;
import com.smart.system.domain.dto.QuestionUpsertDto;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamPaperQuestionMapper;
import com.smart.system.mapper.ScCourseMapper;
import com.smart.system.mapper.ScCourseChapterMapper;
import com.smart.system.mapper.ScKnowledgePointMapper;
import com.smart.system.mapper.ScQuestionBankMapper;
import com.smart.system.mapper.ScQuestionOptionMapper;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.IScAiPromptTemplateService;
import com.smart.system.service.IScAiTaskLogService;
import com.smart.system.service.IScExamManageService;
import com.smart.system.service.IScQuestionItemService;
import com.smart.system.service.ai.IAiGatewayService;

@Service
public class ScExamManageServiceImpl implements IScExamManageService {
    private static final Logger log = LoggerFactory.getLogger(ScExamManageServiceImpl.class);
    private static final String EXAM_QUESTION_ASYNC_BIZ_TYPE = "exam_question_generate_async";
    private static final Set<String> OBJECTIVE_TYPES = new LinkedHashSet<>();
    private static final Map<String, Map<String, Object>> QUESTION_TYPE_META = new LinkedHashMap<>();

    static {
        registerType("single", "单选题", true, true);
        registerType("multiple", "多选题", true, true);
        registerType("judge", "判断题", true, true);
        registerType("fill", "填空题", true, false);
        registerType("essay", "简答题", false, false);
        registerType("material", "材料题", false, false);
        registerType("case", "案例分析题", false, false);
    }

    @Autowired
    private ScQuestionBankMapper scQuestionBankMapper;

    @Autowired
    private ScQuestionOptionMapper scQuestionOptionMapper;

    @Autowired
    private ScExamPaperMapper scExamPaperMapper;

    @Autowired
    private ScExamPaperQuestionMapper scExamPaperQuestionMapper;

    @Autowired
    private ScCourseMapper scCourseMapper;

    @Autowired
    private ScCourseChapterMapper scCourseChapterMapper;

    @Autowired
    private ScKnowledgePointMapper scKnowledgePointMapper;

    @Autowired
    private IAiGatewayService aiGatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IScAiTaskLogService scAiTaskLogService;

    @Autowired
    private IScAiPromptTemplateService scAiPromptTemplateService;

    @Autowired
    private IScAiModelConfigService scAiModelConfigService;

    @Autowired
    private IScQuestionItemService scQuestionItemService;

    @Override
    public QuestionDetailVo selectQuestionDetail(Long questionId) {
        ScQuestionBank question = scQuestionBankMapper.selectScQuestionBankByQuestionId(questionId);
        if (question == null) {
            return null;
        }
        return buildQuestionDetail(question, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveQuestion(QuestionUpsertDto dto, String operator) {
        normalizeQuestionPayload(dto);
        validateQuestion(dto);
        ScQuestionBank question = buildQuestionEntity(dto);
        question.setCreateBy(operator);
        scQuestionBankMapper.insertScQuestionBank(question);
        saveQuestionOptions(question.getQuestionId(), dto.getOptions());
        syncLegacyQuestionToItem(dto, question.getQuestionId(), operator, false, null);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuestion(QuestionUpsertDto dto, String operator) {
        if (dto == null || dto.getQuestionId() == null) {
            throw new ServiceException("题目ID不能为空");
        }
        normalizeQuestionPayload(dto);
        validateQuestion(dto);
        ScQuestionBank existingQuestion = scQuestionBankMapper.selectScQuestionBankByQuestionId(dto.getQuestionId());
        if (existingQuestion == null) {
            throw new ServiceException("题目不存在");
        }
        QuestionUpsertDto originalSnapshot = buildLegacyQuestionSnapshot(existingQuestion);
        ScQuestionBank question = buildQuestionEntity(dto);
        question.setQuestionId(dto.getQuestionId());
        question.setUpdateBy(operator);
        scQuestionBankMapper.updateScQuestionBank(question);
        scQuestionOptionMapper.deleteScQuestionOptionByQuestionId(dto.getQuestionId());
        saveQuestionOptions(dto.getQuestionId(), dto.getOptions());
        syncLegacyQuestionToItem(dto, dto.getQuestionId(), operator, true, originalSnapshot);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuestionByIds(Long[] questionIds) {
        if (questionIds == null || questionIds.length == 0) {
            return 0;
        }
        for (Long questionId : questionIds) {
            scQuestionOptionMapper.deleteScQuestionOptionByQuestionId(questionId);
        }
        return scQuestionBankMapper.deleteScQuestionBankByQuestionIds(questionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importQuestions(List<QuestionImportDto> rows, boolean updateSupport, String operator) {
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException("导入数据不能为空");
        }
        int successCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            QuestionImportDto row = rows.get(i);
            if (row == null || StringUtils.isEmpty(row.getStem())) {
                continue;
            }
            try {
                ScQuestionBank exist = findQuestionByStem(row.getCourseId(), row.getStem(), row.getQuestionType());
                QuestionUpsertDto dto = convertImportRow(row);
                if (exist != null) {
                    if (!updateSupport) {
                        skipCount++;
                        continue;
                    }
                    dto.setQuestionId(exist.getQuestionId());
                    updateQuestion(dto, operator);
                    updateCount++;
                } else {
                    saveQuestion(dto, operator);
                    successCount++;
                }
            } catch (Exception e) {
                errors.add("第" + (i + 2) + "行：" + e.getMessage());
                skipCount++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("successCount", successCount);
        result.put("updateCount", updateCount);
        result.put("skipCount", skipCount);
        result.put("errors", errors);
        result.put("message", buildImportMessage(successCount, updateCount, skipCount, errors));
        return result;
    }

    @Override
    public ExamPaperDetailVo selectPaperDetail(Long paperId) {
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(paperId);
        if (paper == null) {
            return null;
        }
        return buildPaperDetail(paper, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int savePaper(PaperUpsertDto dto, String operator) {
        validatePaper(dto);
        savePaperTree(dto, null, operator);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePaper(PaperUpsertDto dto, String operator) {
        if (dto == null || dto.getPaperId() == null) {
            throw new ServiceException("试卷ID不能为空");
        }
        validatePaper(dto);
        if (scExamPaperMapper.selectScExamPaperByPaperId(dto.getPaperId()) == null) {
            throw new ServiceException("试卷不存在");
        }
        updatePaperTree(dto, operator);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePaperByIds(Long[] paperIds) {
        if (paperIds == null || paperIds.length == 0) {
            return 0;
        }
        for (Long paperId : paperIds) {
            scExamPaperQuestionMapper.deleteScExamPaperQuestionByPaperId(paperId);
        }
        return scExamPaperMapper.deleteScExamPaperByPaperIds(paperIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamPaperDetailVo assemblePaper(ExamPaperAssembleDto dto, String operator) {
        if (dto == null) {
            throw new ServiceException("组卷参数不能为空");
        }
        if (dto.getRules() == null || dto.getRules().isEmpty()) {
            throw new ServiceException("组卷规则不能为空");
        }
        List<PaperQuestionConfigDto> selectedQuestions = new ArrayList<>();
        Set<Long> usedQuestionIds = new HashSet<>();
        int sortNo = 1;
        for (PaperAssembleRuleDto rule : dto.getRules()) {
            appendQuestionsByRule(dto.getCourseId(), rule, usedQuestionIds, selectedQuestions, sortNo);
            sortNo = selectedQuestions.size() + 1;
        }

        PaperUpsertDto paperDto = new PaperUpsertDto();
        paperDto.setPaperName(StringUtils.defaultIfEmpty(dto.getPaperName(), "智能组卷-" + System.currentTimeMillis()));
        paperDto.setCourseId(dto.getCourseId());
        paperDto.setPaperType(StringUtils.defaultIfEmpty(dto.getPaperType(), "random"));
        paperDto.setDurationMinutes(dto.getDurationMinutes() == null ? 60 : dto.getDurationMinutes());
        paperDto.setPublishStatus(StringUtils.defaultIfEmpty(dto.getPublishStatus(), "draft"));
        paperDto.setStatus(StringUtils.defaultIfEmpty(dto.getStatus(), "0"));
        paperDto.setRemark(dto.getRemark());
        paperDto.setQuestions(selectedQuestions);
        if (!Boolean.FALSE.equals(dto.getSavePaper())) {
            savePaper(paperDto, operator);
            ScExamPaper paper = findLatestPaper(dto.getCourseId(), paperDto.getPaperName());
            if (paper != null) {
                return selectPaperDetail(paper.getPaperId());
            }
        }
        ScExamPaper preview = buildPaperEntity(paperDto);
        preview.setTotalScore(calculatePaperTotalScore(selectedQuestions, paperDto.getTotalScore()));
        return buildPaperDetailFromConfigs(preview, selectedQuestions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiQuestionGenerateResultVo generateQuestionsByAi(AiGenerateQuestionDto dto, String operator) {
        validateAiGenerateRequest(dto);
        Map<String, String> promptContext = buildAiPromptContext(dto);
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(dto.getModelId());
        requestDto.setBizType("exam_question_generate");
        requestDto.setStream(Boolean.FALSE);
        requestDto.setDeepThinking(Boolean.FALSE);
        requestDto.setSystemPrompt(buildAiGenerateSystemPrompt(promptContext));
        requestDto.setUserPrompt(buildAiGenerateUserPrompt(dto, promptContext));
        AiChatResponseVo response = aiGatewayService.chat(requestDto);
        String rawContent = response == null ? "" : response.getContent();
        List<QuestionUpsertDto> generatedQuestions = parseAiQuestions(rawContent, dto);

        List<QuestionDetailVo> detailVos = new ArrayList<>();
        int savedCount = 0;
        for (QuestionUpsertDto item : generatedQuestions) {
            if (Boolean.TRUE.equals(dto.getSaveToBank())) {
                saveQuestion(item, operator);
                ScQuestionBank saved = findQuestionByStem(item.getCourseId(), item.getStem(), item.getQuestionType());
                if (saved != null) {
                    detailVos.add(buildQuestionDetail(saved, true));
                    savedCount++;
                    continue;
                }
            }
            detailVos.add(buildQuestionDetailPreview(item));
        }

        AiQuestionGenerateResultVo vo = new AiQuestionGenerateResultVo();
        vo.setGeneratedCount(detailVos.size());
        vo.setSavedCount(savedCount);
        vo.setTokenUsed(response == null ? 0 : response.getTokenUsed());
        vo.setRawContent(rawContent);
        vo.setQuestions(detailVos);
        return vo;
    }

    @Override
    public AiAsyncTaskSubmitVo submitGenerateQuestionsByAiTask(AiGenerateQuestionDto dto, String operator) {
        validateAiGenerateRequest(dto);
        ScAiModelConfig modelConfig = scAiModelConfigService.resolveModel("chat", "exam_question_generate", dto.getModelId());
        ScAiTaskLog taskLog = new ScAiTaskLog();
        taskLog.setBizType(EXAM_QUESTION_ASYNC_BIZ_TYPE);
        taskLog.setBizId(dto.getCourseId());
        taskLog.setModelId(modelConfig == null ? dto.getModelId() : modelConfig.getModelId());
        taskLog.setTaskStatus("RUNNING");
        taskLog.setRequestPayload(buildAiGenerateTaskPayload(dto, operator));
        taskLog.setResponsePayload(writeJsonQuietly(buildTaskStage("queued", "任务已提交，正在排队准备出题")));
        taskLog.setDurationMs(0);
        scAiTaskLogService.insertScAiTaskLog(taskLog);

        Long taskId = taskLog.getTaskId();
        CompletableFuture.runAsync(() -> executeAiGenerateTask(taskId, dto, operator));

        AiAsyncTaskSubmitVo vo = new AiAsyncTaskSubmitVo();
        vo.setTaskId(taskId);
        vo.setBizType(EXAM_QUESTION_ASYNC_BIZ_TYPE);
        vo.setTaskStatus("RUNNING");
        vo.setMessage("AI 出题任务已提交，可在任务窗口查看进度");
        return vo;
    }

    @Override
    public List<Map<String, Object>> listQuestionTypeMeta() {
        return new ArrayList<>(QUESTION_TYPE_META.values());
    }

    private static void registerType(String code, String label, boolean objective, boolean requiresOptions) {
        OBJECTIVE_TYPES.add(code);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("code", code);
        item.put("label", label);
        item.put("objective", objective);
        item.put("requiresOptions", requiresOptions);
        QUESTION_TYPE_META.put(code, item);
    }

    private void validateAiGenerateRequest(AiGenerateQuestionDto dto) {
        if (dto == null || StringUtils.isEmpty(dto.getQuestionType())) {
            throw new ServiceException("题型不能为空");
        }
        if (dto.getCount() != null && dto.getCount() > 50) {
            throw new ServiceException("单次 AI 出题数量不能超过50题");
        }
    }

    private String questionTypeLabel(String questionType) {
        Map<String, Object> meta = QUESTION_TYPE_META.get(normalizeQuestionType(questionType));
        return meta == null ? questionType : String.valueOf(meta.get("label"));
    }

    private void executeAiGenerateTask(Long taskId, AiGenerateQuestionDto dto, String operator) {
        long startedAt = System.currentTimeMillis();
        updateAiTaskProgress(taskId, "RUNNING", null, buildTaskStage("generating", "AI 正在生成题目并整理结构化结果"),
                null, (int) (System.currentTimeMillis() - startedAt));
        try {
            AiQuestionGenerateResultVo resultVo = generateQuestionsByAi(dto, operator);
            Map<String, Object> payload = buildTaskStage("completed",
                    "AI 出题完成，共生成" + defaultNumber(resultVo.getGeneratedCount()) + "题，已入库"
                            + defaultNumber(resultVo.getSavedCount()) + "题");
            payload.put("result", resultVo);
            updateAiTaskProgress(taskId, "SUCCESS", null, payload, resultVo.getTokenUsed(),
                    (int) (System.currentTimeMillis() - startedAt));
        } catch (Exception e) {
            String message = resolveErrorMessage(e);
            log.warn("AI 异步出题任务执行失败, taskId={}, message={}", taskId, message);
            updateAiTaskProgress(taskId, "FAILED", message,
                    buildTaskStage("failed", "AI 出题失败: " + message), null,
                    (int) (System.currentTimeMillis() - startedAt));
        }
    }

    private void updateAiTaskProgress(Long taskId, String taskStatus, String errorMsg, Map<String, Object> payload,
            Integer tokenUsed, Integer durationMs) {
        ScAiTaskLog update = new ScAiTaskLog();
        update.setTaskId(taskId);
        update.setTaskStatus(taskStatus);
        update.setDurationMs(durationMs);
        if (tokenUsed != null) {
            update.setTokenUsed(tokenUsed);
        }
        if (errorMsg != null) {
            update.setErrorMsg(errorMsg);
        }
        if (payload != null) {
            update.setResponsePayload(writeJsonQuietly(payload));
        }
        scAiTaskLogService.updateScAiTaskLog(update);
    }

    private Map<String, Object> buildTaskStage(String stage, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("stage", stage);
        payload.put("message", message);
        return payload;
    }

    private String buildAiGenerateTaskPayload(AiGenerateQuestionDto dto, String operator) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("taskLabel", "AI异步出题");
        payload.put("courseId", dto.getCourseId());
        payload.put("knowledgePointId", dto.getKnowledgePointId());
        payload.put("modelId", dto.getModelId());
        payload.put("questionType", normalizeQuestionType(dto.getQuestionType()));
        payload.put("questionTypeLabel", questionTypeLabel(normalizeQuestionType(dto.getQuestionType())));
        payload.put("difficultyLevel", dto.getDifficultyLevel() == null ? 3 : dto.getDifficultyLevel());
        payload.put("count", dto.getCount() == null || dto.getCount() <= 0 ? 5 : dto.getCount());
        payload.put("saveToBank", !Boolean.FALSE.equals(dto.getSaveToBank()));
        payload.put("instruction", StringUtils.defaultIfEmpty(dto.getUserInstruction(), ""));
        payload.put("operator", operator);
        return writeJsonQuietly(payload);
    }

    private String writeJsonQuietly(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private String resolveErrorMessage(Exception e) {
        Throwable current = e;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return StringUtils.defaultIfEmpty(current.getMessage(), e.getMessage());
    }

    private int defaultNumber(Integer value) {
        return value == null ? 0 : value;
    }

    private void appendQuestionsByRule(Long courseId, PaperAssembleRuleDto rule, Set<Long> usedQuestionIds,
            List<PaperQuestionConfigDto> selectedQuestions, int startSortNo) {
        if (rule == null || StringUtils.isEmpty(rule.getQuestionType()) || rule.getCount() == null
                || rule.getCount() <= 0) {
            return;
        }
        ScQuestionBank query = new ScQuestionBank();
        query.setCourseId(courseId);
        query.setQuestionType(normalizeQuestionType(rule.getQuestionType()));
        query.setKnowledgePointId(rule.getKnowledgePointId());
        query.setStatus("0");
        List<ScQuestionBank> candidates = scQuestionBankMapper.selectScQuestionBankList(query)
                .stream()
                .filter(item -> !usedQuestionIds.contains(item.getQuestionId()))
                .filter(item -> rule.getDifficultyLevel() == null
                        || rule.getDifficultyLevel().equals(item.getDifficultyLevel()))
                .sorted(Comparator.comparing(ScQuestionBank::getQualityScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        if (candidates.size() < rule.getCount()) {
            throw new ServiceException("题型[" + rule.getQuestionType() + "]可用题目不足，期望" + rule.getCount()
                    + "题，实际仅" + candidates.size() + "题");
        }
        Collections.shuffle(candidates);
        List<ScQuestionBank> chosen = candidates.subList(0, rule.getCount());
        chosen.sort(Comparator.comparing(ScQuestionBank::getQuestionId));
        int sortNo = startSortNo;
        for (ScQuestionBank question : chosen) {
            usedQuestionIds.add(question.getQuestionId());
            PaperQuestionConfigDto config = new PaperQuestionConfigDto();
            config.setQuestionId(question.getQuestionId());
            config.setScore(rule.getScore() == null ? BigDecimal.TEN : rule.getScore());
            config.setSortNo(sortNo++);
            selectedQuestions.add(config);
        }
    }

    private ScQuestionBank buildQuestionEntity(QuestionUpsertDto dto) {
        ScQuestionBank question = new ScQuestionBank();
        question.setCourseId(dto.getCourseId());
        question.setChapterId(dto.getChapterId());
        question.setKnowledgePointId(dto.getKnowledgePointId());
        question.setQuestionType(normalizeQuestionType(dto.getQuestionType()));
        question.setDifficultyLevel(dto.getDifficultyLevel() == null ? 1 : dto.getDifficultyLevel());
        question.setStem(StringUtils.trim(dto.getStem()));
        question.setAnswer(StringUtils.trim(dto.getAnswer()));
        question.setAnalysis(StringUtils.trim(dto.getAnalysis()));
        question.setSource(StringUtils.trim(dto.getSource()));
        question.setQuestionTags(StringUtils.trim(dto.getQuestionTags()));
        question.setMaterialContent(StringUtils.trim(dto.getMaterialContent()));
        question.setSourceBatchNo(StringUtils.trim(dto.getSourceBatchNo()));
        question.setQualityScore(dto.getQualityScore() == null ? BigDecimal.ZERO : dto.getQualityScore());
        question.setStatus(StringUtils.defaultIfEmpty(dto.getStatus(), "0"));
        question.setRemark(StringUtils.trim(dto.getRemark()));
        return question;
    }

    private void validateQuestion(QuestionUpsertDto dto) {
        if (dto == null) {
            throw new ServiceException("题目数据不能为空");
        }
        if (StringUtils.isEmpty(dto.getQuestionType())) {
            throw new ServiceException("题型不能为空");
        }
        if (StringUtils.isEmpty(dto.getStem())) {
            throw new ServiceException("题干不能为空");
        }
        String questionType = normalizeQuestionType(dto.getQuestionType());
        if (requiresOptions(questionType)) {
            if (dto.getOptions() == null || dto.getOptions().isEmpty()) {
                throw new ServiceException("当前题型必须维护选项");
            }
            long rightCount = dto.getOptions().stream()
                    .filter(item -> isTrue(item == null ? null : item.getIsRight()))
                    .count();
            if ("single".equals(questionType) && rightCount != 1) {
                throw new ServiceException("单选题必须且只能有一个正确选项");
            }
            if ("multiple".equals(questionType) && rightCount < 2) {
                throw new ServiceException("多选题至少需要两个正确选项");
            }
            if ("judge".equals(questionType) && rightCount != 1) {
                throw new ServiceException("判断题必须设置一个正确选项");
            }
        }
        if (StringUtils.isEmpty(dto.getAnswer()) && OBJECTIVE_TYPES.contains(questionType)) {
            throw new ServiceException("客观题答案不能为空");
        }
    }

    private void normalizeQuestionPayload(QuestionUpsertDto dto) {
        if (dto == null) {
            return;
        }
        String questionType = normalizeQuestionType(dto.getQuestionType());
        if (requiresOptions(questionType)) {
            dto.setAnswer(composeAnswerFromOptions(questionType, dto.getOptions(), dto.getAnswer()));
        }
    }

    private String composeAnswerFromOptions(String questionType, List<QuestionOptionEditDto> options, String fallback) {
        if (options == null || options.isEmpty()) {
            return StringUtils.trim(fallback);
        }
        List<QuestionOptionEditDto> rightOptions = options.stream()
                .filter(item -> item != null && isTrue(item.getIsRight()))
                .collect(Collectors.toList());
        if (rightOptions.isEmpty()) {
            return StringUtils.trim(fallback);
        }
        if ("judge".equals(questionType)) {
            QuestionOptionEditDto option = rightOptions.get(0);
            if (StringUtils.isNotEmpty(option.getOptionContent())) {
                return option.getOptionContent().trim();
            }
            return StringUtils.defaultIfEmpty(option.getOptionKey(), "").trim().toUpperCase(Locale.ROOT);
        }
        List<String> keys = rightOptions.stream()
                .map(item -> StringUtils.defaultIfEmpty(item.getOptionKey(), "").trim().toUpperCase(Locale.ROOT))
                .filter(StringUtils::isNotEmpty)
                .sorted()
                .collect(Collectors.toList());
        return String.join(",", keys);
    }

    private void saveQuestionOptions(Long questionId, List<QuestionOptionEditDto> optionDtos) {
        if (optionDtos == null || optionDtos.isEmpty()) {
            return;
        }
        for (int i = 0; i < optionDtos.size(); i++) {
            QuestionOptionEditDto item = optionDtos.get(i);
            if (item == null || StringUtils.isEmpty(item.getOptionContent())) {
                continue;
            }
            ScQuestionOption option = new ScQuestionOption();
            option.setQuestionId(questionId);
            option.setOptionKey(StringUtils.isEmpty(item.getOptionKey()) ? buildDefaultOptionKey(i) : item.getOptionKey());
            option.setOptionContent(item.getOptionContent());
            option.setIsRight(isTrue(item.getIsRight()) ? "1" : "0");
            scQuestionOptionMapper.insertScQuestionOption(option);
        }
    }

    private String buildDefaultOptionKey(int index) {
        return String.valueOf((char) ('A' + index));
    }

    private QuestionDetailVo buildQuestionDetail(ScQuestionBank question, boolean includeOptions) {
        QuestionDetailVo vo = new QuestionDetailVo();
        vo.setQuestionId(question.getQuestionId());
        vo.setCourseId(question.getCourseId());
        vo.setChapterId(question.getChapterId());
        vo.setKnowledgePointId(question.getKnowledgePointId());
        vo.setQuestionType(question.getQuestionType());
        vo.setDifficultyLevel(question.getDifficultyLevel());
        vo.setStem(question.getStem());
        vo.setAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        vo.setSource(question.getSource());
        vo.setQuestionTags(question.getQuestionTags());
        vo.setMaterialContent(question.getMaterialContent());
        vo.setSourceBatchNo(question.getSourceBatchNo());
        vo.setQualityScore(question.getQualityScore());
        vo.setStatus(question.getStatus());
        if (includeOptions) {
            List<QuestionOptionVo> options = scQuestionOptionMapper.selectScQuestionOptionByQuestionId(question.getQuestionId())
                    .stream()
                    .map(this::buildOptionVo)
                    .collect(Collectors.toList());
            vo.setOptions(options);
        }
        return vo;
    }

    private QuestionDetailVo buildQuestionDetailPreview(QuestionUpsertDto dto) {
        QuestionDetailVo vo = new QuestionDetailVo();
        vo.setCourseId(dto.getCourseId());
        vo.setChapterId(dto.getChapterId());
        vo.setKnowledgePointId(dto.getKnowledgePointId());
        vo.setQuestionType(normalizeQuestionType(dto.getQuestionType()));
        vo.setDifficultyLevel(dto.getDifficultyLevel());
        vo.setStem(dto.getStem());
        vo.setAnswer(dto.getAnswer());
        vo.setAnalysis(dto.getAnalysis());
        vo.setSource(dto.getSource());
        vo.setQuestionTags(dto.getQuestionTags());
        vo.setMaterialContent(dto.getMaterialContent());
        vo.setSourceBatchNo(dto.getSourceBatchNo());
        vo.setQualityScore(dto.getQualityScore());
        vo.setStatus(dto.getStatus());
        if (dto.getOptions() != null) {
            List<QuestionOptionVo> options = new ArrayList<>();
            for (QuestionOptionEditDto item : dto.getOptions()) {
                if (item == null) {
                    continue;
                }
                QuestionOptionVo optionVo = new QuestionOptionVo();
                optionVo.setOptionKey(item.getOptionKey());
                optionVo.setOptionContent(item.getOptionContent());
                optionVo.setIsRight(isTrue(item.getIsRight()) ? "1" : "0");
                options.add(optionVo);
            }
            vo.setOptions(options);
        }
        return vo;
    }

    private QuestionOptionVo buildOptionVo(ScQuestionOption item) {
        QuestionOptionVo vo = new QuestionOptionVo();
        vo.setOptionId(item.getOptionId());
        vo.setOptionKey(item.getOptionKey());
        vo.setOptionContent(item.getOptionContent());
        vo.setIsRight(item.getIsRight());
        return vo;
    }

    private ScQuestionBank findQuestionByStem(Long courseId, String stem, String questionType) {
        if (StringUtils.isEmpty(stem)) {
            return null;
        }
        ScQuestionBank query = new ScQuestionBank();
        query.setCourseId(courseId);
        query.setQuestionType(normalizeQuestionType(questionType));
        List<ScQuestionBank> list = scQuestionBankMapper.selectScQuestionBankList(query);
        String normalizedStem = normalizeText(stem);
        for (ScQuestionBank item : list) {
            if (normalizedStem.equals(normalizeText(item.getStem()))) {
                return item;
            }
        }
        return null;
    }

    private QuestionUpsertDto convertImportRow(QuestionImportDto row) {
        QuestionUpsertDto dto = new QuestionUpsertDto();
        dto.setCourseId(row.getCourseId());
        dto.setKnowledgePointId(row.getKnowledgePointId());
        dto.setQuestionType(row.getQuestionType());
        dto.setDifficultyLevel(row.getDifficultyLevel());
        dto.setStem(row.getStem());
        dto.setAnswer(row.getAnswer());
        dto.setAnalysis(row.getAnalysis());
        dto.setSource(row.getSource());
        dto.setQuestionTags(row.getQuestionTags());
        dto.setMaterialContent(row.getMaterialContent());
        dto.setSourceBatchNo(row.getSourceBatchNo());
        dto.setStatus(StringUtils.defaultIfEmpty(row.getStatus(), "0"));
        dto.setRemark(row.getRemark());
        dto.setOptions(parseOptionsText(row.getOptionsText()));
        markCorrectOptions(dto);
        normalizeQuestionPayload(dto);
        return dto;
    }

    private void syncLegacyQuestionToItem(QuestionUpsertDto dto, Long legacyQuestionId, String operator, boolean updating,
            QuestionUpsertDto originalSnapshot) {
        if (dto == null || legacyQuestionId == null) {
            return;
        }
        QuestionItemUpsertDto itemDto = toQuestionItemUpsertDto(dto, legacyQuestionId);
        ScQuestionItem existing = scQuestionItemService.selectScQuestionItemBySource("LEGACY", legacyQuestionId);
        if (existing == null) {
            existing = scQuestionItemService.selectLatestScQuestionItemBySourceRefId(legacyQuestionId);
        }
        if (existing == null) {
            if (updating && originalSnapshot != null) {
                QuestionItemUpsertDto initialItemDto = toQuestionItemUpsertDto(originalSnapshot, legacyQuestionId);
                initialItemDto.setChangeType("MIGRATE");
                Long itemId = scQuestionItemService.insertScQuestionItem(initialItemDto, operator);
                itemDto.setItemId(itemId);
                scQuestionItemService.updateScQuestionItem(itemDto, operator);
                return;
            }
            scQuestionItemService.insertScQuestionItem(itemDto, operator);
            return;
        }
        itemDto.setItemId(existing.getItemId());
        if (updating || dto.getQuestionId() != null || itemDto.getItemId() != null) {
            scQuestionItemService.updateScQuestionItem(itemDto, operator);
            return;
        }
        scQuestionItemService.insertScQuestionItem(itemDto, operator);
    }

    private QuestionItemUpsertDto toQuestionItemUpsertDto(QuestionUpsertDto dto, Long legacyQuestionId) {
        QuestionItemUpsertDto itemDto = new QuestionItemUpsertDto();
        itemDto.setCatalogId(dto.getCatalogId());
        itemDto.setCourseId(dto.getCourseId());
        itemDto.setChapterId(dto.getChapterId());
        itemDto.setKnowledgePointId(dto.getKnowledgePointId());
        itemDto.setQuestionType(normalizeQuestionType(dto.getQuestionType()));
        itemDto.setDifficultyLevel(dto.getDifficultyLevel());
        itemDto.setSourceType(resolveQuestionSourceType(dto, legacyQuestionId));
        itemDto.setSourceRefId(legacyQuestionId);
        itemDto.setSourceBatchNo(dto.getSourceBatchNo());
        itemDto.setStem(dto.getStem());
        itemDto.setAnswer(dto.getAnswer());
        itemDto.setAnalysis(dto.getAnalysis());
        itemDto.setMaterialContent(dto.getMaterialContent());
        itemDto.setQuestionTags(dto.getQuestionTags());
        itemDto.setQualityScore(dto.getQualityScore());
        itemDto.setSource(dto.getSource());
        itemDto.setChangeType(resolveQuestionChangeType(dto, legacyQuestionId));
        itemDto.setStatus(dto.getStatus());
        itemDto.setRemark(dto.getRemark());
        return itemDto;
    }

    private String resolveQuestionSourceType(QuestionUpsertDto dto, Long legacyQuestionId) {
        if (legacyQuestionId != null) {
            return "LEGACY";
        }
        if (StringUtils.isNotEmpty(dto.getSourceBatchNo()) && dto.getSourceBatchNo().startsWith("AI-")) {
            return "AI";
        }
        return "MANUAL";
    }

    private String resolveQuestionChangeType(QuestionUpsertDto dto, Long legacyQuestionId) {
        if (StringUtils.isNotEmpty(dto.getSourceBatchNo()) && dto.getSourceBatchNo().startsWith("AI-")) {
            return "AI";
        }
        return dto.getQuestionId() == null ? "CREATE" : "EDIT";
    }

    private QuestionUpsertDto buildLegacyQuestionSnapshot(ScQuestionBank question) {
        if (question == null) {
            return null;
        }
        QuestionUpsertDto snapshot = new QuestionUpsertDto();
        snapshot.setQuestionId(question.getQuestionId());
        snapshot.setCatalogId(question.getCatalogId());
        snapshot.setCourseId(question.getCourseId());
        snapshot.setChapterId(question.getChapterId());
        snapshot.setKnowledgePointId(question.getKnowledgePointId());
        snapshot.setQuestionType(question.getQuestionType());
        snapshot.setDifficultyLevel(question.getDifficultyLevel());
        snapshot.setStem(question.getStem());
        snapshot.setAnswer(question.getAnswer());
        snapshot.setAnalysis(question.getAnalysis());
        snapshot.setSource(question.getSource());
        snapshot.setQuestionTags(question.getQuestionTags());
        snapshot.setMaterialContent(question.getMaterialContent());
        snapshot.setSourceBatchNo(question.getSourceBatchNo());
        snapshot.setQualityScore(question.getQualityScore());
        snapshot.setStatus(question.getStatus());
        snapshot.setRemark(question.getRemark());
        return snapshot;
    }

    private List<QuestionOptionEditDto> parseOptionsText(String optionsText) {
        if (StringUtils.isEmpty(optionsText)) {
            return Collections.emptyList();
        }
        String[] segments = optionsText.split("[\\|\\n]");
        List<QuestionOptionEditDto> result = new ArrayList<>();
        for (String raw : segments) {
            if (StringUtils.isEmpty(raw)) {
                continue;
            }
            String item = raw.trim();
            int splitIndex = item.indexOf('.');
            if (splitIndex <= 0) {
                splitIndex = item.indexOf('：');
            }
            if (splitIndex <= 0) {
                splitIndex = item.indexOf(':');
            }
            QuestionOptionEditDto dto = new QuestionOptionEditDto();
            if (splitIndex > 0) {
                dto.setOptionKey(item.substring(0, splitIndex).trim());
                dto.setOptionContent(item.substring(splitIndex + 1).trim());
            } else {
                dto.setOptionContent(item);
            }
            dto.setIsRight("0");
            result.add(dto);
        }
        return result;
    }

    private void markCorrectOptions(QuestionUpsertDto dto) {
        if (dto == null || dto.getOptions() == null || dto.getOptions().isEmpty() || StringUtils.isEmpty(dto.getAnswer())) {
            return;
        }
        Set<String> answers = splitAnswerTokens(dto.getAnswer());
        for (QuestionOptionEditDto item : dto.getOptions()) {
            if (item != null && answers.contains(StringUtils.defaultIfEmpty(item.getOptionKey(), "")
                    .trim().toUpperCase(Locale.ROOT))) {
                item.setIsRight("1");
            }
        }
    }

    private Set<String> splitAnswerTokens(String answer) {
        String normalized = StringUtils.defaultIfEmpty(answer, "")
                .replace("，", ",")
                .replace("、", ",")
                .replace(";", ",")
                .replace("；", ",");
        Set<String> result = new HashSet<>();
        for (String token : normalized.split(",")) {
            if (StringUtils.isNotEmpty(token)) {
                result.add(token.trim().toUpperCase(Locale.ROOT));
            }
        }
        return result;
    }

    private String buildImportMessage(int successCount, int updateCount, int skipCount, List<String> errors) {
        StringBuilder builder = new StringBuilder();
        builder.append("导入完成，新增 ").append(successCount).append(" 条");
        if (updateCount > 0) {
            builder.append("，更新 ").append(updateCount).append(" 条");
        }
        if (skipCount > 0) {
            builder.append("，跳过 ").append(skipCount).append(" 条");
        }
        if (errors != null && !errors.isEmpty()) {
            builder.append("。错误：")
                    .append(String.join("；", errors.subList(0, Math.min(5, errors.size()))));
            if (errors.size() > 5) {
                builder.append("...等 ").append(errors.size()).append(" 个错误");
            }
        }
        return builder.toString();
    }

    private void validatePaper(PaperUpsertDto dto) {
        if (dto == null) {
            throw new ServiceException("试卷数据不能为空");
        }
        if (StringUtils.isEmpty(dto.getPaperName())) {
            throw new ServiceException("试卷名称不能为空");
        }
        if (dto.getQuestions() == null || dto.getQuestions().isEmpty()) {
            if (dto.getSubPapers() == null || dto.getSubPapers().isEmpty()) {
                throw new ServiceException("试卷题目不能为空");
            }
        }
        String paperLevel = StringUtils.defaultIfEmpty(dto.getPaperLevel(), "MAIN");
        if ("SUB".equalsIgnoreCase(paperLevel) && dto.getParentPaperId() == null && dto.getPaperId() == null) {
            // 子卷在新增场景下允许由主卷递归挂接，这里不额外抛错
        }
        Set<Long> questionIds = new HashSet<>();
        if (dto.getQuestions() != null) {
            for (PaperQuestionConfigDto item : dto.getQuestions()) {
                if (item == null || item.getQuestionId() == null) {
                    throw new ServiceException("试卷题目不能为空");
                }
                if (!questionIds.add(item.getQuestionId())) {
                    throw new ServiceException("试卷中存在重复题目：" + item.getQuestionId());
                }
                if (scQuestionBankMapper.selectScQuestionBankByQuestionId(item.getQuestionId()) == null) {
                    throw new ServiceException("题目不存在：" + item.getQuestionId());
                }
            }
        }
        if (dto.getSubPapers() != null) {
            for (PaperUpsertDto subPaper : dto.getSubPapers()) {
                if (subPaper == null) {
                    continue;
                }
                if (StringUtils.isEmpty(subPaper.getPaperLevel())) {
                    subPaper.setPaperLevel("SUB");
                }
                validatePaper(subPaper);
            }
        }
    }

    private ScExamPaper buildPaperEntity(PaperUpsertDto dto) {
        ScExamPaper paper = new ScExamPaper();
        paper.setParentPaperId(dto.getParentPaperId());
        paper.setPaperName(StringUtils.trim(dto.getPaperName()));
        paper.setCourseId(dto.getCourseId());
        paper.setPaperType(StringUtils.defaultIfEmpty(dto.getPaperType(), "fixed"));
        paper.setPaperLevel(StringUtils.defaultIfEmpty(dto.getPaperLevel(), "MAIN"));
        paper.setAnswerMode(StringUtils.defaultIfEmpty(dto.getAnswerMode(), "REQUIRED"));
        paper.setPaperWeight(dto.getPaperWeight() == null ? BigDecimal.valueOf(100) : dto.getPaperWeight());
        paper.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        paper.setDurationMinutes(dto.getDurationMinutes() == null ? 60 : dto.getDurationMinutes());
        paper.setPublishStatus(StringUtils.defaultIfEmpty(dto.getPublishStatus(), "draft"));
        paper.setPublishStartTime(dto.getPublishStartTime());
        paper.setPublishEndTime(dto.getPublishEndTime());
        paper.setStrategyTemplateId(dto.getStrategyTemplateId());
        paper.setPassScore(dto.getPassScore());
        paper.setMarkingMode(StringUtils.defaultIfEmpty(dto.getMarkingMode(), "auto_first"));
        paper.setQuestionOrderMode(StringUtils.defaultIfEmpty(dto.getQuestionOrderMode(), "manual"));
        paper.setQuestionNavigationMode(StringUtils.defaultIfEmpty(dto.getQuestionNavigationMode(), "free"));
        paper.setAllowReviewAnalysis(StringUtils.defaultIfEmpty(dto.getAllowReviewAnalysis(), "1"));
        paper.setAntiCheatEnabled(StringUtils.defaultIfEmpty(dto.getAntiCheatEnabled(), "1"));
        paper.setMaxFocusLossCount(dto.getMaxFocusLossCount() == null ? 5 : Math.max(0, dto.getMaxFocusLossCount()));
        paper.setAutoSubmitOnFocusLossLimit(StringUtils.defaultIfEmpty(dto.getAutoSubmitOnFocusLossLimit(), "0"));
        paper.setAllowCopyPaste(StringUtils.defaultIfEmpty(dto.getAllowCopyPaste(), "1"));
        paper.setMaxAttemptCount(dto.getMaxAttemptCount() == null ? 0 : Math.max(0, dto.getMaxAttemptCount()));
        paper.setAllowViewScore(StringUtils.defaultIfEmpty(dto.getAllowViewScore(), "1"));
        paper.setSectionConfigJson(dto.getSectionConfigJson());
        paper.setAdaptiveRuleJson(dto.getAdaptiveRuleJson());
        paper.setStatus(StringUtils.defaultIfEmpty(dto.getStatus(), "0"));
        paper.setRemark(StringUtils.trim(dto.getRemark()));
        return paper;
    }

    private BigDecimal calculatePaperTotalScore(List<PaperQuestionConfigDto> questions, BigDecimal providedTotal) {
        if (providedTotal != null && providedTotal.compareTo(BigDecimal.ZERO) > 0) {
            return providedTotal.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        if (questions != null) {
            for (PaperQuestionConfigDto item : questions) {
                total = total.add(item == null || item.getScore() == null ? BigDecimal.ZERO : item.getScore());
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void savePaperQuestions(Long paperId, List<PaperQuestionConfigDto> questions) {
        if (questions == null) {
            return;
        }
        int sortNo = 1;
        for (PaperQuestionConfigDto item : questions) {
            if (item == null || item.getQuestionId() == null) {
                continue;
            }
            ScExamPaperQuestion relation = new ScExamPaperQuestion();
            relation.setPaperId(paperId);
            relation.setQuestionId(item.getQuestionId());
            relation.setScore(item.getScore() == null ? BigDecimal.TEN : item.getScore());
            relation.setSortNo(item.getSortNo() == null ? sortNo : item.getSortNo());
            scExamPaperQuestionMapper.insertScExamPaperQuestion(relation);
            sortNo++;
        }
    }

    private ExamPaperDetailVo buildPaperDetail(ScExamPaper paper, boolean includeQuestions) {
        ExamPaperDetailVo vo = new ExamPaperDetailVo();
        vo.setPaperId(paper.getPaperId());
        vo.setParentPaperId(paper.getParentPaperId());
        vo.setPaperName(paper.getPaperName());
        vo.setCourseId(paper.getCourseId());
        vo.setPaperType(paper.getPaperType());
        vo.setPaperLevel(paper.getPaperLevel());
        vo.setAnswerMode(paper.getAnswerMode());
        vo.setPaperWeight(paper.getPaperWeight());
        vo.setSortNo(paper.getSortNo());
        vo.setTotalScore(paper.getTotalScore());
        vo.setDurationMinutes(paper.getDurationMinutes());
        vo.setPublishStatus(paper.getPublishStatus());
        vo.setPublishStartTime(paper.getPublishStartTime());
        vo.setPublishEndTime(paper.getPublishEndTime());
        vo.setStrategyTemplateId(paper.getStrategyTemplateId());
        vo.setPassScore(paper.getPassScore());
        vo.setMarkingMode(paper.getMarkingMode());
        vo.setQuestionOrderMode(paper.getQuestionOrderMode());
        vo.setQuestionNavigationMode(paper.getQuestionNavigationMode());
        vo.setAllowReviewAnalysis(paper.getAllowReviewAnalysis());
        vo.setAntiCheatEnabled(paper.getAntiCheatEnabled());
        vo.setMaxFocusLossCount(paper.getMaxFocusLossCount());
        vo.setAutoSubmitOnFocusLossLimit(paper.getAutoSubmitOnFocusLossLimit());
        vo.setAllowCopyPaste(paper.getAllowCopyPaste());
        vo.setMaxAttemptCount(paper.getMaxAttemptCount());
        vo.setAllowViewScore(paper.getAllowViewScore());
        vo.setSectionConfigJson(paper.getSectionConfigJson());
        vo.setAdaptiveRuleJson(paper.getAdaptiveRuleJson());
        vo.setStatus(paper.getStatus());
        if (includeQuestions) {
            List<ExamPaperQuestionDetailVo> questions = scExamPaperQuestionMapper.selectScExamPaperQuestionByPaperId(paper.getPaperId())
                    .stream()
                    .map(this::buildPaperQuestionDetail)
                    .collect(Collectors.toList());
            vo.setQuestions(questions);
            vo.setSubPapers(loadSubPaperDetails(paper.getPaperId()));
        }
        return vo;
    }

    private ExamPaperDetailVo buildPaperDetailFromConfigs(ScExamPaper paper, List<PaperQuestionConfigDto> configs) {
        ExamPaperDetailVo vo = new ExamPaperDetailVo();
        vo.setPaperId(paper.getPaperId());
        vo.setParentPaperId(paper.getParentPaperId());
        vo.setPaperName(paper.getPaperName());
        vo.setCourseId(paper.getCourseId());
        vo.setPaperType(paper.getPaperType());
        vo.setPaperLevel(paper.getPaperLevel());
        vo.setAnswerMode(paper.getAnswerMode());
        vo.setPaperWeight(paper.getPaperWeight());
        vo.setSortNo(paper.getSortNo());
        vo.setTotalScore(paper.getTotalScore());
        vo.setDurationMinutes(paper.getDurationMinutes());
        vo.setPublishStatus(paper.getPublishStatus());
        vo.setPublishStartTime(paper.getPublishStartTime());
        vo.setPublishEndTime(paper.getPublishEndTime());
        vo.setStrategyTemplateId(paper.getStrategyTemplateId());
        vo.setPassScore(paper.getPassScore());
        vo.setMarkingMode(paper.getMarkingMode());
        vo.setQuestionOrderMode(paper.getQuestionOrderMode());
        vo.setQuestionNavigationMode(paper.getQuestionNavigationMode());
        vo.setAllowReviewAnalysis(paper.getAllowReviewAnalysis());
        vo.setAntiCheatEnabled(paper.getAntiCheatEnabled());
        vo.setMaxFocusLossCount(paper.getMaxFocusLossCount());
        vo.setAutoSubmitOnFocusLossLimit(paper.getAutoSubmitOnFocusLossLimit());
        vo.setAllowCopyPaste(paper.getAllowCopyPaste());
        vo.setMaxAttemptCount(paper.getMaxAttemptCount());
        vo.setAllowViewScore(paper.getAllowViewScore());
        vo.setSectionConfigJson(paper.getSectionConfigJson());
        vo.setAdaptiveRuleJson(paper.getAdaptiveRuleJson());
        vo.setStatus(paper.getStatus());
        List<ExamPaperQuestionDetailVo> details = new ArrayList<>();
        if (configs != null) {
            for (PaperQuestionConfigDto item : configs) {
                ExamPaperQuestionDetailVo detailVo = new ExamPaperQuestionDetailVo();
                detailVo.setQuestionId(item.getQuestionId());
                detailVo.setScore(item.getScore());
                detailVo.setSortNo(item.getSortNo());
                ScQuestionBank question = scQuestionBankMapper.selectScQuestionBankByQuestionId(item.getQuestionId());
                if (question != null) {
                    detailVo.setQuestion(buildQuestionDetail(question, true));
                }
                details.add(detailVo);
            }
        }
        vo.setQuestions(details);
        return vo;
    }

    private void savePaperTree(PaperUpsertDto dto, Long parentPaperId, String operator) {
        ScExamPaper paper = buildPaperEntity(dto);
        paper.setParentPaperId(parentPaperId);
        paper.setCreateBy(operator);
        paper.setTotalScore(calculateTreePaperTotal(dto));
        scExamPaperMapper.insertScExamPaper(paper);
        savePaperQuestions(paper.getPaperId(), dto.getQuestions());
        if (dto.getSubPapers() != null) {
            int sortNo = 1;
            for (PaperUpsertDto subPaper : dto.getSubPapers()) {
                if (subPaper == null) {
                    continue;
                }
                subPaper.setParentPaperId(paper.getPaperId());
                subPaper.setCourseId(subPaper.getCourseId() == null ? dto.getCourseId() : subPaper.getCourseId());
                subPaper.setPaperLevel("SUB");
                if (subPaper.getSortNo() == null) {
                    subPaper.setSortNo(sortNo);
                }
                savePaperTree(subPaper, paper.getPaperId(), operator);
                sortNo++;
            }
        }
    }

    private void updatePaperTree(PaperUpsertDto dto, String operator) {
        ScExamPaper paper = buildPaperEntity(dto);
        paper.setPaperId(dto.getPaperId());
        paper.setUpdateBy(operator);
        paper.setTotalScore(calculateTreePaperTotal(dto));
        scExamPaperMapper.updateScExamPaper(paper);
        scExamPaperQuestionMapper.deleteScExamPaperQuestionByPaperId(dto.getPaperId());
        savePaperQuestions(dto.getPaperId(), dto.getQuestions());
        deleteChildPapers(dto.getPaperId());
        if (dto.getSubPapers() != null) {
            int sortNo = 1;
            for (PaperUpsertDto subPaper : dto.getSubPapers()) {
                if (subPaper == null) {
                    continue;
                }
                subPaper.setParentPaperId(dto.getPaperId());
                subPaper.setCourseId(subPaper.getCourseId() == null ? dto.getCourseId() : subPaper.getCourseId());
                subPaper.setPaperLevel("SUB");
                if (subPaper.getSortNo() == null) {
                    subPaper.setSortNo(sortNo);
                }
                savePaperTree(subPaper, dto.getPaperId(), operator);
                sortNo++;
            }
        }
    }

    private void deleteChildPapers(Long parentPaperId) {
        ScExamPaper query = new ScExamPaper();
        query.setParentPaperId(parentPaperId);
        List<ScExamPaper> children = scExamPaperMapper.selectScExamPaperList(query);
        for (ScExamPaper child : children) {
            if (child == null || child.getPaperId() == null) {
                continue;
            }
            scExamPaperQuestionMapper.deleteScExamPaperQuestionByPaperId(child.getPaperId());
            scExamPaperMapper.deleteScExamPaperByPaperId(child.getPaperId());
        }
    }

    private List<ExamPaperDetailVo> loadSubPaperDetails(Long parentPaperId) {
        if (parentPaperId == null) {
            return Collections.emptyList();
        }
        ScExamPaper query = new ScExamPaper();
        query.setParentPaperId(parentPaperId);
        List<ScExamPaper> subPapers = scExamPaperMapper.selectScExamPaperList(query);
        if (subPapers == null || subPapers.isEmpty()) {
            return Collections.emptyList();
        }
        return subPapers.stream()
                .map(item -> buildPaperDetail(item, true))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTreePaperTotal(PaperUpsertDto dto) {
        if (dto == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (dto.getTotalScore() != null && dto.getTotalScore().compareTo(BigDecimal.ZERO) > 0) {
            return dto.getTotalScore().setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = calculatePaperTotalScore(dto.getQuestions(), null);
        if (dto.getSubPapers() != null) {
            for (PaperUpsertDto subPaper : dto.getSubPapers()) {
                total = total.add(calculateTreePaperTotal(subPaper));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private ExamPaperQuestionDetailVo buildPaperQuestionDetail(ScExamPaperQuestion relation) {
        ExamPaperQuestionDetailVo vo = new ExamPaperQuestionDetailVo();
        vo.setId(relation.getId());
        vo.setPaperId(relation.getPaperId());
        vo.setQuestionId(relation.getQuestionId());
        vo.setScore(relation.getScore());
        vo.setSortNo(relation.getSortNo());
        ScQuestionBank question = scQuestionBankMapper.selectScQuestionBankByQuestionId(relation.getQuestionId());
        if (question != null) {
            vo.setQuestion(buildQuestionDetail(question, true));
        }
        return vo;
    }

    private ScExamPaper findLatestPaper(Long courseId, String paperName) {
        ScExamPaper query = new ScExamPaper();
        query.setCourseId(courseId);
        query.setPaperName(paperName);
        List<ScExamPaper> list = scExamPaperMapper.selectScExamPaperList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    private String buildAiGenerateSystemPrompt(Map<String, String> promptContext) {
        String defaultPrompt = "你是高校与中学校园题库生成助手。"
                + "你必须严格围绕当前课程与知识点出题，绝不能输出与课程无关的题目。"
                + "如果课程是 Python、Java、数据库等学科课程，题目必须体现该课程本身的专业知识，而不是智慧校园平台、校园管理、教务流程等泛化主题。"
                + "你需要优先参考课程名称、课程简介、章节目录和知识点信息，确保题目贴合课程教学内容。"
                + "你必须只返回 JSON 数组，不允许返回 markdown，不允许解释，不允许额外说明。"
                + "数组每一项格式固定为："
                + "{\"questionType\":\"single\",\"difficultyLevel\":3,\"stem\":\"...\",\"answer\":\"...\",\"analysis\":\"...\",\"options\":[{\"optionKey\":\"A\",\"optionContent\":\"...\",\"isRight\":\"0\"}]}"
                + "。questionType 仅允许：single,multiple,judge,fill,essay,material,case。"
                + "客观题必须提供 options；主观题 options 返回空数组。"
                + "题干、答案、解析必须语言自然、专业准确、适合中国校园教学与考试场景，可直接入库。"
                + "当课程上下文与用户补充要求冲突时，以课程和知识点上下文为最高优先级。";
        ScAiPromptTemplate template = scAiPromptTemplateService.selectActiveTemplate("exam_question_generate");
        String templateContent = template == null || StringUtils.isEmpty(template.getPromptContent())
                ? defaultPrompt
                : template.getPromptContent();
        String merged = applyPromptTemplate(templateContent, promptContext);
        if (!merged.contains("只返回 JSON 数组")) {
            merged = merged + "\n\n补充约束：你必须只返回 JSON 数组，不允许输出解释、标题、markdown。";
        }
        return merged;
    }

    private String buildAiGenerateUserPrompt(AiGenerateQuestionDto dto, Map<String, String> promptContext) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("courseId", dto.getCourseId());
        payload.put("courseName", promptContext.get("courseName"));
        payload.put("courseCode", promptContext.get("courseCode"));
        payload.put("subjectType", promptContext.get("subjectType"));
        payload.put("courseIntro", promptContext.get("courseIntro"));
        payload.put("chapterOutline", promptContext.get("chapterOutline"));
        payload.put("knowledgePointId", dto.getKnowledgePointId());
        payload.put("knowledgeName", promptContext.get("knowledgeName"));
        payload.put("knowledgeDescription", promptContext.get("knowledgeDescription"));
        payload.put("questionType", normalizeQuestionType(dto.getQuestionType()));
        payload.put("questionTypeLabel", promptContext.get("questionTypeLabel"));
        payload.put("difficultyLevel", dto.getDifficultyLevel() == null ? 3 : dto.getDifficultyLevel());
        payload.put("count", dto.getCount() == null || dto.getCount() <= 0 ? 5 : dto.getCount());
        payload.put("difficultyHint", promptContext.get("difficultyHint"));
        payload.put("instruction", StringUtils.defaultIfEmpty(dto.getUserInstruction(), ""));
        payload.put("goal", "请基于上述课程上下文，生成可直接导入题库的试题数据，题目必须与课程强相关");
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new ServiceException("AI 出题请求构建失败");
        }
    }

    private Map<String, String> buildAiPromptContext(AiGenerateQuestionDto dto) {
        Map<String, String> context = new LinkedHashMap<>();
        String normalizedQuestionType = normalizeQuestionType(dto.getQuestionType());
        context.put("courseName", "");
        context.put("courseCode", "");
        context.put("subjectType", "");
        context.put("courseIntro", "");
        context.put("chapterOutline", "");
        context.put("knowledgeName", "");
        context.put("knowledgeDescription", "");
        context.put("questionTypeLabel", questionTypeLabel(normalizedQuestionType));
        context.put("difficultyHint", buildDifficultyHint(dto.getDifficultyLevel()));
        if (dto.getCourseId() != null) {
            ScCourse course = scCourseMapper.selectScCourseByCourseId(dto.getCourseId());
            if (course != null) {
                context.put("courseName", StringUtils.defaultIfEmpty(course.getCourseName(), ""));
                context.put("courseCode", StringUtils.defaultIfEmpty(course.getCourseCode(), ""));
                context.put("subjectType", StringUtils.defaultIfEmpty(course.getSubjectType(), ""));
                context.put("courseIntro", StringUtils.defaultIfEmpty(course.getIntro(), ""));
            }
            ScCourseChapter chapterQuery = new ScCourseChapter();
            chapterQuery.setCourseId(dto.getCourseId());
            chapterQuery.setStatus("0");
            List<ScCourseChapter> chapters = scCourseChapterMapper.selectScCourseChapterList(chapterQuery);
            if (chapters != null && !chapters.isEmpty()) {
                String chapterOutline = chapters.stream()
                        .map(ScCourseChapter::getChapterName)
                        .filter(StringUtils::isNotEmpty)
                        .limit(8)
                        .collect(Collectors.joining("、"));
                context.put("chapterOutline", chapterOutline);
            }
        }
        if (dto.getKnowledgePointId() != null) {
            ScKnowledgePoint point = scKnowledgePointMapper.selectScKnowledgePointByKnowledgePointId(dto.getKnowledgePointId());
            if (point != null) {
                context.put("knowledgeName", StringUtils.defaultIfEmpty(point.getKnowledgeName(), ""));
                context.put("knowledgeDescription", StringUtils.defaultIfEmpty(point.getDescription(), ""));
            }
        }
        return context;
    }

    private String buildDifficultyHint(Integer difficultyLevel) {
        int level = difficultyLevel == null ? 3 : difficultyLevel;
        if (level <= 2) {
            return "偏基础，考查核心概念与入门理解";
        }
        if (level == 3) {
            return "中等难度，兼顾概念理解与基础应用";
        }
        if (level == 4) {
            return "偏进阶，强调综合分析与场景应用";
        }
        return "高难度，强调综合推理、迁移应用与细节辨析";
    }

    private String applyPromptTemplate(String templateContent, Map<String, String> promptContext) {
        String content = StringUtils.defaultIfEmpty(templateContent, "");
        for (Map.Entry<String, String> entry : promptContext.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", StringUtils.defaultIfEmpty(entry.getValue(), ""));
        }
        return content;
    }

    private List<QuestionUpsertDto> parseAiQuestions(String rawContent, AiGenerateQuestionDto sourceDto) {
        if (StringUtils.isEmpty(rawContent)) {
            throw new ServiceException("AI 未返回题目内容");
        }
        try {
            JsonNode root = objectMapper.readTree(extractJsonArray(rawContent));
            if (!root.isArray()) {
                throw new ServiceException("AI 返回格式不正确");
            }
            List<QuestionUpsertDto> result = new ArrayList<>();
            for (JsonNode item : root) {
                QuestionUpsertDto dto = new QuestionUpsertDto();
                dto.setCourseId(sourceDto.getCourseId());
                dto.setCatalogId(sourceDto.getCatalogId());
                dto.setChapterId(sourceDto.getChapterId());
                dto.setKnowledgePointId(sourceDto.getKnowledgePointId());
                dto.setQuestionType(normalizeQuestionType(item.path("questionType").asText(sourceDto.getQuestionType())));
                dto.setDifficultyLevel(item.path("difficultyLevel").asInt(sourceDto.getDifficultyLevel() == null ? 3
                        : sourceDto.getDifficultyLevel()));
                dto.setStem(item.path("stem").asText(""));
                dto.setAnswer(item.path("answer").asText(""));
                dto.setAnalysis(item.path("analysis").asText(""));
                dto.setSource(StringUtils.defaultIfEmpty(sourceDto.getSource(), "AI智能出题"));
                dto.setSourceBatchNo("AI-" + System.currentTimeMillis());
                dto.setStatus(StringUtils.defaultIfEmpty(sourceDto.getStatus(), "0"));
                dto.setQualityScore(BigDecimal.ZERO);
                dto.setOptions(parseAiOptions(item.path("options")));
                markCorrectOptions(dto);
                normalizeQuestionPayload(dto);
                validateQuestion(dto);
                result.add(dto);
            }
            if (result.isEmpty()) {
                throw new ServiceException("AI 未生成有效题目");
            }
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("AI 题目解析失败：" + e.getMessage());
        }
    }

    private List<QuestionOptionEditDto> parseAiOptions(JsonNode optionsNode) {
        if (optionsNode == null || !optionsNode.isArray()) {
            return Collections.emptyList();
        }
        List<QuestionOptionEditDto> result = new ArrayList<>();
        for (JsonNode item : optionsNode) {
            QuestionOptionEditDto dto = new QuestionOptionEditDto();
            dto.setOptionKey(item.path("optionKey").asText(""));
            dto.setOptionContent(item.path("optionContent").asText(""));
            dto.setIsRight(item.path("isRight").asText("0"));
            result.add(dto);
        }
        return result;
    }

    private String extractJsonArray(String rawContent) {
        String normalized = rawContent.trim();
        int start = normalized.indexOf('[');
        int end = normalized.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return normalized.substring(start, end + 1);
        }
        return normalized;
    }

    private String normalizeQuestionType(String questionType) {
        return StringUtils.defaultIfEmpty(questionType, "single").trim().toLowerCase(Locale.ROOT);
    }

    private boolean requiresOptions(String questionType) {
        return "single".equals(questionType) || "multiple".equals(questionType) || "judge".equals(questionType);
    }

    private boolean isTrue(String value) {
        String normalized = StringUtils.defaultIfEmpty(value, "0").trim().toLowerCase(Locale.ROOT);
        return "1".equals(normalized) || "true".equals(normalized) || "y".equals(normalized)
                || "yes".equals(normalized);
    }

    private String normalizeText(String value) {
        return StringUtils.defaultIfEmpty(value, "")
                .replaceAll("\\s+", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
