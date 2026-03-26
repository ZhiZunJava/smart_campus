package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourseSelectionPlan;

public interface ScCourseSelectionPlanMapper {
    ScCourseSelectionPlan selectScCourseSelectionPlanByPlanId(Long planId);

    List<ScCourseSelectionPlan> selectScCourseSelectionPlanList(ScCourseSelectionPlan plan);

    int insertScCourseSelectionPlan(ScCourseSelectionPlan plan);

    int updateScCourseSelectionPlan(ScCourseSelectionPlan plan);

    int deleteScCourseSelectionPlanByPlanId(Long planId);

    int deleteScCourseSelectionPlanByPlanIds(Long[] planIds);
}
