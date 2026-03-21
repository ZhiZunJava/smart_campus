package com.smart.web.controller.campus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.smart.common.utils.poi.ExcelUtil;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.*;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.dto.AiGenerateQuestionDto;
import com.smart.system.domain.dto.AiQuestionTaskQueryDto;
import com.smart.system.domain.dto.ExamPaperAssembleDto;
import com.smart.system.domain.dto.ExamAnswerDraftSaveDto;
import com.smart.system.domain.dto.ExamQuestionSubmitDto;
import com.smart.system.domain.dto.ExamSubPaperSubmitDto;
import com.smart.system.domain.dto.ExamSubmitDto;
import com.smart.system.domain.dto.PaperUpsertDto;
import com.smart.system.domain.dto.QuestionImportDto;
import com.smart.system.domain.dto.QuestionUpsertDto;
import com.smart.system.domain.dto.WrongQuestionRetryDto;
import com.smart.system.service.*;

@RestController
@RequestMapping("/campus/exam")
public class ScExamController extends BaseController {
    @Autowired
    private IScQuestionBankService scQuestionBankService;
    @Autowired
    private IScQuestionOptionService scQuestionOptionService;
    @Autowired
    private IScExamPaperService scExamPaperService;
    @Autowired
    private IScExamPaperQuestionService scExamPaperQuestionService;
    @Autowired
    private IScExamRecordService scExamRecordService;
    @Autowired
    private IScExamAnswerService scExamAnswerService;
    @Autowired
    private IScWrongQuestionBookService scWrongQuestionBookService;
    @Autowired
    private IScExamManageService scExamManageService;
    @Autowired
    private IScExamAnalysisService scExamAnalysisService;
    @Autowired
    private IScExamStrategyTemplateService scExamStrategyTemplateService;

