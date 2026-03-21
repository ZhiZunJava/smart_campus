package com.smart.web.controller.campus;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.smart.system.domain.ScQuestionItem;
import com.smart.system.domain.dto.QuestionItemUpsertDto;
import com.smart.system.service.IScQuestionItemService;

@RestController
@RequestMapping("/campus/questionItem")
public class ScQuestionItemController extends BaseController {
    @Autowired
    private IScQuestionItemService scQuestionItemService;

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/list")
    public TableDataInfo list(ScQuestionItem scQuestionItem) {
        startPage();
        List<ScQuestionItem> list = scQuestionItemService.selectScQuestionItemList(scQuestionItem);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/{itemId}")
    public AjaxResult getInfo(@PathVariable Long itemId) {
        return success(scQuestionItemService.selectScQuestionItemByItemId(itemId));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/{itemId}/versions")
    public AjaxResult versionList(@PathVariable Long itemId) {
        return success(scQuestionItemService.selectScQuestionItemVersionByItemId(itemId));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/legacy/{questionId}/versions")
    public AjaxResult legacyVersionList(@PathVariable Long questionId) {
        ScQuestionItem item = scQuestionItemService.selectScQuestionItemBySource("LEGACY", questionId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("item", item);
        result.put("versions", item == null ? List.of() : scQuestionItemService.selectScQuestionItemVersionByItemId(item.getItemId()));
        return success(result);
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @Log(title = "版本化题目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionItemUpsertDto dto) {
        return success(scQuestionItemService.insertScQuestionItem(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @Log(title = "版本化题目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionItemUpsertDto dto) {
        return success(scQuestionItemService.updateScQuestionItem(dto, getUsername()));
    }
}
