package com.smart.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.smart.system.domain.ScTextbookPlan;

public interface ScTextbookPlanMapper {
    ScTextbookPlan selectScTextbookPlanByPlanId(Long planId);

    List<ScTextbookPlan> selectScTextbookPlanList(ScTextbookPlan query);

    int insertScTextbookPlan(ScTextbookPlan plan);

    int updateScTextbookPlan(ScTextbookPlan plan);

    int deleteScTextbookPlanByPlanId(Long planId);

    int deleteScTextbookPlanByPlanIds(Long[] planIds);

    int incrementDistributedQuantity(@Param("textbookId") Long textbookId, @Param("termId") Long termId, @Param("quantity") int quantity);
}
