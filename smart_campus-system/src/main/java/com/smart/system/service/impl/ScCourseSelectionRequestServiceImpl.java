package com.smart.system.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import org.springframework.web.util.HtmlUtils;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseSelectionRequest;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.SysNotice;
import com.smart.system.domain.dto.CourseSelectionRequestReviewDto;
import com.smart.system.mapper.ScCourseSelectionRequestMapper;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.IScCourseSelectionRequestService;
import com.smart.system.service.IScCourseStudentService;
import com.smart.system.service.IScUserProfileService;
import com.smart.system.service.ISysNoticeService;

@Service
public class ScCourseSelectionRequestServiceImpl implements IScCourseSelectionRequestService {
    @Autowired
    private ScCourseSelectionRequestMapper scCourseSelectionRequestMapper;

    @Autowired
    private IScCourseStudentService scCourseStudentService;

    @Autowired
    private IScClassCourseService scClassCourseService;

    @Autowired
    private IScUserProfileService scUserProfileService;

    @Autowired
    private ISysNoticeService noticeService;

    @Override
    public ScCourseSelectionRequest selectScCourseSelectionRequestByRequestId(Long requestId) {
        return scCourseSelectionRequestMapper.selectScCourseSelectionRequestByRequestId(requestId);
    }

    @Override
    public List<ScCourseSelectionRequest> selectScCourseSelectionRequestList(ScCourseSelectionRequest request) {
        return scCourseSelectionRequestMapper.selectScCourseSelectionRequestList(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createRequest(ScCourseSelectionRequest request, String operator) {
        if (request == null || request.getStudentUserId() == null || request.getTargetClassCourseId() == null) {
            throw new ServiceException("申请参数不能为空");
        }
        ScUserProfile profile = requireStudentProfile(request.getStudentUserId());
        ScClassCourse classCourse = requireClassCourse(request.getTargetClassCourseId());
        String requestType = StringUtils.upperCase(StringUtils.defaultIfEmpty(request.getRequestType(), "ADD"));
        if ("DROP".equals(requestType)) {
            if (scCourseStudentService.findActiveSelection(request.getStudentUserId(), request.getTargetClassCourseId()) == null) {
                throw new ServiceException("当前教学班未在你的已选课程中，无法申请退课");
            }
        } else {
            if (scCourseStudentService.findActiveSelection(request.getStudentUserId(), request.getTargetClassCourseId()) != null) {
                throw new ServiceException("当前教学班已在你的已选课程中");
            }
            if (isStandardSelectable(profile, classCourse) && hasAvailableSeats(classCourse)) {
                throw new ServiceException("当前教学班支持直接选课，请前往选课中心办理");
            }
        }
        validatePendingDuplicate(request);
        request.setRequestNo(buildRequestNo());
        request.setRequestType(requestType);
        request.setRequestStatus("0");
        request.setCreateBy(operator);
        scCourseSelectionRequestMapper.insertScCourseSelectionRequest(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getRequestId());
        result.put("requestNo", request.getRequestNo());
        result.put("requestStatus", request.getRequestStatus());
        result.put("message", "个性化选课申请已提交");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelRequest(Long requestId, Long studentUserId, String operator) {
        ScCourseSelectionRequest request = requireRequest(requestId);
        if (!Objects.equals(request.getStudentUserId(), studentUserId)) {
            throw new ServiceException("无权取消其他学生的申请");
        }
        if (!"0".equals(request.getRequestStatus())) {
            throw new ServiceException("当前申请已处理，无法取消");
        }
        request.setRequestStatus("3");
        request.setCancelTime(new Date());
        request.setUpdateBy(operator);
        scCourseSelectionRequestMapper.updateScCourseSelectionRequest(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getRequestId());
        result.put("requestStatus", request.getRequestStatus());
        result.put("message", "申请已撤回");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reviewRequest(CourseSelectionRequestReviewDto dto, String operator, Long reviewerUserId) {
        if (dto == null || dto.getRequestId() == null || StringUtils.isEmpty(dto.getRequestStatus())) {
            throw new ServiceException("审核参数不能为空");
        }
        ScCourseSelectionRequest request = requireRequest(dto.getRequestId());
        if (!"0".equals(request.getRequestStatus())) {
            throw new ServiceException("当前申请已处理，请勿重复审核");
        }
        String nextStatus = "1".equals(dto.getRequestStatus()) ? "1" : "2";
        request.setRequestStatus(nextStatus);
        request.setReviewRemark(StringUtils.trimToEmpty(dto.getReviewRemark()));
        request.setReviewUserId(reviewerUserId);
        request.setReviewTime(new Date());
        request.setUpdateBy(operator);
        if ("1".equals(nextStatus)) {
            if ("DROP".equalsIgnoreCase(request.getRequestType())) {
                scCourseStudentService.cancelCourseByApproval(request.getStudentUserId(), request.getTargetClassCourseId(), operator);
            } else {
                scCourseStudentService.selectCourseByApproval(request.getStudentUserId(), request.getTargetClassCourseId(), operator);
            }
        }
        scCourseSelectionRequestMapper.updateScCourseSelectionRequest(request);
        pushReviewResultNotice(request, operator);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getRequestId());
        result.put("requestStatus", request.getRequestStatus());
        if ("1".equals(nextStatus)) {
            result.put("message", "DROP".equalsIgnoreCase(request.getRequestType()) ? "退课申请已通过，学生已退出教学班" : "选课申请已通过，学生已加入教学班");
        } else {
            result.put("message", "申请已驳回");
        }
        return result;
    }

    private void validatePendingDuplicate(ScCourseSelectionRequest request) {
        ScCourseSelectionRequest duplicate = scCourseSelectionRequestMapper.selectPendingByStudentAndClassCourse(request);
        if (duplicate != null) {
            throw new ServiceException("你已提交过该教学班的申请，请等待审核结果");
        }
    }

    private void pushReviewResultNotice(ScCourseSelectionRequest request, String operator) {
        if (request == null || request.getStudentUserId() == null) {
            return;
        }
        String requestType = "DROP".equalsIgnoreCase(StringUtils.defaultIfEmpty(request.getRequestType(), "ADD"))
                ? "退课"
                : "选课";
        boolean approved = "1".equals(StringUtils.defaultIfEmpty(request.getRequestStatus(), ""));
        String courseName = StringUtils.defaultIfEmpty(request.getTargetCourseName(), "目标课程");
        String className = StringUtils.defaultIfEmpty(request.getTargetClassName(), "目标教学班");
        String teachingClassCode = StringUtils.defaultIfEmpty(request.getTargetTeachingClassCode(), "");
        String resultText = approved
                ? ("DROP".equalsIgnoreCase(request.getRequestType())
                        ? "审核已通过，系统已为你完成退课处理。"
                        : "审核已通过，系统已为你完成选课处理。")
                : "审核未通过，请根据审核说明调整后重新提交申请。";
        String reviewRemark = StringUtils.trimToEmpty(request.getReviewRemark());
        if (StringUtils.isEmpty(reviewRemark)) {
            reviewRemark = approved ? "可前往“我的申请”或“我的课程”查看最新结果。" : "如有疑问，请联系教务老师进一步确认。";
        }

        SysNotice notice = new SysNotice();
        notice.setNoticeTitle("个性化" + requestType + "申请" + (approved ? "审核通过" : "审核未通过"));
        notice.setNoticeType("1");
        notice.setStatus("0");
        notice.setBizCategory("MESSAGE");
        notice.setReceiverScope("CUSTOM");
        notice.setReceiverUserIds(String.valueOf(request.getStudentUserId()));
        notice.setActionType("courseSelectionReview");
        notice.setActionPath(buildReviewActionPath(request));
        notice.setActionTargetId(request.getRequestId());
        notice.setNoticeSummary(String.format("%s《%s》申请结果已更新，请查看审核结论。", requestType, courseName));
        notice.setNoticeContent(buildReviewNoticeContent(request, requestType, courseName, className, teachingClassCode,
                resultText, reviewRemark));
        notice.setCreateBy(operator);
        notice.setRemark("COURSE_SELECTION_REVIEW");
        noticeService.insertNotice(notice);
    }

    private String buildReviewActionPath(ScCourseSelectionRequest request) {
        StringBuilder path = new StringBuilder("/student/personalized-selection?tab=mine");
        if (request != null && request.getTermId() != null) {
            path.append("&termId=").append(request.getTermId());
        }
        return path.toString();
    }

    private String buildReviewNoticeContent(ScCourseSelectionRequest request, String requestType, String courseName,
            String className, String teachingClassCode, String resultText, String reviewRemark) {
        if (courseName == null) { courseName = ""; }
        if (className == null) { className = ""; }
        if (teachingClassCode == null) { teachingClassCode = ""; }
        if (reviewRemark == null) { reviewRemark = ""; }
        String safeCourseName = HtmlUtils.htmlEscape(courseName);
        String safeClassName = HtmlUtils.htmlEscape(className);
        String safeTeachingClassCode = HtmlUtils.htmlEscape(teachingClassCode);
        String reqNo = (request != null && request.getRequestNo() != null) ? request.getRequestNo() : "";
        String safeRequestNo = HtmlUtils.htmlEscape(reqNo);
        String safeReviewRemark = HtmlUtils.htmlEscape(reviewRemark);
        StringBuilder content = new StringBuilder();
        content.append("<div style='line-height:1.8;'>");
        content.append("<p>你的个性化").append(requestType).append("申请结果已更新。</p>");
        content.append("<p><strong>课程：</strong>").append(safeCourseName).append("</p>");
        content.append("<p><strong>教学班：</strong>").append(safeClassName);
        if (StringUtils.isNotEmpty(teachingClassCode)) {
            content.append("/").append(safeTeachingClassCode);
        }
        content.append("</p>");
        if (StringUtils.isNotEmpty(safeRequestNo)) {
            content.append("<p><strong>申请编号：</strong>").append(safeRequestNo).append("</p>");
        }
        content.append("<p><strong>审核结果：</strong>").append(resultText).append("</p>");
        content.append("<p><strong>审核说明：</strong>”).append(safeReviewRemark).append(“</p>");
        content.append("<p>你可以前往”选课申请”页面查看完整记录，消息中心也会保留本次通知。</p>");
        content.append("</div>");
        return content.toString();
    }

    private ScCourseSelectionRequest requireRequest(Long requestId) {
        ScCourseSelectionRequest request = scCourseSelectionRequestMapper.selectScCourseSelectionRequestByRequestId(requestId);
        if (request == null) {
            throw new ServiceException("申请记录不存在");
        }
        return request;
    }

    private ScUserProfile requireStudentProfile(Long studentUserId) {
        ScUserProfile profile = scUserProfileService.selectScUserProfileByUserId(studentUserId);
        if (profile == null) {
            throw new ServiceException("学生档案不存在");
        }
        return profile;
    }

    private ScClassCourse requireClassCourse(Long classCourseId) {
        ScClassCourse classCourse = scClassCourseService.selectScClassCourseById(classCourseId);
        if (classCourse == null) {
            throw new ServiceException("目标教学班不存在");
        }
        if (!"0".equals(StringUtils.defaultIfEmpty(classCourse.getStatus(), "0"))) {
            throw new ServiceException("目标教学班已停用");
        }
        return classCourse;
    }

    private boolean isStandardSelectable(ScUserProfile profile, ScClassCourse classCourse) {
        if (profile == null || classCourse == null) {
            return false;
        }
        if (Objects.equals(profile.getClassId(), classCourse.getClassId())) {
            return true;
        }
        if ("Y".equalsIgnoreCase(StringUtils.defaultIfEmpty(classCourse.getRequiredFlag(), "N"))) {
            return false;
        }
        String classCourseMajor = StringUtils.trimToEmpty(classCourse.getMajor());
        String studentMajor = StringUtils.trimToEmpty(profile.getMajor());
        if (StringUtils.isEmpty(classCourseMajor) || StringUtils.isEmpty(studentMajor)) {
            return true;
        }
        return StringUtils.equalsIgnoreCase(classCourseMajor, studentMajor);
    }

    private boolean hasAvailableSeats(ScClassCourse classCourse) {
        if (classCourse == null || classCourse.getId() == null) {
            return false;
        }
        if (classCourse.getStudentLimit() == null || classCourse.getStudentLimit() <= 0) {
            return true;
        }
        int currentCount = scCourseStudentService.countActiveByClassCourseId(classCourse.getId());
        return currentCount < classCourse.getStudentLimit();
    }

    private String buildRequestNo() {
        return "PSR" + System.currentTimeMillis() + String.format("%06d", (int) (Math.random() * 1000000));
    }
}