    @Autowired
    private IScAiTaskLogService scAiTaskLogService;

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/list")
    public TableDataInfo questionList(ScQuestionBank scQuestionBank) {
        startPage();
        return getDataTable(scQuestionBankService.selectScQuestionBankList(scQuestionBank));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/{questionId}")
    public AjaxResult getQuestionDetail(@PathVariable Long questionId) {
        return success(scExamManageService.selectQuestionDetail(questionId));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/type-meta")
    public AjaxResult questionTypeMeta() {
        return success(scExamManageService.listQuestionTypeMeta());
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:add')")
    @Log(title = "题库管理", businessType = BusinessType.INSERT)
    @PostMapping("/question")
    public AjaxResult addQuestion(@Validated @RequestBody ScQuestionBank scQuestionBank) {
        scQuestionBank.setCreateBy(getUsername());
        return toAjax(scQuestionBankService.insertScQuestionBank(scQuestionBank));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @PutMapping("/question")
    public AjaxResult editQuestion(@Validated @RequestBody ScQuestionBank scQuestionBank) {
        scQuestionBank.setUpdateBy(getUsername());
        return toAjax(scQuestionBankService.updateScQuestionBank(scQuestionBank));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:add')")
    @Log(title = "题库管理", businessType = BusinessType.INSERT)
    @PostMapping("/question/detail")
    public AjaxResult addQuestionDetail(@RequestBody QuestionUpsertDto dto) {
        return toAjax(scExamManageService.saveQuestion(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @Log(title = "题库管理", businessType = BusinessType.UPDATE)
    @PutMapping("/question/detail")
    public AjaxResult editQuestionDetail(@RequestBody QuestionUpsertDto dto) {
        return toAjax(scExamManageService.updateQuestion(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @Log(title = "题库管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/question/{questionIds}")
    public AjaxResult removeQuestion(@PathVariable Long[] questionIds) {
        return toAjax(scExamManageService.deleteQuestionByIds(questionIds));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @Log(title = "题库管理", businessType = BusinessType.IMPORT)
    @PostMapping("/question/importData")
    public AjaxResult importQuestions(MultipartFile file,
            @RequestParam(value = "updateSupport", defaultValue = "false") boolean updateSupport) throws Exception {
        ExcelUtil<QuestionImportDto> util = new ExcelUtil<QuestionImportDto>(QuestionImportDto.class);
        List<QuestionImportDto> rows = util.importExcel(file.getInputStream());
        return success(scExamManageService.importQuestions(rows, updateSupport, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @PostMapping("/question/importTemplate")
    public void importQuestionTemplate(HttpServletResponse response) {
        ExcelUtil<QuestionImportDto> util = new ExcelUtil<QuestionImportDto>(QuestionImportDto.class);
        util.importTemplateExcel(response, "题库导入模板");
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @PostMapping("/question/ai-generate")
    public AjaxResult aiGenerateQuestion(@RequestBody AiGenerateQuestionDto dto) {
        return success(scExamManageService.generateQuestionsByAi(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:edit')")
    @PostMapping("/question/ai-generate-task")
    public AjaxResult aiGenerateQuestionTask(@RequestBody AiGenerateQuestionDto dto) {
        return success(scExamManageService.submitGenerateQuestionsByAiTask(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/ai-task/list")
    public AjaxResult listAiGenerateQuestionTask(AiQuestionTaskQueryDto queryDto) {
        ScAiTaskLog query = new ScAiTaskLog();
        query.setBizType("exam_question_generate_async");
        query.setBizId(queryDto == null ? null : queryDto.getCourseId());
        query.setTaskStatus(queryDto == null ? null : queryDto.getTaskStatus());
        List<ScAiTaskLog> list = scAiTaskLogService.selectScAiTaskLogList(query)
                .stream()
                .limit(50)
                .collect(Collectors.toList());
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/ai-task/{taskId}")
    public AjaxResult getAiGenerateQuestionTask(@PathVariable Long taskId) {
        ScAiTaskLog taskLog = scAiTaskLogService.selectScAiTaskLogByTaskId(taskId);
        if (taskLog == null || !"exam_question_generate_async".equals(taskLog.getBizType())) {
            return error("AI 出题任务不存在");
        }
        return success(taskLog);
    }

    @GetMapping("/option/list")
    public TableDataInfo optionList(ScQuestionOption scQuestionOption) {
        startPage();
        return getDataTable(scQuestionOptionService.selectScQuestionOptionList(scQuestionOption));
    }

    @PostMapping("/option")
    public AjaxResult addOption(@Validated @RequestBody ScQuestionOption scQuestionOption) {
        scQuestionOption.setCreateBy(getUsername());
        return toAjax(scQuestionOptionService.insertScQuestionOption(scQuestionOption));
    }

    @PutMapping("/option")
    public AjaxResult editOption(@Validated @RequestBody ScQuestionOption scQuestionOption) {
        scQuestionOption.setUpdateBy(getUsername());
        return toAjax(scQuestionOptionService.updateScQuestionOption(scQuestionOption));
    }

    @DeleteMapping("/option/{optionIds}")
    public AjaxResult removeOption(@PathVariable Long[] optionIds) {
        return toAjax(scQuestionOptionService.deleteScQuestionOptionByOptionIds(optionIds));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paper/list")
    public TableDataInfo paperList(ScExamPaper scExamPaper) {
        startPage();
        return getDataTable(scExamPaperService.selectScExamPaperList(scExamPaper));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paper/{paperId}")
    public AjaxResult getPaperDetail(@PathVariable Long paperId) {
        ExamPaperDetailVo detailVo = scExamManageService.selectPaperDetail(paperId);
        return success(detailVo);
    }

    @PostMapping("/paper")
    public AjaxResult addPaper(@Validated @RequestBody ScExamPaper scExamPaper) {
        scExamPaper.setCreateBy(getUsername());
        return toAjax(scExamPaperService.insertScExamPaper(scExamPaper));
    }

    @PutMapping("/paper")
    public AjaxResult editPaper(@Validated @RequestBody ScExamPaper scExamPaper) {
        scExamPaper.setUpdateBy(getUsername());
        return toAjax(scExamPaperService.updateScExamPaper(scExamPaper));
    }

    @DeleteMapping("/paper/{paperIds}")
    public AjaxResult removePaper(@PathVariable Long[] paperIds) {
        return toAjax(scExamPaperService.deleteScExamPaperByPaperIds(paperIds));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "试卷管理", businessType = BusinessType.INSERT)
    @PostMapping("/paper/detail")
    public AjaxResult addPaperDetail(@RequestBody PaperUpsertDto dto) {
        return toAjax(scExamManageService.savePaper(dto, getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "试卷管理", businessType = BusinessType.UPDATE)
    @PutMapping("/paper/detail")
    public AjaxResult editPaperDetail(@RequestBody PaperUpsertDto dto) {
        return toAjax(scExamManageService.updatePaper(dto, getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "试卷管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/paper/detail/{paperIds}")
    public AjaxResult removePaperDetail(@PathVariable Long[] paperIds) {
        return toAjax(scExamManageService.deletePaperByIds(paperIds));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/paper/assemble")
    public AjaxResult assemblePaper(@RequestBody ExamPaperAssembleDto dto) {
        return success(scExamManageService.assemblePaper(dto, getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/strategy/list")
    public TableDataInfo strategyList(ScExamStrategyTemplate scExamStrategyTemplate) {
        startPage();
        return getDataTable(scExamStrategyTemplateService.selectScExamStrategyTemplateList(scExamStrategyTemplate));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/strategy/{templateId}")
    public AjaxResult getStrategy(@PathVariable Long templateId) {
        return success(scExamStrategyTemplateService.selectScExamStrategyTemplateByTemplateId(templateId));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "组卷策略模板", businessType = BusinessType.INSERT)
    @PostMapping("/strategy")
    public AjaxResult addStrategy(@RequestBody ScExamStrategyTemplate scExamStrategyTemplate) {
        scExamStrategyTemplate.setCreateBy(getUsername());
        return toAjax(scExamStrategyTemplateService.insertScExamStrategyTemplate(scExamStrategyTemplate));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "组卷策略模板", businessType = BusinessType.UPDATE)
    @PutMapping("/strategy")
    public AjaxResult editStrategy(@RequestBody ScExamStrategyTemplate scExamStrategyTemplate) {
        scExamStrategyTemplate.setUpdateBy(getUsername());
        return toAjax(scExamStrategyTemplateService.updateScExamStrategyTemplate(scExamStrategyTemplate));
    }

    @PreAuthorize("isAuthenticated()")
    @Log(title = "组卷策略模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/strategy/{templateIds}")
    public AjaxResult removeStrategy(@PathVariable Long[] templateIds) {
        return toAjax(scExamStrategyTemplateService.deleteScExamStrategyTemplateByTemplateIds(templateIds));
    }

    @PostMapping("/paperQuestion")
    public AjaxResult addPaperQuestion(@Validated @RequestBody ScExamPaperQuestion scExamPaperQuestion) {
        scExamPaperQuestion.setCreateBy(getUsername());
        return toAjax(scExamPaperQuestionService.insertScExamPaperQuestion(scExamPaperQuestion));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/list")
    public TableDataInfo recordList(ScExamRecord scExamRecord) {
        startPage();
        return getDataTable(scExamRecordService.selectScExamRecordList(scExamRecord));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/overview")
    public AjaxResult recordOverview(ScExamRecord scExamRecord) {
        return success(scExamAnalysisService.buildRecordOverview(scExamRecord));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/{recordId}")
    public AjaxResult recordDetail(@PathVariable Long recordId) {
        return success(scExamAnalysisService.buildRecordDetail(recordId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/runtime/{recordId}")
    public AjaxResult runtimeDetail(@PathVariable Long recordId) {
        return success(scExamAnalysisService.buildExamRuntime(recordId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/start")
    public AjaxResult startExam(Long paperId, Long userId) {
        return success(scExamRecordService.startExam(paperId, userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit/{recordId}")
    public AjaxResult submitExam(@PathVariable Long recordId, @RequestBody List<Map<String, Object>> answers) {
        return success(scExamRecordService.submitExam(recordId, answers));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit")
    public AjaxResult submitExamDto(@RequestBody ExamSubmitDto submitDto) {
        if (submitDto == null || submitDto.getRecordId() == null) {
            return error("考试记录不能为空");
        }
        List<Map<String, Object>> answers = submitDto.getAnswers() == null
                ? Collections.emptyList()
                : submitDto.getAnswers().stream()
                        .map(item -> Map.<String, Object>of("questionId", item.getQuestionId(), "userAnswer",
                                item.getUserAnswer()))
                        .collect(Collectors.toList());
        return success(scExamRecordService.submitExam(submitDto.getRecordId(), answers));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/draft/save")
    public AjaxResult saveDraft(@RequestBody ExamAnswerDraftSaveDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null) {
            return error("考试记录不存在");
        }
        return success(scExamRecordService.saveAnswerDraft(dto, record.getUserId(), getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/submit")
    public AjaxResult submitQuestion(@RequestBody ExamQuestionSubmitDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null) {
            return error("考试记录不存在");
        }
        return success(scExamRecordService.submitQuestionAnswer(dto, record.getUserId(), getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/sub-paper/submit")
    public AjaxResult submitSubPaper(@RequestBody ExamSubPaperSubmitDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null) {
            return error("考试记录不存在");
        }
        return success(scExamRecordService.submitSubPaper(dto, record.getUserId(), getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/draft/{recordId}")
    public AjaxResult listDrafts(@PathVariable Long recordId) {
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(recordId);
        if (record == null) {
            return error("考试记录不存在");
        }
        return success(scExamRecordService.listAnswerDrafts(recordId, record.getUserId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/answer/list")
    public TableDataInfo answerList(ScExamAnswer scExamAnswer) {
        startPage();
        return getDataTable(scExamAnswerService.selectScExamAnswerList(scExamAnswer));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/wrong/list")
    public TableDataInfo wrongList(ScWrongQuestionBook scWrongQuestionBook) {
        startPage();
        return getDataTable(scWrongQuestionBookService.selectScWrongQuestionBookList(scWrongQuestionBook));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/wrong/overview")
    public AjaxResult wrongOverview(ScWrongQuestionBook scWrongQuestionBook) {
        return success(scExamAnalysisService.buildWrongOverview(scWrongQuestionBook));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/wrong/{wrongId}")
    public AjaxResult wrongDetail(@PathVariable Long wrongId) {
        return success(scExamAnalysisService.buildWrongDetail(wrongId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/wrong/retry")
    public AjaxResult wrongRetry(@RequestBody WrongQuestionRetryDto dto) {
        return success(scExamAnalysisService.buildWrongRetryPaper(dto, getUsername()));
    }
}
