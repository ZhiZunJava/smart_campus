package com.smart.web.controller.campus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.domain.campusvo.QuestionDetailVo;
import com.smart.system.domain.campusvo.QuestionOptionVo;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.dto.ExamAnswerDraftSaveDto;
import com.smart.system.domain.dto.ExamQuestionSubmitDto;
import com.smart.system.domain.dto.ExamSubPaperSubmitDto;
import com.smart.system.domain.dto.ExamSubmitDto;
import com.smart.system.domain.dto.WrongQuestionRetryDto;
import com.smart.system.service.IScExamAnalysisService;
import com.smart.system.service.IScExamManageService;
import com.smart.system.service.IScExamPaperService;
import com.smart.system.service.IScExamRecordService;
import com.smart.system.service.IScQuestionBankService;
import com.smart.system.service.IScWrongQuestionBookService;

/**
 * 门户端考试 / 错题接口
 */
@RestController
@RequestMapping("/campus/portal/exam")
public class CampusPortalExamController extends BaseController {
    @Autowired
    private IScExamPaperService scExamPaperService;

    @Autowired
    private IScExamManageService scExamManageService;

    @Autowired
    private IScExamRecordService scExamRecordService;

    @Autowired
    private IScExamAnalysisService scExamAnalysisService;

    @Autowired
    private IScWrongQuestionBookService scWrongQuestionBookService;

