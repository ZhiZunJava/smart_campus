package com.smart.web.controller.campus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.*;
import com.smart.system.domain.dto.ExamSubmitDto;
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

    @PreAuthorize("@ss.hasPermi('campus:exam:question:list')")
    @GetMapping("/question/list")
    public TableDataInfo questionList(ScQuestionBank scQuestionBank) {
        startPage();
        return getDataTable(scQuestionBankService.selectScQuestionBankList(scQuestionBank));
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
}
