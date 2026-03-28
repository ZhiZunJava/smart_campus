package com.smart.system.service.impl;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScCourseSelectionPlan;
import com.smart.system.mapper.ScCourseSelectionPlanMapper;
import com.smart.system.service.IScCourseSelectionPlanService;

@Service
public class ScCourseSelectionPlanServiceImpl implements IScCourseSelectionPlanService {
    @Autowired
    private ScCourseSelectionPlanMapper scCourseSelectionPlanMapper;

    @Override
    public ScCourseSelectionPlan selectScCourseSelectionPlanByPlanId(Long planId) {
        return scCourseSelectionPlanMapper.selectScCourseSelectionPlanByPlanId(planId);
    }

    @Override
    public List<ScCourseSelectionPlan> selectScCourseSelectionPlanList(ScCourseSelectionPlan plan) {
        return scCourseSelectionPlanMapper.selectScCourseSelectionPlanList(plan);
    }

    @Override
    public int insertScCourseSelectionPlan(ScCourseSelectionPlan plan) {
        validatePlan(plan);
        return scCourseSelectionPlanMapper.insertScCourseSelectionPlan(plan);
    }

    @Override
    public int updateScCourseSelectionPlan(ScCourseSelectionPlan plan) {
        validatePlan(plan);
        return scCourseSelectionPlanMapper.updateScCourseSelectionPlan(plan);
    }

    @Override
    public int deleteScCourseSelectionPlanByPlanIds(Long[] planIds) {
        if (planIds != null) {
            for (Long planId : planIds) {
                ScCourseSelectionPlan plan = scCourseSelectionPlanMapper.selectScCourseSelectionPlanByPlanId(planId);
                if (plan != null && "0".equals(plan.getStatus())) {
                    throw new ServiceException("不能删除启用中的选课计划【" + plan.getPlanName() + "】，请先停用");
                }
            }
        }
        return scCourseSelectionPlanMapper.deleteScCourseSelectionPlanByPlanIds(planIds);
    }

    @Override
    public ScCourseSelectionPlan resolveActivePlan(Long termId) {
        if (termId == null) {
            return null;
        }
        ScCourseSelectionPlan query = new ScCourseSelectionPlan();
        query.setTermId(termId);
        query.setStatus("0");
        List<ScCourseSelectionPlan> list = scCourseSelectionPlanMapper.selectScCourseSelectionPlanList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    private void validatePlan(ScCourseSelectionPlan plan) {
        if (plan == null || plan.getTermId() == null) {
            throw new ServiceException("学期不能为空");
        }
        plan.setPlanName(StringUtils.trimToEmpty(plan.getPlanName()));
        plan.setPlanType(StringUtils.upperCase(StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(plan.getPlanType()), "GENERAL")));
        plan.setStatus(StringUtils.defaultIfEmpty(StringUtils.trimToEmpty(plan.getStatus()), "0"));
        plan.setNoticeContent(StringUtils.trimToEmpty(plan.getNoticeContent()));
        plan.setRuleContent(StringUtils.trimToEmpty(plan.getRuleContent()));
        plan.setRemark(StringUtils.trimToEmpty(plan.getRemark()));

        if (StringUtils.isEmpty(plan.getPlanName())) {
            throw new ServiceException("选课计划名称不能为空");
        }
        if (plan.getNoticeContent() != null && plan.getNoticeContent().length() > 1000) {
            throw new ServiceException("选课公告不能超过1000字");
        }
        if (plan.getRuleContent() != null && plan.getRuleContent().length() > 1500) {
            throw new ServiceException("规则说明不能超过1500字");
        }
        validateWindowRange(plan.getSelectionStartTime(), plan.getSelectionEndTime(), "选课开放");
        validateWindowRange(plan.getDropStartTime(), plan.getDropEndTime(), "退课开放");
        validateWindowRange(plan.getRequestStartTime(), plan.getRequestEndTime(), "申请开放");

        boolean hasSelectionWindow = plan.getSelectionStartTime() != null || plan.getSelectionEndTime() != null;
        boolean hasDropWindow = plan.getDropStartTime() != null || plan.getDropEndTime() != null;
        boolean hasRequestWindow = plan.getRequestStartTime() != null || plan.getRequestEndTime() != null;
        if (!hasSelectionWindow && !hasDropWindow && !hasRequestWindow) {
            throw new ServiceException("至少需要配置一个开放时间段");
        }

        if ("0".equals(plan.getStatus())) {
            ScCourseSelectionPlan query = new ScCourseSelectionPlan();
            query.setTermId(plan.getTermId());
            query.setStatus("0");
            List<ScCourseSelectionPlan> activePlans = scCourseSelectionPlanMapper.selectScCourseSelectionPlanList(query);
            boolean hasConflict = activePlans.stream()
                    .anyMatch(item -> !Objects.equals(item.getPlanId(), plan.getPlanId()));
            if (hasConflict) {
                throw new ServiceException("同一学期只能启用一个选课计划，请先停用现有启用计划");
            }
        }
    }

    private void validateWindowRange(java.util.Date startTime, java.util.Date endTime, String label) {
        if ((startTime == null) != (endTime == null)) {
            throw new ServiceException(label + "开始和结束时间需要成对配置");
        }
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            throw new ServiceException(label + "开始时间不能晚于结束时间");
        }
    }
}
