package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScTextbookPlan;
import com.smart.system.mapper.ScTextbookPlanMapper;
import com.smart.system.service.IScTextbookPlanService;

@Service
public class ScTextbookPlanServiceImpl implements IScTextbookPlanService {
    @Autowired
    private ScTextbookPlanMapper scTextbookPlanMapper;

    @Override
    public ScTextbookPlan selectScTextbookPlanByPlanId(Long planId) {
        return scTextbookPlanMapper.selectScTextbookPlanByPlanId(planId);
    }

    @Override
    public List<ScTextbookPlan> selectScTextbookPlanList(ScTextbookPlan query) {
        return scTextbookPlanMapper.selectScTextbookPlanList(query);
    }

    @Override
    public int insertScTextbookPlan(ScTextbookPlan plan) {
        if (plan.getPlanQuantity() == null) {
            plan.setPlanQuantity(0);
        }
        if (plan.getDistributedQuantity() == null) {
            plan.setDistributedQuantity(0);
        }
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus("DRAFT");
        }
        return scTextbookPlanMapper.insertScTextbookPlan(plan);
    }

    @Override
    public int updateScTextbookPlan(ScTextbookPlan plan) {
        return scTextbookPlanMapper.updateScTextbookPlan(plan);
    }

    @Override
    public int deleteScTextbookPlanByPlanIds(Long[] planIds) {
        return scTextbookPlanMapper.deleteScTextbookPlanByPlanIds(planIds);
    }

    @Override
    public int incrementDistributedQuantity(Long textbookId, Long termId, int quantity) {
        if (textbookId == null || termId == null || quantity <= 0) {
            return 0;
        }
        return scTextbookPlanMapper.incrementDistributedQuantity(textbookId, termId, quantity);
    }
}