    @Autowired
    private IScQuestionBankService scQuestionBankService;

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/paper/list")
    public AjaxResult paperList(@RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        ScExamPaper query = new ScExamPaper();
        query.setCourseId(courseId);
        query.setStatus("0");
        List<ScExamPaper> all = scExamPaperService.selectScExamPaperList(query);
        Date now = new Date();
        List<ScExamPaper> filtered = all.stream()
                .filter(item -> "0".equals(item.getStatus()))
                .filter(item -> !"archived".equalsIgnoreCase(item.getPublishStatus()))
                .filter(item -> !"draft".equalsIgnoreCase(item.getPublishStatus()))
                .filter(item -> item.getPublishStartTime() == null || !now.before(item.getPublishStartTime()))
                .filter(item -> item.getPublishEndTime() == null || !now.after(item.getPublishEndTime()))
                .collect(Collectors.toList());
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(filtered.size(), fromIndex + pageSize);
        List<ScExamPaper> rows = fromIndex >= filtered.size() ? new ArrayList<>() : filtered.subList(fromIndex, toIndex);
        return AjaxResult.success().put("rows", rows).put("total", filtered.size());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/paper/{paperId}")
    public AjaxResult paperDetail(@PathVariable Long paperId) {
        ensurePortalPaperAccessible(paperId);
        ExamPaperDetailVo detailVo = scExamManageService.selectPaperDetail(paperId);
        if (detailVo == null) {
            return AjaxResult.error("试卷不存在");
        }
        return AjaxResult.success(maskPaperDetail(detailVo));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/question/list")
    public AjaxResult questionList(@RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "50") Integer pageSize) {
        ScQuestionBank query = new ScQuestionBank();
        query.setCourseId(courseId);
        query.setStatus("0");
        List<ScQuestionBank> all = scQuestionBankService.selectScQuestionBankList(query);
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(all.size(), fromIndex + pageSize);
        List<ScQuestionBank> rows = fromIndex >= all.size() ? new ArrayList<>() : all.subList(fromIndex, toIndex);
        return AjaxResult.success().put("rows", rows.stream().map(this::maskQuestionBank).collect(Collectors.toList())).put("total", all.size());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/record/list")
    public AjaxResult recordList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(getUserId());
        List<ScExamRecord> all = scExamRecordService.selectScExamRecordList(query);
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(all.size(), fromIndex + pageSize);
        List<ScExamRecord> rows = fromIndex >= all.size() ? new ArrayList<>() : all.subList(fromIndex, toIndex);
        return AjaxResult.success().put("rows", rows).put("total", all.size());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/record/overview")
    public AjaxResult recordOverview() {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(getUserId());
        return AjaxResult.success(scExamAnalysisService.buildRecordOverview(query));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/record/{recordId}")
    public AjaxResult recordDetail(@PathVariable Long recordId) {
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(recordId);
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权查看该考试记录");
        }
        return AjaxResult.success(scExamAnalysisService.buildRecordDetail(recordId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/runtime/{recordId}")
    public AjaxResult runtimeDetail(@PathVariable Long recordId) {
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(recordId);
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权查看该考试运行态");
        }
        return AjaxResult.success(scExamAnalysisService.buildExamRuntime(recordId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/wrong/list")
    public AjaxResult wrongList(@RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "50") Integer pageSize) {
        ScWrongQuestionBook query = new ScWrongQuestionBook();
        query.setUserId(getUserId());
        query.setCourseId(courseId);
        List<ScWrongQuestionBook> all = scWrongQuestionBookService.selectScWrongQuestionBookList(query);
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(all.size(), fromIndex + pageSize);
        List<ScWrongQuestionBook> rows = fromIndex >= all.size() ? new ArrayList<>() : all.subList(fromIndex, toIndex);
        return AjaxResult.success().put("rows", rows).put("total", all.size());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/wrong/overview")
    public AjaxResult wrongOverview() {
        ScWrongQuestionBook query = new ScWrongQuestionBook();
        query.setUserId(getUserId());
        return AjaxResult.success(scExamAnalysisService.buildWrongOverview(query));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/wrong/{wrongId}")
    public AjaxResult wrongDetail(@PathVariable Long wrongId) {
        ScWrongQuestionBook wrong = scWrongQuestionBookService.selectScWrongQuestionBookById(wrongId);
        if (wrong == null || !getUserId().equals(wrong.getUserId())) {
            return AjaxResult.error("无权查看该错题");
        }
        return AjaxResult.success(scExamAnalysisService.buildWrongDetail(wrongId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/wrong/retry")
    public AjaxResult wrongRetry(@RequestBody WrongQuestionRetryDto dto) {
        dto.setUserId(getUserId());
        return AjaxResult.success(scExamAnalysisService.buildWrongRetryPaper(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/start")
    public AjaxResult startExam(@RequestParam Long paperId) {
        ensurePortalPaperAccessible(paperId);
        return AjaxResult.success(scExamRecordService.startExam(paperId, getUserId()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/submit")
    public AjaxResult submitExam(@RequestBody ExamSubmitDto submitDto) {
        if (submitDto == null || submitDto.getRecordId() == null) {
            return AjaxResult.error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(submitDto.getRecordId());
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权提交该考试");
        }
        List<Map<String, Object>> answers = submitDto.getAnswers() == null
                ? new ArrayList<>()
                : submitDto.getAnswers().stream()
                        .map(item -> Map.<String, Object>of("questionId", item.getQuestionId(), "userAnswer",
                                StringUtils.defaultIfEmpty(item.getUserAnswer(), "")))
                        .collect(Collectors.toList());
        return AjaxResult.success(scExamRecordService.submitExam(submitDto.getRecordId(), answers,
                submitDto.getFocusLossCount(), submitDto.getFlaggedCount()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/draft/save")
    public AjaxResult saveDraft(@RequestBody ExamAnswerDraftSaveDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return AjaxResult.error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权保存该考试草稿");
        }
        return AjaxResult.success(scExamRecordService.saveAnswerDraft(dto, getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/question/submit")
    public AjaxResult submitQuestion(@RequestBody ExamQuestionSubmitDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return AjaxResult.error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权提交该题目");
        }
        return AjaxResult.success(scExamRecordService.submitQuestionAnswer(dto, getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/sub-paper/submit")
    public AjaxResult submitSubPaper(@RequestBody ExamSubPaperSubmitDto dto) {
        if (dto == null || dto.getRecordId() == null) {
            return AjaxResult.error("考试记录不能为空");
        }
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(dto.getRecordId());
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权提交该子试卷");
        }
        return AjaxResult.success(scExamRecordService.submitSubPaper(dto, getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/draft/{recordId}")
    public AjaxResult listDrafts(@PathVariable Long recordId) {
        ScExamRecord record = scExamRecordService.selectScExamRecordByRecordId(recordId);
        if (record == null || !getUserId().equals(record.getUserId())) {
            return AjaxResult.error("无权查看该考试草稿");
        }
        return AjaxResult.success(scExamRecordService.listAnswerDrafts(recordId, getUserId()));
    }

    private void ensurePortalPaperAccessible(Long paperId) {
        ScExamPaper paper = scExamPaperService.selectScExamPaperByPaperId(paperId);
        if (paper == null || !"0".equals(paper.getStatus())) {
            throw new ServiceException("试卷不存在或已停用");
        }
        if (hasUserExamRecord(paperId)) {
            return;
        }
        if ("draft".equalsIgnoreCase(paper.getPublishStatus()) || "archived".equalsIgnoreCase(paper.getPublishStatus())) {
            throw new ServiceException("当前试卷暂未对学生开放查看");
        }
        Date now = new Date();
        if (paper.getPublishStartTime() != null && now.before(paper.getPublishStartTime())) {
            throw new ServiceException("试卷尚未到开放时间");
        }
        if (paper.getPublishEndTime() != null && now.after(paper.getPublishEndTime())) {
            throw new ServiceException("试卷已过开放时间");
        }
    }

    private boolean hasUserExamRecord(Long paperId) {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(getUserId());
        query.setPaperId(paperId);
        List<ScExamRecord> records = scExamRecordService.selectScExamRecordList(query);
        return records != null && !records.isEmpty();
    }

    private ScQuestionBank maskQuestionBank(ScQuestionBank source) {
        if (source == null) {
            return null;
        }
        ScQuestionBank target = new ScQuestionBank();
        target.setQuestionId(source.getQuestionId());
        target.setCatalogId(source.getCatalogId());
        target.setCourseId(source.getCourseId());
        target.setChapterId(source.getChapterId());
        target.setKnowledgePointId(source.getKnowledgePointId());
        target.setQuestionType(source.getQuestionType());
        target.setDifficultyLevel(source.getDifficultyLevel());
        target.setStem(maskStem(source.getStem(), source.getQuestionType()));
        target.setSource(source.getSource());
        target.setQuestionTags(source.getQuestionTags());
        target.setMaterialContent(source.getMaterialContent());
        target.setSourceBatchNo(source.getSourceBatchNo());
        target.setQualityScore(source.getQualityScore());
        target.setStatus(source.getStatus());
        return target;
    }

    private ExamPaperDetailVo maskPaperDetail(ExamPaperDetailVo source) {
        if (source == null) {
            return null;
        }
        if (source.getQuestions() != null) {
            source.getQuestions().forEach(item -> {
                if (item != null && item.getQuestion() != null) {
                    item.setQuestion(maskQuestionDetail(item.getQuestion()));
                }
            });
        }
        if (source.getSubPapers() != null) {
            source.setSubPapers(source.getSubPapers().stream().map(this::maskPaperDetail).collect(Collectors.toList()));
        }
        return source;
    }

    private QuestionDetailVo maskQuestionDetail(QuestionDetailVo source) {
        if (source == null) {
            return null;
        }
        QuestionDetailVo target = new QuestionDetailVo();
        target.setQuestionId(source.getQuestionId());
        target.setCourseId(source.getCourseId());
        target.setChapterId(source.getChapterId());
        target.setKnowledgePointId(source.getKnowledgePointId());
        target.setQuestionType(source.getQuestionType());
        target.setDifficultyLevel(source.getDifficultyLevel());
        target.setStem(maskStem(source.getStem(), source.getQuestionType()));
        target.setSource(source.getSource());
        target.setQuestionTags(source.getQuestionTags());
        target.setMaterialContent(source.getMaterialContent());
        target.setSourceBatchNo(source.getSourceBatchNo());
        target.setQualityScore(source.getQualityScore());
        target.setStatus(source.getStatus());
        if (source.getOptions() != null) {
            List<QuestionOptionVo> options = source.getOptions().stream().map(this::maskQuestionOption).collect(Collectors.toList());
            target.setOptions(options);
        }
        return target;
    }

    private QuestionOptionVo maskQuestionOption(QuestionOptionVo source) {
        if (source == null) {
            return null;
        }
        QuestionOptionVo target = new QuestionOptionVo();
        target.setOptionId(source.getOptionId());
        target.setOptionKey(source.getOptionKey());
        target.setOptionContent(source.getOptionContent());
        return target;
    }

    private String maskStem(String stem, String questionType) {
        if (StringUtils.isEmpty(stem)) {
            return stem;
        }
        String plain = stem.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        String type = StringUtils.defaultIfEmpty(questionType, "").trim().toLowerCase();
        if ("essay".equals(type) || "material".equals(type) || "case".equals(type)) {
            if (plain.length() <= 10) {
                return "题干已脱敏";
            }
            return plain.substring(0, Math.min(8, plain.length())) + " ……（题干已脱敏）";
        }
        if (plain.length() <= 12) {
            return plain.length() <= 4 ? "题干已脱敏" : plain.substring(0, 2) + " ...";
        }
        int prefix = Math.min(10, Math.max(4, plain.length() / 5));
        int suffix = Math.min(6, Math.max(2, plain.length() / 8));
        if (prefix + suffix >= plain.length()) {
            return plain.substring(0, Math.min(4, plain.length())) + " ...";
        }
        return plain.substring(0, prefix) + " ...... " + plain.substring(plain.length() - suffix);
    }
}
