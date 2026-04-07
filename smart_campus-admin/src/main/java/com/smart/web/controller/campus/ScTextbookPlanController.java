package com.smart.web.controller.campus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.ScTextbook;
import com.smart.system.domain.ScTextbookPlan;
import com.smart.system.mapper.ScAffairSpecialRecordMapper;
import com.smart.system.service.IScTextbookPlanService;
import com.smart.system.service.IScTextbookService;

@RestController
@RequestMapping("/campus/textbook/plan")
public class ScTextbookPlanController extends BaseController {
    @Autowired
    private IScTextbookPlanService scTextbookPlanService;
    @Autowired
    private IScTextbookService scTextbookService;
    @Autowired
    private ScAffairSpecialRecordMapper scAffairSpecialRecordMapper;

    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScTextbookPlan query) {
        startPage();
        List<ScTextbookPlan> list = scTextbookPlanService.selectScTextbookPlanList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:query')")
    @GetMapping("/{planId}")
    public AjaxResult getInfo(@PathVariable Long planId) {
        return success(scTextbookPlanService.selectScTextbookPlanByPlanId(planId));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:add')")
    @Log(title = "教材计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScTextbookPlan plan) {
        plan.setCreateBy(getUsername());
        return toAjax(scTextbookPlanService.insertScTextbookPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:edit')")
    @Log(title = "教材计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScTextbookPlan plan) {
        plan.setUpdateBy(getUsername());
        return toAjax(scTextbookPlanService.updateScTextbookPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:remove')")
    @Log(title = "教材计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds) {
        return toAjax(scTextbookPlanService.deleteScTextbookPlanByPlanIds(planIds));
    }

    /**
     * 查看某教材计划的发放/领取记录
     * 通过planId找到教材的isbn，再查询审批通过的领取记录
     */
    @PreAuthorize("@ss.hasPermi('campus:textbookPlan:list')")
    @GetMapping("/claim-records/{planId}")
    public AjaxResult claimRecords(@PathVariable Long planId) {
        ScTextbookPlan plan = scTextbookPlanService.selectScTextbookPlanByPlanId(planId);
        if (plan == null || plan.getTextbookId() == null) {
            return success(new ArrayList<>());
        }
        ScTextbook textbook = scTextbookService.selectScTextbookByTextbookId(plan.getTextbookId());
        if (textbook == null || textbook.getIsbn() == null || textbook.getIsbn().isEmpty()) {
            return success(new ArrayList<>());
        }
        List<Map<String, Object>> records = scAffairSpecialRecordMapper.selectTextbookClaimRecordsByIsbn(textbook.getIsbn());
        if (records == null) {
            records = new ArrayList<>();
        }
        // 包装返回更多上下文信息
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("planId", plan.getPlanId());
        result.put("textbookName", textbook.getTextbookName());
        result.put("isbn", textbook.getIsbn());
        result.put("planQuantity", plan.getPlanQuantity());
        result.put("distributedQuantity", plan.getDistributedQuantity());
        result.put("claimRecords", records);
        result.put("claimCount", records.size());
        return success(result);
    }
}
