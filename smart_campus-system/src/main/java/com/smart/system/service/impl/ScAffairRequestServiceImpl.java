package com.smart.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.common.core.domain.entity.SysDept;
import com.smart.common.core.domain.entity.SysRole;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.DateUtils;
import com.smart.common.utils.SecurityUtils;
import com.smart.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.system.domain.ScAffairAction;
import com.smart.system.domain.ScAffairCategory;
import com.smart.system.domain.ScAffairRequest;
import com.smart.system.domain.ScAffairTemplate;
import com.smart.system.domain.ScAffairWorkStudyJob;
import com.smart.system.domain.ScClass;
import com.smart.system.domain.ScGrade;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.domain.ScStudentStatusLog;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScTextbook;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.AffairBatchReviewDto;
import com.smart.system.domain.dto.AffairRequestReviewDto;
import com.smart.system.domain.dto.AffairRequestSubmitDto;
import com.smart.system.mapper.ScAffairActionMapper;
import com.smart.system.mapper.ScAffairRequestMapper;
import com.smart.system.mapper.ScAffairSpecialRecordMapper;
import com.smart.system.mapper.ScStudentStatusLogMapper;
import com.smart.system.mapper.SysDeptMapper;
import com.smart.system.mapper.SysUserMapper;
import com.smart.system.service.IScAffairCategoryService;
import com.smart.system.service.IScAffairRequestService;
import com.smart.system.service.IScAffairWorkStudyJobService;
import com.smart.system.service.IScSchoolTermService;
import com.smart.system.service.IScAffairTemplateService;
import com.smart.system.service.IScTextbookPlanService;
import com.smart.system.service.IScTextbookService;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScClassService;
import com.smart.system.service.IScGradeService;
import com.smart.system.service.IScUserProfileService;
import com.smart.system.service.ISysDeptService;
import com.smart.system.service.ISysUserService;

@Service
public class ScAffairRequestServiceImpl implements IScAffairRequestService {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final Set<String> REVIEW_ACTIONS = new LinkedHashSet<>(List.of("APPROVE", "REJECT"));

    @Autowired
    private ScAffairRequestMapper scAffairRequestMapper;
    @Autowired
    private ScAffairActionMapper scAffairActionMapper;
    @Autowired
    private ScStudentStatusLogMapper scStudentStatusLogMapper;
    @Autowired
    private ScAffairSpecialRecordMapper scAffairSpecialRecordMapper;
    @Autowired
    private IScAffairTemplateService scAffairTemplateService;
    @Autowired
    private IScAffairCategoryService scAffairCategoryService;
    @Autowired
    private IScUserProfileService scUserProfileService;
    @Autowired
    private IScSchoolTermService scSchoolTermService;
    @Autowired
    private IScAffairWorkStudyJobService scAffairWorkStudyJobService;
    @Autowired
    private IScGradeService scGradeService;
    @Autowired
    private IScClassService scClassService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IScTextbookPlanService scTextbookPlanService;
    @Autowired
    private IScTextbookService scTextbookService;
    @Autowired
    private IScClassCourseService scClassCourseService;

    @Override
    public ScAffairRequest selectScAffairRequestByRequestId(Long requestId) {
        return scAffairRequestMapper.selectScAffairRequestByRequestId(requestId);
    }

    @Override
    public List<ScAffairRequest> selectScAffairRequestList(ScAffairRequest scAffairRequest) {
        return scAffairRequestMapper.selectScAffairRequestList(scAffairRequest);
    }

