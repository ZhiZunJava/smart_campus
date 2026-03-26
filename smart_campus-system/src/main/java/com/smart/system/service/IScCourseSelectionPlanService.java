package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScCourseSelectionPlan;

public interface IScCourseSelectionPlanService {
    ScCourseSelectionPlan selectScCourseSelectionPlanByPlanId(Long planId);

    List<ScCourseSelectionPlan> selectScCourseSelectionPlanList(ScCourseSelectionPlan plan);

    int insertScCourseSelectionPlan(ScCourseSelectionPlan plan);

    int updateScCourseSelectionPlan(ScCourseSelectionPlan plan);

    int deleteScCourseSelectionPlanByPlanIds(Long[] planIds);

    ScCourseSelectionPlan resolveActivePlan(Long termId);
}
