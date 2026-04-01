package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScTextbookPlan;

public interface IScTextbookPlanService {
    ScTextbookPlan selectScTextbookPlanByPlanId(Long planId);

    List<ScTextbookPlan> selectScTextbookPlanList(ScTextbookPlan query);

    int insertScTextbookPlan(ScTextbookPlan plan);

    int updateScTextbookPlan(ScTextbookPlan plan);

    int deleteScTextbookPlanByPlanIds(Long[] planIds);

    /**
     * 根据教材ID和学期ID递增已发放数量
     */
    int incrementDistributedQuantity(Long textbookId, Long termId, int quantity);
}