    @Override
    public Map<String, Object> selectRequestDetail(Long requestId, LoginUser loginUser) {
        ScAffairRequest request = requireRequest(requestId);
        if (!canViewRequest(loginUser, request)) {
            throw new ServiceException("无权查看该事务申请");
        }
        ScAffairTemplate template = scAffairTemplateService.selectScAffairTemplateByTemplateId(request.getTemplateId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("request", request);
        result.put("template", template);
        result.put("formFields", template == null ? Collections.emptyList() : parseArray(template.getFormSchemaJson()));
        result.put("workflowSteps", template == null ? Collections.emptyList() : parseArray(template.getWorkflowSchemaJson()));
        result.put("formData", parseObjectMap(request.getFormDataJson()));
        result.put("attachments", parseArray(request.getAttachmentJson()));
        result.put("actions", scAffairActionMapper.selectScAffairActionListByRequestId(requestId));
        result.put("specializedRecord", loadSpecializedRecord(request, template));
        result.put("canReview", canReviewCurrentStep(loginUser, request));
        result.put("canCancel", canCancelRequest(loginUser, request, template));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitRequest(AffairRequestSubmitDto dto, LoginUser loginUser, String portalRole) {
        if (dto == null || dto.getTemplateId() == null) {
            throw new ServiceException("事务模板不能为空");
        }
        requirePortalRole(loginUser, portalRole);
        ScAffairTemplate template = requireTemplate(dto.getTemplateId());
        if (!supportsPortalRole(template, portalRole) && !isAdmin(loginUser)) {
            throw new ServiceException("当前模板不支持在该端发起");
        }
        Map<String, Object> formData = parseObjectMap(dto.getFormDataJson());
        List<Map<String, Object>> formFields = parseArray(template.getFormSchemaJson());
        List<Map<String, Object>> workflowSteps = parseArray(template.getWorkflowSchemaJson());
        validateFormData(formFields, formData);
        validateSubmissionRules(template, loginUser, formData);
        if (workflowSteps.isEmpty()) {
            throw new ServiceException("模板尚未配置审核流程");
        }

        ScAffairCategory category = scAffairCategoryService.selectScAffairCategoryByCategoryId(template.getCategoryId());
        ScAffairRequest request = new ScAffairRequest();
        populateApplicantSnapshot(request, loginUser);
        request.setRequestNo(buildRequestNo());
        request.setCategoryId(template.getCategoryId());
        request.setCategoryCode(template.getCategoryCode());
        request.setCategoryName(category == null ? template.getCategoryName() : category.getCategoryName());
        request.setTemplateId(template.getTemplateId());
        request.setTemplateCode(template.getTemplateCode());
        request.setTemplateName(template.getTemplateName());
        request.setBusinessCode(template.getBusinessCode());
        request.setBusinessName(template.getBusinessName());
        request.setTitle(resolveTitle(dto.getTitle(), template, request));
        request.setSummaryText(buildSummaryText(formFields, formData));
        request.setRequestStatus(STATUS_PENDING);
        request.setSubmittedTime(new Date());
        request.setLastAction("SUBMIT");
        request.setTargetStudentStatusCode(StringUtils.defaultString(template.getTargetStatusCode()));
        request.setTargetStudentStatusName(StringUtils.defaultString(template.getTargetStatusName()));
        request.setFormDataJson(writeJson(formData));
        request.setAttachmentJson(StringUtils.defaultIfEmpty(dto.getAttachmentJson(), "[]"));
        request.setCreateBy(loginUser.getUsername());
        applyCurrentStep(request, workflowSteps, 0, formData);
        scAffairRequestMapper.insertScAffairRequest(request);
        recordAction(request, loginUser, "SUBMIT", "SUBMIT", "已提交申请");
        persistSpecializedRecord(request, template, formData);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getRequestId());
        result.put("requestNo", request.getRequestNo());
        result.put("requestStatus", request.getRequestStatus());
        result.put("currentStepName", request.getCurrentStepName());
        result.put("message", "事务申请已提交");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelRequest(Long requestId, LoginUser loginUser) {
        ScAffairRequest request = requireRequest(requestId);
        ScAffairTemplate template = requireTemplate(request.getTemplateId());
        if (!canCancelRequest(loginUser, request, template)) {
            throw new ServiceException("当前申请不允许撤回");
        }
        request.setRequestStatus(STATUS_CANCELLED);
        request.setFinishTime(new Date());
        request.setLastAction("CANCEL");
        request.setUpdateBy(loginUser.getUsername());
        scAffairRequestMapper.updateScAffairRequest(request);
        recordAction(request, loginUser, "CANCEL", "CANCEL", "申请已撤回");
        persistSpecializedRecord(request, template, parseObjectMap(request.getFormDataJson()));
        return buildReviewResult(request, "申请已撤回");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reviewRequest(AffairRequestReviewDto dto, LoginUser loginUser) {
        if (dto == null || dto.getRequestId() == null) {
            throw new ServiceException("申请不能为空");
        }
        String actionType = StringUtils.upperCase(StringUtils.trimToEmpty(dto.getActionType()));
        if (!REVIEW_ACTIONS.contains(actionType)) {
            throw new ServiceException("审核动作仅支持 APPROVE 或 REJECT");
        }
        ScAffairRequest request = requireRequest(dto.getRequestId());
        if (!STATUS_PENDING.equals(request.getRequestStatus())) {
            throw new ServiceException("当前申请已处理");
        }
        if (!canReviewCurrentStep(loginUser, request)) {
            throw new ServiceException("无权审核当前申请");
        }
        ScAffairTemplate template = requireTemplate(request.getTemplateId());
        List<Map<String, Object>> workflowSteps = parseArray(template.getWorkflowSchemaJson());
        Map<String, Object> formData = parseObjectMap(request.getFormDataJson());

        if ("REJECT".equals(actionType)) {
            request.setRequestStatus(STATUS_REJECTED);
            request.setFinishTime(new Date());
            request.setLastAction("REJECT");
            request.setUpdateBy(loginUser.getUsername());
            request.setCurrentStepName("已驳回");
            scAffairRequestMapper.updateScAffairRequest(request);
            recordAction(request, loginUser, "REVIEW", "REJECT", dto.getCommentText());
            persistSpecializedRecord(request, template, formData);
            return buildReviewResult(request, "申请已驳回");
        }

        int nextStepIndex = request.getCurrentStepIndex() == null ? 1 : request.getCurrentStepIndex() + 1;
        if (nextStepIndex < workflowSteps.size()) {
            applyCurrentStep(request, workflowSteps, nextStepIndex, formData);
            request.setLastAction("APPROVE");
            request.setUpdateBy(loginUser.getUsername());
            scAffairRequestMapper.updateScAffairRequest(request);
            recordAction(request, loginUser, "REVIEW", "APPROVE", dto.getCommentText());
            persistSpecializedRecord(request, template, formData);
            return buildReviewResult(request, "审核通过，已流转至下一节点");
        }

        request.setRequestStatus(STATUS_APPROVED);
        request.setFinishTime(new Date());
        request.setLastAction("APPROVE");
        request.setCurrentStepCode("");
        request.setCurrentStepName("已办结");
        request.setCurrentAssignmentType("");
        request.setCurrentReviewerRole("");
        request.setCurrentReviewerUserId(null);
        request.setCurrentReviewerName("");
        request.setUpdateBy(loginUser.getUsername());
        scAffairRequestMapper.updateScAffairRequest(request);
        recordAction(request, loginUser, "REVIEW", "APPROVE", dto.getCommentText());
        persistSpecializedRecord(request, template, formData);
        applyApprovalSideEffect(request, template, formData, loginUser);
        return buildReviewResult(request, "申请已审核通过");
    }

    @Override
    public List<Map<String, Object>> buildServiceCatalog(String portalRole, LoginUser loginUser) {
        List<ScAffairTemplate> templates = filterAccessibleTemplates(
                scAffairTemplateService.selectEnabledTemplatesByRole(portalRole), loginUser, portalRole);
        Map<Long, List<ScAffairTemplate>> templateGroup = templates.stream()
                .collect(Collectors.groupingBy(ScAffairTemplate::getCategoryId, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<ScAffairTemplate> templateList : templateGroup.values()) {
            if (templateList.isEmpty()) {
                continue;
            }
            ScAffairTemplate first = templateList.get(0);
            ScAffairCategory category = scAffairCategoryService.selectScAffairCategoryByCategoryId(first.getCategoryId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("categoryId", first.getCategoryId());
            item.put("categoryCode", first.getCategoryCode());
            item.put("categoryName", first.getCategoryName());
            item.put("icon", category == null ? "ri-service-line" : category.getIcon());
            item.put("serviceGroup", category == null ? "" : category.getServiceGroup());
            item.put("templates", templateList);
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> buildPortalDashboard(LoginUser loginUser, String portalRole) {
        requirePortalRole(loginUser, portalRole);
        List<ScAffairRequest> myRequests = listMyRequests(loginUser, portalRole);
        List<ScAffairRequest> pendingReviews = listPendingReviews(loginUser, portalRole);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("myRequestCount", myRequests.size());
        result.put("pendingCount", myRequests.stream().filter(item -> STATUS_PENDING.equals(item.getRequestStatus())).count());
        result.put("approvedCount", myRequests.stream().filter(item -> STATUS_APPROVED.equals(item.getRequestStatus())).count());
        result.put("rejectedCount", myRequests.stream().filter(item -> STATUS_REJECTED.equals(item.getRequestStatus())).count());
        result.put("cancelledCount", myRequests.stream().filter(item -> STATUS_CANCELLED.equals(item.getRequestStatus())).count());
        result.put("reviewTodoCount", pendingReviews.size());
        result.put("recentRequests", myRequests.stream().limit(5).collect(Collectors.toList()));
        result.put("serviceCount", buildServiceCatalog(portalRole, loginUser).stream()
                .mapToInt(item -> ((List<?>) item.get("templates")).size()).sum());
        result.put("frequentTemplates", scAffairRequestMapper.selectFrequentTemplates(loginUser.getUserId(), 6));
        return result;
    }

    @Override
    public List<ScAffairRequest> listMyRequests(LoginUser loginUser, String portalRole) {
        requirePortalRole(loginUser, portalRole);
        ScAffairRequest query = new ScAffairRequest();
        query.setApplicantUserId(loginUser.getUserId());
        return scAffairRequestMapper.selectScAffairRequestList(query);
    }

    @Override
    public List<ScAffairRequest> listPendingReviews(LoginUser loginUser, String portalRole) {
        requirePortalRole(loginUser, portalRole);
        if (isAdmin(loginUser)) {
            ScAffairRequest query = new ScAffairRequest();
            query.setRequestStatus(STATUS_PENDING);
            return scAffairRequestMapper.selectScAffairRequestList(query);
        }
        List<String> reviewerRoles = resolveReviewerRolesForPortal(loginUser, portalRole);
        return scAffairRequestMapper.selectPendingReviewsByReviewer(loginUser.getUserId(), reviewerRoles);
    }

    @Override
    public Map<String, Object> buildPortalOptions() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("teacherOptions", buildTeacherOptions());
        result.put("advisorOptions", buildAdvisorOptions());
        result.put("studentOptions", buildStudentOptions());
        result.put("termOptions", buildTermOptions());
        result.put("deptOptions", buildDeptOptions());
        result.put("gradeOptions", buildGradeOptions());
        result.put("classOptions", buildClassOptions());
        result.put("studentStatusOptions", buildStudentStatusOptions());
        result.put("leaveOptions", buildLeaveOptions());
        result.put("myCoursesOptions", buildMyCoursesOptions());
        result.put("myLeaveReturnRequestsOptions", buildMyLeaveReturnRequestsOptions());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchReviewRequests(AffairBatchReviewDto dto, LoginUser loginUser) {
        if (dto == null || dto.getRequestIds() == null || dto.getRequestIds().isEmpty()) {
            throw new ServiceException("申请列表不能为空");
        }
        String actionType = StringUtils.upperCase(StringUtils.trimToEmpty(dto.getActionType()));
        if (!REVIEW_ACTIONS.contains(actionType)) {
            throw new ServiceException("审核动作仅支持 APPROVE 或 REJECT");
        }
        int successCount = 0;
        int skipCount = 0;
        List<String> errors = new ArrayList<>();
        for (Long requestId : dto.getRequestIds()) {
            try {
                AffairRequestReviewDto reviewDto = new AffairRequestReviewDto();
                reviewDto.setRequestId(requestId);
                reviewDto.setActionType(actionType);
                reviewDto.setCommentText(dto.getCommentText());
                reviewRequest(reviewDto, loginUser);
                successCount++;
            } catch (ServiceException e) {
                skipCount++;
                errors.add("申请#" + requestId + ": " + e.getMessage());
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("successCount", successCount);
        result.put("skipCount", skipCount);
        result.put("errors", errors);
        result.put("message", "批量审核完成，成功" + successCount + "条，跳过" + skipCount + "条");
        return result;
    }

    @Override
    public Map<String, Object> buildAffairStatistics(LoginUser loginUser, String portalRole) {
        requirePortalRole(loginUser, portalRole);
        Long applicantUserId = isAdmin(loginUser) ? null : loginUser.getUserId();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("byCategory", scAffairRequestMapper.selectStatisticsByCategory(applicantUserId));
        result.put("byStatus", scAffairRequestMapper.selectStatisticsByStatus(applicantUserId));
        result.put("dailyTrend7", scAffairRequestMapper.selectDailyTrend(applicantUserId, 7));
        result.put("dailyTrend30", scAffairRequestMapper.selectDailyTrend(applicantUserId, 30));
        return result;
    }

    @Override
    public Map<String, Object> buildCategoryDetail(LoginUser loginUser, String portalRole, String categoryCode) {
        requirePortalRole(loginUser, portalRole);
        ScAffairCategory category = scAffairCategoryService.selectScAffairCategoryByCategoryCode(categoryCode);
        if (category == null) {
            throw new ServiceException("事务分类不存在");
        }
        List<ScAffairTemplate> templates = filterAccessibleTemplates(
                scAffairTemplateService.selectEnabledTemplatesByRole(portalRole), loginUser, portalRole).stream()
                .filter(t -> Objects.equals(t.getCategoryId(), category.getCategoryId()))
                .collect(Collectors.toList());
        List<ScAffairRequest> myRequests = scAffairRequestMapper.selectRequestsByCategoryCode(loginUser.getUserId(), categoryCode);
        long pendingCount = myRequests.stream().filter(r -> STATUS_PENDING.equals(r.getRequestStatus())).count();
        long approvedCount = myRequests.stream().filter(r -> STATUS_APPROVED.equals(r.getRequestStatus())).count();
        long rejectedCount = myRequests.stream().filter(r -> STATUS_REJECTED.equals(r.getRequestStatus())).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("category", category);
        result.put("templates", templates);
        result.put("myRequests", myRequests);
        result.put("stats", Map.of(
                "total", myRequests.size(),
                "pending", pendingCount,
                "approved", approvedCount,
                "rejected", rejectedCount
        ));
        result.put("templateStats", buildTemplateStats(templates, myRequests));

        if ("ASK_LEAVE".equals(categoryCode)) {
            result.put("specializedData", buildAskLeaveCategoryData(loginUser.getUserId()));
        }

        if ("LEAVE_RETURN_SCHOOL".equals(categoryCode)) {
            result.put("specializedData", buildLeaveReturnCategoryData(loginUser.getUserId()));
        }

        if ("TEXTBOOK".equals(categoryCode)) {
            result.put("specializedData", buildTextbookCategoryData(loginUser.getUserId()));
        }

        if ("ACADEMIC_STATUS".equals(categoryCode)) {
            result.put("specializedData", buildAcademicStatusCategoryData(loginUser.getUserId()));
        }

        if ("ACADEMIC_STATUS".equals(categoryCode)) {
            ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId());
            if (profile != null) {
                Map<String, Object> statusInfo = new LinkedHashMap<>();
                statusInfo.put("currentStatusCode", StringUtils.defaultIfEmpty(profile.getStudentStatusCode(), "IN_SCHOOL"));
                statusInfo.put("currentStatusName", StringUtils.defaultIfEmpty(profile.getStudentStatusName(), "在籍在校"));
                statusInfo.put("updateTime", profile.getStudentStatusUpdateTime());
                result.put("studentStatus", statusInfo);
            }
        }

        if ("student".equals(portalRole)) {
            Map<String, Object> advisorInfo = buildAdvisorInfo(loginUser.getUserId());
            if (!advisorInfo.isEmpty()) {
                result.put("advisorInfo", advisorInfo);
            }
        }

        return result;
    }

    @Override
    public List<ScAffairRequest> listRecentActivity(LoginUser loginUser, String portalRole, int limit) {
        requirePortalRole(loginUser, portalRole);
        return scAffairRequestMapper.selectRecentActivityByUser(loginUser.getUserId(), limit > 0 ? limit : 5);
    }

    @Override
    public List<Map<String, Object>> listFrequentTemplates(LoginUser loginUser, String portalRole, int limit) {
        requirePortalRole(loginUser, portalRole);
        return scAffairRequestMapper.selectFrequentTemplates(loginUser.getUserId(), limit > 0 ? limit : 6);
    }

    @Override
    public List<ScAffairRequest> exportRequestList(ScAffairRequest query) {
        return scAffairRequestMapper.selectScAffairRequestList(query);
    }

    private void populateApplicantSnapshot(ScAffairRequest request, LoginUser loginUser) {
        SysUser user = loginUser.getUser();
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId());
        request.setApplicantUserId(loginUser.getUserId());
        request.setApplicantName(resolveUserDisplayName(user, profile));
        request.setApplicantUserType(resolveApplicantUserType(user, profile));
        request.setApplicantNo(resolveApplicantNo(user, profile));
        request.setApplicantDeptId(loginUser.getDeptId());
        request.setApplicantDeptName(resolveDeptName(loginUser.getDeptId()));
        if (profile != null) {
            request.setMajor(StringUtils.defaultString(profile.getMajor()));
            if (profile.getGradeId() != null) {
                request.setGradeId(profile.getGradeId());
                ScGrade grade = scGradeService.selectScGradeByGradeId(profile.getGradeId());
                request.setGradeName(grade == null ? "" : grade.getGradeName());
            }
            if (profile.getClassId() != null) {
                request.setClassId(profile.getClassId());
                ScClass scClass = scClassService.selectScClassByClassId(profile.getClassId());
                request.setClassName(scClass == null ? "" : scClass.getClassName());
            }
            request.setCurrentStudentStatusCode(StringUtils.defaultIfEmpty(profile.getStudentStatusCode(), "IN_SCHOOL"));
            request.setCurrentStudentStatusName(StringUtils.defaultIfEmpty(profile.getStudentStatusName(), "在籍在校"));
        } else {
            request.setCurrentStudentStatusCode("");
            request.setCurrentStudentStatusName("");
        }
    }

    private void applyCurrentStep(ScAffairRequest request, List<Map<String, Object>> workflowSteps, int stepIndex, Map<String, Object> formData) {
        if (stepIndex < 0 || stepIndex >= workflowSteps.size()) {
            throw new ServiceException("模板流程配置异常");
        }
        Map<String, Object> currentStep = workflowSteps.get(stepIndex);
        String assignmentType = StringUtils.upperCase(getString(currentStep, "assignmentType"));
        if (StringUtils.isEmpty(assignmentType)) {
            assignmentType = "ROLE";
        }
        request.setCurrentStepIndex(stepIndex);
        request.setCurrentStepCode(getString(currentStep, "stepCode"));
        request.setCurrentStepName(getString(currentStep, "stepName"));
        request.setCurrentAssignmentType(assignmentType);
        request.setCurrentReviewerRole("");
        request.setCurrentReviewerUserId(null);
        request.setCurrentReviewerName("");
        if ("ROLE".equals(assignmentType)) {
            String reviewerRole = StringUtils.lowerCase(getString(currentStep, "reviewerRole"));
            request.setCurrentReviewerRole(reviewerRole);
            if ("advisor".equals(reviewerRole)) {
                Long advisorUserId = resolveAdvisorReviewerUserId(request.getApplicantUserId());
                if (advisorUserId != null) {
                    applyReviewerUser(request, advisorUserId);
                    request.setCurrentReviewerRole(reviewerRole);
                }
            }
            return;
        }
        if ("USER".equals(assignmentType)) {
            Long reviewerUserId = getLong(currentStep, "reviewerUserId");
            if (reviewerUserId == null) {
                throw new ServiceException("模板流程未配置固定审核人");
            }
            applyReviewerUser(request, reviewerUserId);
            return;
        }
        if ("FORM_FIELD".equals(assignmentType)) {
            String reviewerField = getString(currentStep, "reviewerField");
            Long reviewerUserId = getLong(formData, reviewerField);
            if (reviewerUserId == null) {
                throw new ServiceException("表单未填写审核人字段：" + reviewerField);
            }
            applyReviewerUser(request, reviewerUserId);
            return;
        }
        throw new ServiceException("暂不支持的流程分派方式：" + assignmentType);
    }

    private void applyReviewerUser(ScAffairRequest request, Long reviewerUserId) {
        SysUser reviewer = sysUserService.selectUserById(reviewerUserId);
        if (reviewer == null) {
            throw new ServiceException("审核人不存在");
        }
        request.setCurrentReviewerUserId(reviewerUserId);
        request.setCurrentReviewerName(StringUtils.defaultIfEmpty(reviewer.getNickName(), reviewer.getUserName()));
        request.setCurrentReviewerRole(resolveReviewerRole(reviewer));
    }

    private void validateFormData(List<Map<String, Object>> formFields, Map<String, Object> formData) {
        for (Map<String, Object> field : formFields) {
            boolean required = Boolean.parseBoolean(String.valueOf(field.getOrDefault("required", false)));
            if (!required) {
                continue;
            }
            String key = getString(field, "field");
            Object value = formData.get(key);
            if (value == null || (value instanceof String && StringUtils.isEmpty((String) value))) {
                throw new ServiceException(getString(field, "label") + "不能为空");
            }
        }
    }

    private boolean supportsPortalRole(ScAffairTemplate template, String portalRole) {
        if (template == null || StringUtils.isEmpty(portalRole)) {
            return false;
        }
        return parseRoleSet(template.getAudienceRoles()).contains(StringUtils.lowerCase(portalRole));
    }

    private boolean canViewRequest(LoginUser loginUser, ScAffairRequest request) {
        if (loginUser == null || request == null) {
            return false;
        }
        if (isAdmin(loginUser)) {
            return true;
        }
        if (Objects.equals(loginUser.getUserId(), request.getApplicantUserId())) {
            return true;
        }
        if (canReviewCurrentStep(loginUser, request)) {
            return true;
        }
        return scAffairActionMapper.selectScAffairActionListByRequestId(request.getRequestId()).stream()
                .anyMatch(item -> Objects.equals(item.getReviewerUserId(), loginUser.getUserId()));
    }

    private boolean canCancelRequest(LoginUser loginUser, ScAffairRequest request, ScAffairTemplate template) {
        if (request == null || loginUser == null) {
            return false;
        }
        if (isAdmin(loginUser)) {
            return true;
        }
        if (!Objects.equals(loginUser.getUserId(), request.getApplicantUserId())) {
            return false;
        }
        if (!STATUS_PENDING.equals(request.getRequestStatus())) {
            return false;
        }
        return template == null || !"0".equals(StringUtils.defaultIfEmpty(template.getAllowRevoke(), "1"));
    }

    private boolean canReviewCurrentStep(LoginUser loginUser, ScAffairRequest request) {
        if (loginUser == null || request == null || !STATUS_PENDING.equals(request.getRequestStatus())) {
            return false;
        }
        if (isAdmin(loginUser)) {
            return true;
        }
        if (request.getCurrentReviewerUserId() != null) {
            return Objects.equals(request.getCurrentReviewerUserId(), loginUser.getUserId());
        }
        if (StringUtils.isEmpty(request.getCurrentReviewerRole())) {
            return false;
        }
        return hasPortalRole(loginUser, request.getCurrentReviewerRole());
    }

    private void applyApprovalSideEffect(ScAffairRequest request, ScAffairTemplate template, Map<String, Object> formData, LoginUser loginUser) {
        if (request == null || template == null) {
            return;
        }
        String categoryCode = StringUtils.defaultString(request.getCategoryCode());

        // === 教材领用审批通过 → 自动递增教材计划已发放数量 ===
        if ("TEXTBOOK".equals(categoryCode)) {
            applyTextbookDistributionSideEffect(request, formData);
        }

        // === 学籍异动审批通过 → 更新学生状态 ===
        if (StringUtils.isNotEmpty(template.getTargetStatusCode()) && "student".equalsIgnoreCase(request.getApplicantUserType())) {
            applyStudentStatusSideEffect(request, template, formData, loginUser);
        }
    }

    /** 教材领用/补领审批通过后，自动递增教材计划的已发放数量 */
    private void applyTextbookDistributionSideEffect(ScAffairRequest request, Map<String, Object> formData) {
        String textbookType = getString(formData, "textbookType");
        if (!"CLAIM".equals(textbookType) && !"REISSUE".equals(textbookType)) {
            return;
        }
        String isbn = getString(formData, "isbn");
        if (StringUtils.isEmpty(isbn)) {
            return;
        }
        int quantity = 1;
        try {
            Object qty = formData.get("quantity");
            if (qty != null) {
                quantity = Integer.parseInt(String.valueOf(qty));
            }
        } catch (Exception ignored) { }
        if (quantity <= 0) {
            quantity = 1;
        }
        // 通过ISBN查找对应教材ID
        ScTextbook tbQuery = new ScTextbook();
        tbQuery.setIsbn(isbn);
        tbQuery.setStatus("0");
        List<ScTextbook> matchedBooks = scTextbookService.selectScTextbookList(tbQuery);
        if (matchedBooks.isEmpty()) {
            return;
        }
        Long textbookId = matchedBooks.get(0).getTextbookId();
        // 找到当前学期ID
        ScSchoolTerm termQuery = new ScSchoolTerm();
        termQuery.setStatus("0");
        List<ScSchoolTerm> terms = scSchoolTermService.selectScSchoolTermList(termQuery);
        ScSchoolTerm currentTerm = terms.stream()
                .filter(item -> "1".equals(String.valueOf(item.getIsCurrent())))
                .findFirst()
                .orElse(terms.isEmpty() ? null : terms.get(0));
        if (currentTerm == null) {
            return;
        }
        scTextbookPlanService.incrementDistributedQuantity(textbookId, currentTerm.getTermId(), quantity);
    }

    /** 学籍异动审批通过后更新学生状态和日志 */
    private void applyStudentStatusSideEffect(ScAffairRequest request, ScAffairTemplate template, Map<String, Object> formData, LoginUser loginUser) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(request.getApplicantUserId());
        if (profile == null) {
            return;
        }
        String beforeStatusCode = StringUtils.defaultIfEmpty(profile.getStudentStatusCode(), "IN_SCHOOL");
        String beforeStatusName = StringUtils.defaultIfEmpty(profile.getStudentStatusName(), "在籍在校");
        profile.setStudentStatusCode(template.getTargetStatusCode());
        profile.setStudentStatusName(StringUtils.defaultIfEmpty(template.getTargetStatusName(), template.getBusinessName()));
        profile.setStudentStatusUpdateTime(new Date());
        profile.setUpdateBy(loginUser.getUsername());
        scUserProfileService.updateScUserProfile(profile);

        ScStudentStatusLog log = new ScStudentStatusLog();
        log.setRequestId(request.getRequestId());
        log.setStudentUserId(request.getApplicantUserId());
        log.setStudentName(request.getApplicantName());
        log.setStudentNo(request.getApplicantNo());
        log.setBeforeStatusCode(beforeStatusCode);
        log.setBeforeStatusName(beforeStatusName);
        log.setAfterStatusCode(template.getTargetStatusCode());
        log.setAfterStatusName(StringUtils.defaultIfEmpty(template.getTargetStatusName(), template.getBusinessName()));
        log.setBusinessCode(request.getBusinessCode());
        log.setBusinessName(request.getBusinessName());
        log.setEffectiveDate(resolveEffectiveDate(formData));
        log.setDetailJson(request.getFormDataJson());
        log.setOperatorUserId(loginUser.getUserId());
        log.setOperatorName(loginUser.getUsername());
        log.setRemark("AFFAIR_APPROVED");
        scStudentStatusLogMapper.insertScStudentStatusLog(log);
    }

    private Date resolveEffectiveDate(Map<String, Object> formData) {
        Object effectiveDate = formData.get("effectiveDate");
        if (effectiveDate == null) {
            return null;
        }
        return DateUtils.parseDate(String.valueOf(effectiveDate));
    }

    private void recordAction(ScAffairRequest request, LoginUser loginUser, String actionType, String actionResult, String commentText) {
        ScAffairAction action = new ScAffairAction();
        action.setRequestId(request.getRequestId());
        action.setRequestNo(request.getRequestNo());
        action.setStepIndex(request.getCurrentStepIndex());
        action.setStepCode(request.getCurrentStepCode());
        action.setStepName(request.getCurrentStepName());
        action.setActionType(actionType);
        action.setActionResult(actionResult);
        action.setReviewerUserId(loginUser.getUserId());
        action.setReviewerName(resolveUserDisplayName(loginUser.getUser(), scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId())));
        action.setReviewerRole(resolvePrimaryRole(loginUser));
        action.setAssignmentType(request.getCurrentAssignmentType());
        action.setCommentText(StringUtils.defaultString(commentText));
        action.setDataSnapshotJson(request.getFormDataJson());
        action.setRemark(actionResult);
        scAffairActionMapper.insertScAffairAction(action);
    }

    private Map<String, Object> buildReviewResult(ScAffairRequest request, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getRequestId());
        result.put("requestStatus", request.getRequestStatus());
        result.put("currentStepName", request.getCurrentStepName());
        result.put("message", message);
        return result;
    }

    private String resolveTitle(String title, ScAffairTemplate template, ScAffairRequest request) {
        if (StringUtils.isNotEmpty(title)) {
            return StringUtils.substring(title.trim(), 0, 200);
        }
        return StringUtils.substring(
                StringUtils.defaultIfEmpty(template.getTemplateName(), "事务申请") + " - " + StringUtils.defaultIfEmpty(request.getApplicantName(), "申请人"),
                0, 200);
    }

    private String buildSummaryText(List<Map<String, Object>> formFields, Map<String, Object> formData) {
        List<String> parts = new ArrayList<>();
        for (Map<String, Object> field : formFields) {
            if (parts.size() >= 3) {
                break;
            }
            String fieldKey = getString(field, "field");
            if (StringUtils.equalsAnyIgnoreCase(fieldKey, "mentorTeacherId", "reason", "guardianPhone")) {
                continue;
            }
            Object value = formData.get(fieldKey);
            if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
                continue;
            }
            parts.add(getString(field, "label") + "：" + resolveDisplayValue(field, value));
        }
        if (parts.isEmpty()) {
            Object reason = formData.get("reason");
            if (reason != null && StringUtils.isNotEmpty(String.valueOf(reason))) {
                parts.add("说明：" + StringUtils.substring(String.valueOf(reason), 0, 120));
            }
        }
        return StringUtils.substring(String.join("；", parts), 0, 500);
    }

    private String resolveDisplayValue(Map<String, Object> field, Object value) {
        if (value == null) {
            return "";
        }
        if ("user-select".equalsIgnoreCase(getString(field, "component"))) {
            Long userId = getLong(Collections.singletonMap("value", value), "value");
            SysUser user = userId == null ? null : sysUserService.selectUserById(userId);
            return user == null ? String.valueOf(value) : StringUtils.defaultIfEmpty(user.getNickName(), user.getUserName());
        }
        String optionSource = getString(field, "optionSource");
        if (StringUtils.isNotEmpty(optionSource)) {
            String sourceLabel = resolveOptionSourceLabel(optionSource, value);
            if (StringUtils.isNotEmpty(sourceLabel)) {
                return sourceLabel;
            }
        }
        List<Map<String, Object>> options = toOptionList(field.get("options"));
        if (!options.isEmpty()) {
            String valueText = String.valueOf(value);
            for (Map<String, Object> option : options) {
                if (StringUtils.equals(String.valueOf(option.get("value")), valueText)) {
                    return String.valueOf(option.get("label"));
                }
            }
        }
        return String.valueOf(value);
    }

    private String resolveOptionSourceLabel(String optionSource, Object value) {
        if (value == null || StringUtils.isEmpty(optionSource)) {
            return "";
        }
        String source = StringUtils.lowerCase(optionSource);
        if ("studentstatus".equals(source)) {
            return buildStudentStatusOptions().stream()
                    .filter(item -> StringUtils.equals(String.valueOf(item.get("value")), String.valueOf(value)))
                    .map(item -> String.valueOf(item.get("label")))
                    .findFirst()
                    .orElse("");
        }
        Long id = null;
        try {
            id = Long.valueOf(String.valueOf(value));
        } catch (Exception ignored) {
        }
        if ("term".equals(source) && id != null) {
            com.smart.system.domain.ScSchoolTerm term = scSchoolTermService.selectScSchoolTermByTermId(id);
            return term == null ? "" : StringUtils.defaultIfEmpty(term.getTermName(), term.getSchoolYear());
        }
        if ("dept".equals(source) && id != null) {
            SysDept dept = sysDeptService.selectDeptById(id);
            return dept == null ? "" : StringUtils.defaultString(dept.getDeptName());
        }
        if ("grade".equals(source) && id != null) {
            ScGrade grade = scGradeService.selectScGradeByGradeId(id);
            return grade == null ? "" : StringUtils.defaultString(grade.getGradeName());
        }
        if ("class".equals(source) && id != null) {
            ScClass scClass = scClassService.selectScClassByClassId(id);
            return scClass == null ? "" : StringUtils.defaultString(scClass.getClassName());
        }
        return "";
    }

    private List<Map<String, Object>> buildTeacherOptions() {
        SysUser query = new SysUser();
        query.setStatus("0");
        query.setUserType("teacher");
        return sysUserMapper.selectUserList(query).stream()
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", StringUtils.defaultIfEmpty(item.getNickName(), item.getUserName()));
                    option.put("value", item.getUserId());
                    option.put("teacherNo", item.getTeacherNo());
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildAdvisorOptions() {
        return buildTeacherOptions().stream()
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>(item);
                    option.put("role", "advisor");
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildStudentOptions() {
        SysUser query = new SysUser();
        query.setStatus("0");
        query.setUserType("student");
        return sysUserMapper.selectUserList(query).stream()
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", StringUtils.defaultIfEmpty(item.getNickName(), item.getUserName()));
                    option.put("value", item.getUserId());
                    option.put("studentNo", item.getStudentNo());
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildTermOptions() {
        return scSchoolTermService.selectScSchoolTermList(new com.smart.system.domain.ScSchoolTerm()).stream()
                .filter(item -> "0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0")))
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", StringUtils.defaultIfEmpty(item.getTermName(), item.getSchoolYear()));
                    option.put("value", item.getTermId());
                    option.put("schoolYear", item.getSchoolYear());
                    option.put("startDate", item.getStartDate());
                    option.put("endDate", item.getEndDate());
                    option.put("isCurrent", item.getIsCurrent());
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildDeptOptions() {
        SysDept query = new SysDept();
        List<Map<String, Object>> flatList = sysDeptMapper.selectDeptList(query).stream()
                .filter(item -> "0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0")))
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", item.getDeptName());
                    option.put("value", item.getDeptId());
                    option.put("parentId", item.getParentId());
                    return option;
                }).collect(Collectors.toList());
        return buildDeptTree(flatList, 0L);
    }

    /** 递归构建部门树 */
    private List<Map<String, Object>> buildDeptTree(List<Map<String, Object>> flatList, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map<String, Object> node : flatList) {
            Long pid = node.get("parentId") instanceof Number ? ((Number) node.get("parentId")).longValue() : 0L;
            if (Objects.equals(pid, parentId)) {
                List<Map<String, Object>> children = buildDeptTree(flatList, ((Number) node.get("value")).longValue());
                Map<String, Object> treeNode = new LinkedHashMap<>(node);
                if (!children.isEmpty()) {
                    treeNode.put("children", children);
                }
                tree.add(treeNode);
            }
        }
        return tree;
    }

    private List<Map<String, Object>> buildGradeOptions() {
        return scGradeService.selectScGradeList(new ScGrade()).stream()
                .filter(item -> "0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0")))
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", item.getGradeName());
                    option.put("value", item.getGradeId());
                    option.put("schoolYear", item.getSchoolYear());
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildClassOptions() {
        return scClassService.selectScClassList(new ScClass()).stream()
                .filter(item -> "0".equals(StringUtils.defaultIfEmpty(item.getStatus(), "0")))
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("label", item.getClassName());
                    option.put("value", item.getClassId());
                    option.put("gradeId", item.getGradeId());
                    option.put("deptId", item.getDeptId());
                    option.put("deptName", item.getDeptName());
                    return option;
                }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildStudentStatusOptions() {
        return List.of(
                buildOption("在籍在校", "IN_SCHOOL"),
                buildOption("休学", "SUSPENDED"),
                buildOption("转专业", "TRANSFERRED_MAJOR"),
                buildOption("保留学籍", "KEEP_STATUS"),
                buildOption("毕业", "GRADUATED"),
                buildOption("退学", "DROPPED")
        );
    }

    /** 构建当前用户已审批通过且未销假的请假记录选项 */
    private List<Map<String, Object>> buildLeaveOptions() {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return Collections.emptyList();
            }
            // 查询当前用户已审批通过的请假申请
            ScAffairRequest query = new ScAffairRequest();
            query.setApplicantUserId(userId);
            query.setRequestStatus(STATUS_APPROVED);
            List<ScAffairRequest> allApproved = scAffairRequestMapper.selectScAffairRequestList(query);
            // 筛选请假类别的记录 (templateCode 包含 ASK_LEAVE / LEAVE)
            List<ScAffairRequest> leaveRequests = allApproved.stream()
                    .filter(r -> {
                        String tc = StringUtils.defaultIfEmpty(r.getTemplateCode(), "");
                        String cc = StringUtils.defaultIfEmpty(r.getCategoryCode(), "");
                        return tc.contains("ASK_LEAVE") || tc.contains("LEAVE") || "ASK_LEAVE".equals(cc);
                    })
                    .collect(Collectors.toList());
            // 查询当前用户已提交的销假记录，提取已关联的请假编号
            ScAffairRequest cancelQuery = new ScAffairRequest();
            cancelQuery.setApplicantUserId(userId);
            List<ScAffairRequest> allRequests = scAffairRequestMapper.selectScAffairRequestList(cancelQuery);
            Set<String> cancelledLeaveNos = allRequests.stream()
                    .filter(r -> {
                        String tc = StringUtils.defaultIfEmpty(r.getTemplateCode(), "");
                        return tc.contains("CANCEL_LEAVE") || tc.contains("LEAVE_CANCEL");
                    })
                    .filter(r -> !STATUS_REJECTED.equals(r.getRequestStatus()) && !STATUS_CANCELLED.equals(r.getRequestStatus()))
                    .map(r -> {
                        try {
                            Map<String, Object> formData = objectMapper.readValue(
                                    StringUtils.defaultIfEmpty(r.getFormDataJson(), "{}"),
                                    new TypeReference<Map<String, Object>>() {});
                            return StringUtils.defaultIfEmpty(String.valueOf(formData.getOrDefault("leaveRequestNo", "")), "");
                        } catch (Exception e) {
                            return "";
                        }
                    })
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            // 过滤掉已销假的记录，构建选项
            return leaveRequests.stream()
                    .filter(r -> !cancelledLeaveNos.contains(r.getRequestNo()))
                    .map(r -> {
                        Map<String, Object> option = new LinkedHashMap<>();
                        // 解析表单数据获取请假日期范围
                        String dateRange = "";
                        try {
                            Map<String, Object> formData = objectMapper.readValue(
                                    StringUtils.defaultIfEmpty(r.getFormDataJson(), "{}"),
                                    new TypeReference<Map<String, Object>>() {});
                            String startDate = String.valueOf(formData.getOrDefault("startDate", formData.getOrDefault("leaveStartDate", "")));
                            String endDate = String.valueOf(formData.getOrDefault("endDate", formData.getOrDefault("leaveEndDate", "")));
                            String leaveType = String.valueOf(formData.getOrDefault("leaveType", formData.getOrDefault("leaveCategory", "")));
                            if (StringUtils.isNotEmpty(startDate) && !"null".equals(startDate)) {
                                dateRange = startDate + "~" + (StringUtils.isNotEmpty(endDate) && !"null".equals(endDate) ? endDate : "");
                            }
                            if (StringUtils.isNotEmpty(leaveType) && !"null".equals(leaveType)) {
                                dateRange = dateRange + " " + leaveType;
                            }
                        } catch (Exception ignored) {
                        }
                        String label = StringUtils.isNotEmpty(dateRange)
                                ? dateRange.trim() + " (" + r.getRequestNo() + ")"
                                : r.getTitle() + " (" + r.getRequestNo() + ")";
                        option.put("label", label);
                        option.put("value", r.getRequestNo());
                        option.put("requestId", r.getRequestId());
                        return option;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** 构建当前学生本学期的课程选项（供教材申请选课程用） */
    private List<Map<String, Object>> buildMyCoursesOptions() {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return Collections.emptyList();
            }
            ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(userId);
            if (profile == null || profile.getClassId() == null) {
                return Collections.emptyList();
            }
            // 获取当前学期
            ScSchoolTerm termQuery = new ScSchoolTerm();
            termQuery.setStatus("0");
            List<ScSchoolTerm> terms = scSchoolTermService.selectScSchoolTermList(termQuery);
            ScSchoolTerm currentTerm = terms.stream()
                    .filter(item -> "1".equals(String.valueOf(item.getIsCurrent())))
                    .findFirst()
                    .orElse(terms.isEmpty() ? null : terms.get(0));
            Long termId = currentTerm == null ? null : currentTerm.getTermId();

            // 查询该班级本学期的课程
            ScClassCourse ccQuery = new ScClassCourse();
            ccQuery.setClassId(profile.getClassId());
            ccQuery.setTermId(termId);
            ccQuery.setStatus("0");
            List<ScClassCourse> classCourses = scClassCourseService.selectScClassCourseList(ccQuery);

            // 去重课程ID，构建选项
            Set<Long> seenCourseIds = new LinkedHashSet<>();
            List<Map<String, Object>> result = new ArrayList<>();
            for (ScClassCourse cc : classCourses) {
                if (cc.getCourseId() == null || !seenCourseIds.add(cc.getCourseId())) {
                    continue;
                }
                Map<String, Object> option = new LinkedHashMap<>();
                String label = StringUtils.defaultIfEmpty(cc.getCourseName(), "课程" + cc.getCourseId());
                option.put("label", label);
                option.put("value", cc.getCourseId());
                result.add(option);
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** 构建当前学生已审批通过且尚未登记返校的离校申请选项 */
    private List<Map<String, Object>> buildMyLeaveReturnRequestsOptions() {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return Collections.emptyList();
            }
            // 查询已审批通过的离校申请
            ScAffairRequest query = new ScAffairRequest();
            query.setApplicantUserId(userId);
            query.setRequestStatus(STATUS_APPROVED);
            List<ScAffairRequest> allApproved = scAffairRequestMapper.selectScAffairRequestList(query);
            List<ScAffairRequest> leaveReturnRequests = allApproved.stream()
                    .filter(r -> {
                        String tc = StringUtils.defaultIfEmpty(r.getTemplateCode(), "");
                        String cc = StringUtils.defaultIfEmpty(r.getCategoryCode(), "");
                        return "LEAVE_RETURN_SCHOOL".equals(cc)
                                || tc.contains("LEAVE_RETURN") || tc.contains("LEAVE_SCHOOL");
                    })
                    .collect(Collectors.toList());
            // 查询已提交的返校登记记录，提取已关联的离校编号
            ScAffairRequest returnQuery = new ScAffairRequest();
            returnQuery.setApplicantUserId(userId);
            List<ScAffairRequest> allRequests = scAffairRequestMapper.selectScAffairRequestList(returnQuery);
            Set<String> registeredNos = allRequests.stream()
                    .filter(r -> {
                        String tc = StringUtils.defaultIfEmpty(r.getTemplateCode(), "");
                        return tc.contains("RETURN_REGISTRATION") || tc.contains("RETURN_SCHOOL_REG");
                    })
                    .filter(r -> !STATUS_REJECTED.equals(r.getRequestStatus()) && !STATUS_CANCELLED.equals(r.getRequestStatus()))
                    .map(r -> {
                        try {
                            Map<String, Object> formData = objectMapper.readValue(
                                    StringUtils.defaultIfEmpty(r.getFormDataJson(), "{}"),
                                    new TypeReference<Map<String, Object>>() {});
                            return StringUtils.defaultIfEmpty(String.valueOf(formData.getOrDefault("leaveRequestNo", "")), "");
                        } catch (Exception e) {
                            return "";
                        }
                    })
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            // 过滤已登记返校的，构建选项
            return leaveReturnRequests.stream()
                    .filter(r -> !registeredNos.contains(r.getRequestNo()))
                    .map(r -> {
                        Map<String, Object> option = new LinkedHashMap<>();
                        String dateInfo = "";
                        try {
                            Map<String, Object> formData = objectMapper.readValue(
                                    StringUtils.defaultIfEmpty(r.getFormDataJson(), "{}"),
                                    new TypeReference<Map<String, Object>>() {});
                            String leaveDate = String.valueOf(formData.getOrDefault("leaveDate", formData.getOrDefault("startDate", "")));
                            if (StringUtils.isNotEmpty(leaveDate) && !"null".equals(leaveDate)) {
                                dateInfo = leaveDate;
                            }
                        } catch (Exception ignored) {
                        }
                        String label = StringUtils.isNotEmpty(dateInfo)
                                ? dateInfo + " (" + r.getRequestNo() + ")"
                                : r.getTitle() + " (" + r.getRequestNo() + ")";
                        option.put("label", label);
                        option.put("value", r.getRequestNo());
                        option.put("requestId", r.getRequestId());
                        return option;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Object> buildOption(String label, String value) {
        Map<String, Object> option = new LinkedHashMap<>();
        option.put("label", label);
        option.put("value", value);
        return option;
    }

    private ScAffairTemplate requireTemplate(Long templateId) {
        ScAffairTemplate template = scAffairTemplateService.selectScAffairTemplateByTemplateId(templateId);
        if (template == null) {
            throw new ServiceException("事务模板不存在");
        }
        if (!"0".equals(StringUtils.defaultIfEmpty(template.getStatus(), "0"))) {
            throw new ServiceException("事务模板已停用");
        }
        return template;
    }

    private ScAffairRequest requireRequest(Long requestId) {
        ScAffairRequest request = scAffairRequestMapper.selectScAffairRequestByRequestId(requestId);
        if (request == null) {
            throw new ServiceException("事务申请不存在");
        }
        return request;
    }

    private void requirePortalRole(LoginUser loginUser, String portalRole) {
        if (loginUser == null) {
            throw new ServiceException("登录已失效");
        }
        if (StringUtils.isEmpty(portalRole)) {
            return;
        }
        if (!hasPortalRole(loginUser, portalRole)) {
            throw new ServiceException("当前账号无权访问该端服务");
        }
    }

    private boolean matchesPortalRole(LoginUser loginUser, String portalRole) {
        return StringUtils.isEmpty(portalRole) || hasPortalRole(loginUser, portalRole);
    }

    private List<String> resolveReviewerRolesForPortal(LoginUser loginUser, String portalRole) {
        List<String> roles = new ArrayList<>();
        Set<String> roleKeys = resolveRoleKeys(loginUser);
        for (String key : roleKeys) {
            roles.add(key);
        }
        if ("advisor".equals(portalRole)) {
            if (!roles.contains("advisor")) roles.add("advisor");
            if (!roles.contains("head_teacher")) roles.add("head_teacher");
            if (!roles.contains("campus_head_teacher")) roles.add("campus_head_teacher");
        }
        return roles;
    }

    private boolean hasPortalRole(LoginUser loginUser, String portalRole) {
        if (loginUser == null) {
            return false;
        }
        if (isAdmin(loginUser)) {
            return true;
        }
        String role = StringUtils.lowerCase(portalRole);
        return switch (role) {
            case "student" -> hasAnyRole(loginUser, "student");
            case "teacher" -> hasAnyRole(loginUser, "teacher");
            case "advisor" -> hasAnyRole(loginUser, "advisor", "campus_head_teacher", "head_teacher");
            case "parent" -> hasAnyRole(loginUser, "parent");
            case "admin" -> hasAnyRole(loginUser, "admin", "campus_academic_admin");
            default -> hasAnyRole(loginUser, role);
        };
    }

    private boolean hasAnyRole(LoginUser loginUser, String... roles) {
        Set<String> roleKeys = resolveRoleKeys(loginUser);
        for (String role : roles) {
            if (roleKeys.contains(StringUtils.lowerCase(role))) {
                return true;
            }
        }
        return false;
    }

    private Set<String> resolveRoleKeys(LoginUser loginUser) {
        if (loginUser == null || loginUser.getUser() == null || loginUser.getUser().getRoles() == null) {
            return Collections.emptySet();
        }
        return loginUser.getUser().getRoles().stream()
                .map(SysRole::getRoleKey)
                .filter(StringUtils::isNotEmpty)
                .map(StringUtils::lowerCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean isAdmin(LoginUser loginUser) {
        return loginUser != null && (Objects.equals(loginUser.getUserId(), 1L) || hasAnyRole(loginUser, "admin"));
    }

    private String resolvePrimaryRole(LoginUser loginUser) {
        if (hasPortalRole(loginUser, "advisor")) {
            return "advisor";
        }
        if (hasPortalRole(loginUser, "teacher")) {
            return "teacher";
        }
        if (hasPortalRole(loginUser, "student")) {
            return "student";
        }
        if (hasPortalRole(loginUser, "parent")) {
            return "parent";
        }
        return "admin";
    }

    private String resolveReviewerRole(SysUser reviewer) {
        if (reviewer == null) {
            return "";
        }
        if ("teacher".equalsIgnoreCase(reviewer.getUserType())) {
            return "teacher";
        }
        if ("student".equalsIgnoreCase(reviewer.getUserType())) {
            return "student";
        }
        return "admin";
    }

    private String resolveUserDisplayName(SysUser user, ScUserProfile profile) {
        if (profile != null && StringUtils.isNotEmpty(profile.getRealName())) {
            return profile.getRealName();
        }
        if (user != null) {
            return StringUtils.defaultIfEmpty(user.getRealName(), StringUtils.defaultIfEmpty(user.getNickName(), user.getUserName()));
        }
        return "";
    }

    private String resolveApplicantUserType(SysUser user, ScUserProfile profile) {
        if (profile != null && StringUtils.isNotEmpty(profile.getUserType())) {
            return profile.getUserType();
        }
        return user == null ? "" : StringUtils.defaultString(user.getUserType());
    }

    private String resolveApplicantNo(SysUser user, ScUserProfile profile) {
        if (profile != null) {
            if (StringUtils.isNotEmpty(profile.getStudentNo())) {
                return profile.getStudentNo();
            }
            if (StringUtils.isNotEmpty(profile.getTeacherNo())) {
                return profile.getTeacherNo();
            }
        }
        if (user == null) {
            return "";
        }
        return StringUtils.defaultIfEmpty(user.getStudentNo(), StringUtils.defaultIfEmpty(user.getTeacherNo(), String.valueOf(user.getUserId())));
    }

    private List<Map<String, Object>> buildTemplateStats(List<ScAffairTemplate> templates, List<ScAffairRequest> requests) {
        if (templates == null || templates.isEmpty()) {
            return Collections.emptyList();
        }
        List<ScAffairRequest> safeRequests = requests == null ? Collections.emptyList() : requests;
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScAffairTemplate template : templates) {
            if (template == null || template.getTemplateId() == null) {
                continue;
            }
            List<ScAffairRequest> matched = safeRequests.stream()
                    .filter(item -> Objects.equals(item.getTemplateId(), template.getTemplateId()))
                    .collect(Collectors.toList());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("templateId", template.getTemplateId());
            item.put("templateCode", template.getTemplateCode());
            item.put("templateName", template.getTemplateName());
            item.put("businessName", template.getBusinessName());
            item.put("total", matched.size());
            item.put("pending", matched.stream().filter(req -> STATUS_PENDING.equals(req.getRequestStatus())).count());
            item.put("approved", matched.stream().filter(req -> STATUS_APPROVED.equals(req.getRequestStatus())).count());
            item.put("rejected", matched.stream().filter(req -> STATUS_REJECTED.equals(req.getRequestStatus())).count());
            item.put("cancelled", matched.stream().filter(req -> STATUS_CANCELLED.equals(req.getRequestStatus())).count());
            result.add(item);
        }
        return result;
    }

    private List<ScAffairTemplate> filterAccessibleTemplates(List<ScAffairTemplate> templates, LoginUser loginUser, String portalRole) {
        if (!"student".equals(portalRole) || templates == null || templates.isEmpty()) {
            return templates == null ? Collections.emptyList() : templates;
        }
        return templates.stream()
                .filter(template -> canCurrentUserApplyTemplate(template, loginUser, Collections.emptyMap(), false))
                .collect(Collectors.toList());
    }

    private void validateSubmissionRules(ScAffairTemplate template, LoginUser loginUser, Map<String, Object> formData) {
        if (!canCurrentUserApplyTemplate(template, loginUser, formData, true)) {
            throw new ServiceException("当前账号不满足该事务模板的申请条件");
        }
    }

    private boolean canCurrentUserApplyTemplate(ScAffairTemplate template, LoginUser loginUser, Map<String, Object> formData, boolean throwOnFailure) {
        if (template == null || loginUser == null) {
            return false;
        }
        Map<String, Object> scopeConfig = parseConfigMap(template.getApplyScopeJson());
        Map<String, Object> ruleConfig = parseConfigMap(template.getBusinessRulesJson());

        if (!matchApplyScope(scopeConfig, loginUser, throwOnFailure)) {
            return false;
        }
        if (!matchOpenWindow(ruleConfig, throwOnFailure)) {
            return false;
        }
        if (!matchDuplicatePendingRule(template, loginUser, ruleConfig, throwOnFailure)) {
            return false;
        }
        if (!matchPrerequisiteRules(loginUser, ruleConfig, throwOnFailure)) {
            return false;
        }
        if (!matchWorkStudyRule(ruleConfig, formData, throwOnFailure)) {
            return false;
        }
        return true;
    }

    private Map<String, Object> parseConfigMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private boolean matchApplyScope(Map<String, Object> scopeConfig, LoginUser loginUser, boolean throwOnFailure) {
        if (scopeConfig == null || scopeConfig.isEmpty() || isAdmin(loginUser)) {
            return true;
        }
        String scopeType = StringUtils.upperCase(getString(scopeConfig, "scopeType"));
        if (StringUtils.isEmpty(scopeType) || "ALL".equals(scopeType)) {
            return true;
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(loginUser.getUserId());
        List<Long> userIds = getLongList(scopeConfig.get("userIds"));
        List<Long> deptIds = getLongList(scopeConfig.get("deptIds"));
        List<Long> gradeIds = getLongList(scopeConfig.get("gradeIds"));
        List<Long> classIds = getLongList(scopeConfig.get("classIds"));

        boolean matched = userIds.contains(loginUser.getUserId())
                || (loginUser.getDeptId() != null && deptIds.contains(loginUser.getDeptId()))
                || (profile != null && profile.getGradeId() != null && gradeIds.contains(profile.getGradeId()))
                || (profile != null && profile.getClassId() != null && classIds.contains(profile.getClassId()));
        if (!matched && throwOnFailure) {
            throw new ServiceException("当前账号未被纳入该事务模板的申请范围");
        }
        return matched;
    }

    private boolean matchOpenWindow(Map<String, Object> ruleConfig, boolean throwOnFailure) {
        if (ruleConfig == null || ruleConfig.isEmpty()) {
            return true;
        }
        boolean enabled = Boolean.parseBoolean(String.valueOf(ruleConfig.getOrDefault("openWindowEnabled", false)));
        if (!enabled) {
            return true;
        }
        Date now = new Date();
        Date start = DateUtils.parseDate(getString(ruleConfig, "openStartTime"));
        Date end = DateUtils.parseDate(getString(ruleConfig, "openEndTime"));
        if (start != null && now.before(start)) {
            if (throwOnFailure) {
                throw new ServiceException("当前申请尚未到开放时间");
            }
            return false;
        }
        if (end != null && now.after(end)) {
            if (throwOnFailure) {
                throw new ServiceException("当前申请已超过开放截止时间");
            }
            return false;
        }
        return true;
    }

    private boolean matchDuplicatePendingRule(ScAffairTemplate template, LoginUser loginUser, Map<String, Object> ruleConfig, boolean throwOnFailure) {
        boolean requireNoPending = !ruleConfig.containsKey("requireNoPending")
                || Boolean.parseBoolean(String.valueOf(ruleConfig.get("requireNoPending")));
        if (!requireNoPending) {
            return true;
        }
        ScAffairRequest query = new ScAffairRequest();
        query.setApplicantUserId(loginUser.getUserId());
        query.setTemplateId(template.getTemplateId());
        query.setRequestStatus(STATUS_PENDING);
        boolean existsPending = !scAffairRequestMapper.selectScAffairRequestList(query).isEmpty();
        if (existsPending && throwOnFailure) {
            throw new ServiceException("已有同类申请处于待审核状态，请处理完成后再提交");
        }
        return !existsPending;
    }

    private boolean matchPrerequisiteRules(LoginUser loginUser, Map<String, Object> ruleConfig, boolean throwOnFailure) {
        List<String> prerequisiteCategories = getStringList(ruleConfig.get("prerequisiteCategoryCodes"));
        List<String> prerequisiteTemplates = getStringList(ruleConfig.get("prerequisiteTemplateCodes"));
        if (prerequisiteCategories.isEmpty() && prerequisiteTemplates.isEmpty()) {
            return true;
        }
        ScAffairRequest query = new ScAffairRequest();
        query.setApplicantUserId(loginUser.getUserId());
        List<ScAffairRequest> myRequests = scAffairRequestMapper.selectScAffairRequestList(query);
        Long expectedTermId = getLong(ruleConfig, "termId");
        boolean matchedCategory = prerequisiteCategories.isEmpty() || prerequisiteCategories.stream()
                .allMatch(code -> myRequests.stream().anyMatch(item -> STATUS_APPROVED.equals(item.getRequestStatus())
                        && StringUtils.equals(item.getCategoryCode(), code)
                        && matchRequestTerm(item, expectedTermId)));
        boolean matchedTemplate = prerequisiteTemplates.isEmpty() || prerequisiteTemplates.stream()
                .allMatch(code -> myRequests.stream().anyMatch(item -> STATUS_APPROVED.equals(item.getRequestStatus())
                        && StringUtils.equals(item.getTemplateCode(), code)
                        && matchRequestTerm(item, expectedTermId)));
        boolean matched = matchedCategory && matchedTemplate;
        if (!matched && throwOnFailure) {
            throw new ServiceException("当前申请存在前置条件未满足，请先完成相关事务办理");
        }
        return matched;
    }

    private boolean matchRequestTerm(ScAffairRequest request, Long expectedTermId) {
        if (expectedTermId == null || request == null) {
            return true;
        }
        Map<String, Object> formData = parseConfigMap(request.getFormDataJson());
        Long requestTermId = getLong(formData, "termId");
        return Objects.equals(expectedTermId, requestTermId);
    }

    private boolean matchWorkStudyRule(Map<String, Object> ruleConfig, Map<String, Object> formData, boolean throwOnFailure) {
        boolean requirePublishedJob = Boolean.parseBoolean(String.valueOf(ruleConfig.getOrDefault("requirePublishedJob", false)));
        if (!requirePublishedJob) {
            return true;
        }
        if (!throwOnFailure && (formData == null || formData.isEmpty())) {
            return true;
        }
        String jobId = getString(formData, "jobId");
        if (StringUtils.isEmpty(jobId)) {
            if (throwOnFailure) {
                throw new ServiceException("勤工助学申请需先选择已发布岗位");
            }
            return false;
        }
        ScAffairWorkStudyJob job = null;
        try {
            job = scAffairWorkStudyJobService.selectScAffairWorkStudyJobByJobId(Long.valueOf(jobId));
        } catch (Exception ignored) {
        }
        if (job == null || !"0".equals(StringUtils.defaultIfEmpty(job.getPublishStatus(), "1"))) {
            if (throwOnFailure) {
                throw new ServiceException("所选勤工助学岗位不存在或未开放申请");
            }
            return false;
        }
        Date now = new Date();
        if ((job.getOpenStartTime() != null && now.before(job.getOpenStartTime()))
                || (job.getOpenEndTime() != null && now.after(job.getOpenEndTime()))) {
            if (throwOnFailure) {
                throw new ServiceException("当前岗位不在开放申请时间内");
            }
            return false;
        }
        return true;
    }

    private Map<String, Object> buildAdvisorInfo(Long applicantUserId) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (applicantUserId == null) {
            return result;
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(applicantUserId);
        if (profile == null) {
            return result;
        }
        Long advisorUserId = profile.getAdvisorUserId();
        String bindingMode = "DIRECT";
        if (advisorUserId == null && profile.getClassId() != null) {
            ScClass scClass = scClassService.selectScClassByClassId(profile.getClassId());
            advisorUserId = scClass == null ? null : scClass.getHeadTeacherId();
            bindingMode = "CLASS_HEAD_TEACHER";
        }
        if (advisorUserId == null) {
            return result;
        }
        SysUser advisor = sysUserService.selectUserById(advisorUserId);
        result.put("advisorUserId", advisorUserId);
        result.put("advisorName", advisor == null ? String.valueOf(advisorUserId) : StringUtils.defaultIfEmpty(advisor.getRealName(), StringUtils.defaultIfEmpty(advisor.getNickName(), advisor.getUserName())));
        result.put("teacherNo", advisor == null ? "" : StringUtils.defaultString(advisor.getTeacherNo()));
        result.put("phonenumber", advisor == null ? "" : StringUtils.defaultString(advisor.getPhonenumber()));
        result.put("bindingMode", bindingMode);
        return result;
    }

    private Map<String, Object> buildAskLeaveCategoryData(Long applicantUserId) {
        if (applicantUserId == null) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> records = scAffairSpecialRecordMapper.selectAskLeaveRecordsByApplicantUserId(applicantUserId);
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> leaveRecords = records.stream()
                .filter(item -> "LEAVE".equals(getString(item, "business_type")) || "ASK_LEAVE_STANDARD".equals(getString(item, "template_code")))
                .collect(Collectors.toList());
        List<Map<String, Object>> cancelRecords = records.stream()
                .filter(item -> "CANCEL".equals(getString(item, "business_type")) || "ASK_LEAVE_CANCEL".equals(getString(item, "template_code")))
                .collect(Collectors.toList());
        Set<String> cancelledLeaveNos = cancelRecords.stream()
                .map(item -> getString(item, "leave_request_no"))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Map<String, Object>> pendingCancelRecords = leaveRecords.stream()
                .filter(item -> STATUS_APPROVED.equals(getString(item, "request_status")))
                .filter(item -> !cancelledLeaveNos.contains(getString(item, "request_no")))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("leaveCount", leaveRecords.size());
        result.put("cancelCount", cancelRecords.size());
        result.put("pendingCount", records.stream().filter(item -> STATUS_PENDING.equals(getString(item, "request_status"))).count());
        result.put("pendingCancelCount", pendingCancelRecords.size());
        result.put("leaveRecords", leaveRecords.stream().limit(12).collect(Collectors.toList()));
        result.put("cancelRecords", cancelRecords.stream().limit(12).collect(Collectors.toList()));
        result.put("pendingCancelRecords", pendingCancelRecords.stream().limit(6).collect(Collectors.toList()));
        result.put("recentRecords", records.stream().limit(12).collect(Collectors.toList()));
        return result;
    }

    private Map<String, Object> buildLeaveReturnCategoryData(Long applicantUserId) {
        if (applicantUserId == null) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> records = scAffairSpecialRecordMapper.selectLeaveReturnRecordsByApplicantUserId(applicantUserId);
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> leaveRecords = records.stream()
                .filter(item -> "LEAVE".equals(getString(item, "business_type")) || "LEAVE_RETURN_STANDARD".equals(getString(item, "template_code")))
                .collect(Collectors.toList());
        List<Map<String, Object>> returnRecords = records.stream()
                .filter(item -> "RETURN".equals(getString(item, "business_type")) || "LEAVE_RETURN_CONFIRM".equals(getString(item, "template_code")))
                .collect(Collectors.toList());
        Set<String> returnedLeaveNos = returnRecords.stream()
                .map(item -> getString(item, "leave_request_no"))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Map<String, Object>> pendingReturnRecords = leaveRecords.stream()
                .filter(item -> STATUS_APPROVED.equals(getString(item, "request_status")))
                .filter(item -> !returnedLeaveNos.contains(getString(item, "request_no")))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("leaveCount", leaveRecords.size());
        result.put("returnCount", returnRecords.size());
        result.put("pendingCount", records.stream().filter(item -> STATUS_PENDING.equals(getString(item, "request_status"))).count());
        result.put("pendingReturnCount", pendingReturnRecords.size());
        result.put("leaveRecords", leaveRecords.stream().limit(12).collect(Collectors.toList()));
        result.put("returnRecords", returnRecords.stream().limit(12).collect(Collectors.toList()));
        result.put("pendingReturnRecords", pendingReturnRecords.stream().limit(6).collect(Collectors.toList()));
        result.put("recentRecords", records.stream().limit(12).collect(Collectors.toList()));
        return result;
    }

    private Map<String, Object> buildTextbookCategoryData(Long applicantUserId) {
        if (applicantUserId == null) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> records = scAffairSpecialRecordMapper.selectTextbookRecordsByApplicantUserId(applicantUserId);
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap();
        }
        long claimCount = records.stream().filter(item -> "CLAIM".equals(getString(item, "textbook_type"))).count();
        long reissueCount = records.stream().filter(item -> "REISSUE".equals(getString(item, "textbook_type"))).count();
        long orderCount = records.stream().filter(item -> "ORDER".equals(getString(item, "textbook_type"))).count();
        long pendingCount = records.stream().filter(item -> STATUS_PENDING.equals(getString(item, "request_status"))).count();
        long approvedCount = records.stream().filter(item -> STATUS_APPROVED.equals(getString(item, "request_status"))).count();
        int totalQuantity = records.stream().mapToInt(item -> getInteger(item, "quantity")).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("claimCount", claimCount);
        result.put("reissueCount", reissueCount);
        result.put("orderCount", orderCount);
        result.put("pendingCount", pendingCount);
        result.put("approvedCount", approvedCount);
        result.put("totalQuantity", totalQuantity);
        result.put("recentRecords", records.stream().limit(12).collect(Collectors.toList()));
        result.put("pendingRecords", records.stream()
                .filter(item -> STATUS_PENDING.equals(getString(item, "request_status")))
                .limit(6)
                .collect(Collectors.toList()));
        return result;
    }

    private Map<String, Object> buildAcademicStatusCategoryData(Long applicantUserId) {
        if (applicantUserId == null) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> records = scAffairSpecialRecordMapper.selectAcademicStatusRecordsByApplicantUserId(applicantUserId);
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap();
        }
        long suspendCount = records.stream().filter(item -> "SUSPENDED".equals(getString(item, "after_status_code"))).count();
        long resumeCount = records.stream().filter(item -> "IN_SCHOOL".equals(getString(item, "after_status_code"))).count();
        long transferCount = records.stream().filter(item -> "TRANSFERRED_MAJOR".equals(getString(item, "after_status_code"))).count();
        long pendingCount = records.stream().filter(item -> STATUS_PENDING.equals(getString(item, "request_status"))).count();
        Map<String, Object> latestApprovedRecord = records.stream()
                .filter(item -> STATUS_APPROVED.equals(getString(item, "request_status")))
                .findFirst()
                .orElse(Collections.emptyMap());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("suspendCount", suspendCount);
        result.put("resumeCount", resumeCount);
        result.put("transferCount", transferCount);
        result.put("pendingCount", pendingCount);
        result.put("records", records.stream().limit(12).collect(Collectors.toList()));
        result.put("pendingRecords", records.stream()
                .filter(item -> STATUS_PENDING.equals(getString(item, "request_status")))
                .limit(6)
                .collect(Collectors.toList()));
        result.put("latestApprovedRecord", latestApprovedRecord);
        return result;
    }

    private void persistSpecializedRecord(ScAffairRequest request, ScAffairTemplate template, Map<String, Object> formData) {
        if (request == null || template == null) {
            return;
        }
        String categoryCode = StringUtils.defaultString(request.getCategoryCode());
        String templateCode = StringUtils.defaultString(template.getTemplateCode());
        Map<String, Object> params = buildSpecializedRecordParams(request, templateCode, formData);
        if ("ASK_LEAVE".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertAskLeaveRecord(params);
            return;
        }
        if ("LEAVE_RETURN_SCHOOL".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertLeaveReturnRecord(params);
            return;
        }
        if ("ACTIVITY".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertActivityRecord(params);
            return;
        }
        if ("SUBJECT_COMPETITION".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertCompetitionRecord(params);
            return;
        }
        if ("INNOVATION_ENTREPRENEURSHIP".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertInnovationRecord(params);
            return;
        }
        if ("COMPREHENSIVE_EVALUATION".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertEvaluationRecord(params);
            return;
        }
        if ("TEXTBOOK".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertTextbookRecord(params);
            return;
        }
        if ("ACADEMIC_STATUS".equals(categoryCode)) {
            scAffairSpecialRecordMapper.upsertAcademicStatusRecord(params);
        }
    }

    private Map<String, Object> loadSpecializedRecord(ScAffairRequest request, ScAffairTemplate template) {
        if (request == null) {
            return Collections.emptyMap();
        }
        String categoryCode = StringUtils.defaultString(request.getCategoryCode());
        if ("ASK_LEAVE".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectAskLeaveRecordByRequestId(request.getRequestId());
        }
        if ("LEAVE_RETURN_SCHOOL".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectLeaveReturnRecordByRequestId(request.getRequestId());
        }
        if ("ACTIVITY".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectActivityRecordByRequestId(request.getRequestId());
        }
        if ("SUBJECT_COMPETITION".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectCompetitionRecordByRequestId(request.getRequestId());
        }
        if ("INNOVATION_ENTREPRENEURSHIP".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectInnovationRecordByRequestId(request.getRequestId());
        }
        if ("COMPREHENSIVE_EVALUATION".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectEvaluationRecordByRequestId(request.getRequestId());
        }
        if ("TEXTBOOK".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectTextbookRecordByRequestId(request.getRequestId());
        }
        if ("ACADEMIC_STATUS".equals(categoryCode)) {
            return scAffairSpecialRecordMapper.selectAcademicStatusRecordByRequestId(request.getRequestId());
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> buildSpecializedRecordParams(ScAffairRequest request, String templateCode, Map<String, Object> formData) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("requestId", request.getRequestId());
        params.put("requestNo", request.getRequestNo());
        params.put("applicantUserId", request.getApplicantUserId());
        params.put("templateCode", templateCode);
        params.put("requestStatus", request.getRequestStatus());
        params.put("businessSnapshotJson", request.getFormDataJson());
        params.put("businessType", resolveAttendanceBusinessType(templateCode));
        params.put("leaveType", getString(formData, "leaveType"));
        params.put("startDate", DateUtils.parseDate(getString(formData, "startDate")));
        params.put("endDate", DateUtils.parseDate(getString(formData, "endDate")));
        params.put("destination", getString(formData, "destination"));
        params.put("emergencyContact", getString(formData, "emergencyContact"));
        params.put("leaveRequestNo", getString(formData, "leaveRequestNo"));
        params.put("actualReturnDate", DateUtils.parseDate(getString(formData, "actualReturnDate")));
        params.put("cancelRemark", getString(formData, "cancelRemark"));
        params.put("leaveSchoolDate", DateUtils.parseDate(getString(formData, "leaveSchoolDate")));
        params.put("expectedReturnDate", DateUtils.parseDate(getString(formData, "expectedReturnDate")));
        params.put("location", getString(formData, "location"));
        params.put("healthStatus", getString(formData, "healthStatus"));
        params.put("returnDate", DateUtils.parseDate(getString(formData, "returnDate")));
        params.put("travelSummary", getString(formData, "travelSummary"));
        params.put("activityName", getString(formData, "activityName"));
        params.put("activityDate", DateUtils.parseDate(getString(formData, "activityDate")));
        params.put("venue", getString(formData, "venue"));
        params.put("participantCount", getInteger(formData, "participantCount"));
        params.put("mentorTeacherId", getLong(formData, "mentorTeacherId"));
        params.put("mentorTeacherName", resolveUserName(getLong(formData, "mentorTeacherId")));
        params.put("reason", resolveSpecialReason(formData));
        params.put("competitionName", getString(formData, "competitionName"));
        params.put("competitionLevel", getString(formData, "competitionLevel"));
        params.put("competitionDate", DateUtils.parseDate(getString(formData, "competitionDate")));
        params.put("teamInfo", getString(formData, "teamInfo"));
        params.put("projectName", getString(formData, "projectName"));
        params.put("projectType", getString(formData, "projectType"));
        params.put("fundingNeed", getDecimal(formData, "fundingNeed"));
        params.put("evaluationTerm", getString(formData, "evaluationTerm"));
        params.put("achievementSummary", getString(formData, "achievementSummary"));
        params.put("volunteerHours", getDecimal(formData, "volunteerHours"));
        params.put("textbookType", getString(formData, "textbookType"));
        params.put("courseName", getString(formData, "courseName"));
        params.put("isbn", getString(formData, "isbn"));
        params.put("quantity", getInteger(formData, "quantity"));
        params.put("effectiveDate", DateUtils.parseDate(getString(formData, "effectiveDate")));
        params.put("expectedResumeDate", DateUtils.parseDate(getString(formData, "expectedResumeDate")));
        params.put("targetMajor", getString(formData, "targetMajor"));
        params.put("recoveryProof", getString(formData, "recoveryProof"));
        params.put("guardianPhone", getString(formData, "guardianPhone"));
        params.put("beforeStatusCode", request.getCurrentStudentStatusCode());
        params.put("beforeStatusName", request.getCurrentStudentStatusName());
        params.put("afterStatusCode", request.getTargetStudentStatusCode());
        params.put("afterStatusName", request.getTargetStudentStatusName());
        return params;
    }

    private String resolveAttendanceBusinessType(String templateCode) {
        if ("ASK_LEAVE_CANCEL".equals(templateCode)) {
            return "CANCEL";
        }
        if ("LEAVE_RETURN_CONFIRM".equals(templateCode)) {
            return "RETURN";
        }
        if ("ASK_LEAVE_STANDARD".equals(templateCode)) {
            return "LEAVE";
        }
        if ("LEAVE_RETURN_STANDARD".equals(templateCode)) {
            return "LEAVE";
        }
        return "";
    }

    private String resolveSpecialReason(Map<String, Object> formData) {
        String reason = getString(formData, "reason");
        if (StringUtils.isNotEmpty(reason)) {
            return reason;
        }
        String cancelRemark = getString(formData, "cancelRemark");
        if (StringUtils.isNotEmpty(cancelRemark)) {
            return cancelRemark;
        }
        return getString(formData, "travelSummary");
    }

    private String resolveUserName(Long userId) {
        if (userId == null) {
            return "";
        }
        SysUser user = sysUserService.selectUserById(userId);
        return user == null ? "" : StringUtils.defaultIfEmpty(user.getRealName(), StringUtils.defaultIfEmpty(user.getNickName(), user.getUserName()));
    }

    private Long resolveAdvisorReviewerUserId(Long applicantUserId) {
        if (applicantUserId == null) {
            return null;
        }
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(applicantUserId);
        if (profile == null) {
            return null;
        }
        if (profile.getAdvisorUserId() != null) {
            return profile.getAdvisorUserId();
        }
        if (profile.getClassId() == null) {
            return null;
        }
        ScClass scClass = scClassService.selectScClassByClassId(profile.getClassId());
        return scClass == null ? null : scClass.getHeadTeacherId();
    }

    private String resolveDeptName(Long deptId) {
        if (deptId == null) {
            return "";
        }
        SysDept dept = sysDeptService.selectDeptById(deptId);
        return dept == null ? "" : StringUtils.defaultString(dept.getDeptName());
    }

    private String buildRequestNo() {
        return "AF" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    private Set<String> parseRoleSet(String roles) {
        if (StringUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }
        return List.of(roles.split(",")).stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .map(StringUtils::lowerCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<Map<String, Object>> parseArray(String json) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception ex) {
            throw new ServiceException("JSON 数据解析失败");
        }
    }

    private Map<String, Object> parseObjectMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new ServiceException("表单数据格式不正确");
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Collections.emptyMap() : value);
        } catch (Exception ex) {
            throw new ServiceException("JSON 序列化失败");
        }
    }

    private String getString(Map<String, Object> source, String key) {
        if (source == null || StringUtils.isEmpty(key)) {
            return "";
        }
        Object value = source.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private Long getLong(Map<String, Object> source, String key) {
        if (source == null || StringUtils.isEmpty(key)) {
            return null;
        }
        Object value = source.get(key);
        if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ex) {
            throw new ServiceException("数值字段格式不正确：" + key);
        }
    }

    private Integer getInteger(Map<String, Object> source, String key) {
        Long value = getLong(source, key);
        return value == null ? 0 : value.intValue();
    }

    private java.math.BigDecimal getDecimal(Map<String, Object> source, String key) {
        if (source == null || StringUtils.isEmpty(key)) {
            return null;
        }
        Object value = source.get(key);
        if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
            return null;
        }
        try {
            return new java.math.BigDecimal(String.valueOf(value));
        } catch (Exception ex) {
            throw new ServiceException("数值字段格式不正确：" + key);
        }
    }

    private List<Long> getLongList(Object value) {
        if (!(value instanceof List<?> values)) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        for (Object item : values) {
            if (item == null || StringUtils.isEmpty(String.valueOf(item))) {
                continue;
            }
            try {
                result.add(Long.valueOf(String.valueOf(item)));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private List<String> getStringList(Object value) {
        if (!(value instanceof List<?> values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toOptionList(Object options) {
        if (options instanceof List<?>) {
            return ((List<?>) options).stream()
                    .filter(Map.class::isInstance)
                    .map(item -> (Map<String, Object>) item)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
