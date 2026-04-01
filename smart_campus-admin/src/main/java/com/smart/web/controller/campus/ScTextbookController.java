package com.smart.web.controller.campus;

import java.util.List;
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
import com.smart.system.service.IScTextbookService;

@RestController
@RequestMapping("/campus/textbook")
public class ScTextbookController extends BaseController {
    @Autowired
    private IScTextbookService scTextbookService;

    @PreAuthorize("@ss.hasPermi('campus:textbook:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScTextbook query) {
        startPage();
        List<ScTextbook> list = scTextbookService.selectScTextbookList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:textbook:query')")
    @GetMapping("/{textbookId}")
    public AjaxResult getInfo(@PathVariable Long textbookId) {
        return success(scTextbookService.selectScTextbookByTextbookId(textbookId));
    }

    @GetMapping("/options")
    public AjaxResult options() {
        ScTextbook query = new ScTextbook();
        query.setStatus("0");
        return success(scTextbookService.selectScTextbookList(query));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbook:add')")
    @Log(title = "教材目录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScTextbook scTextbook) {
        scTextbook.setCreateBy(getUsername());
        return toAjax(scTextbookService.insertScTextbook(scTextbook));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbook:edit')")
    @Log(title = "教材目录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScTextbook scTextbook) {
        scTextbook.setUpdateBy(getUsername());
        return toAjax(scTextbookService.updateScTextbook(scTextbook));
    }

    @PreAuthorize("@ss.hasPermi('campus:textbook:remove')")
    @Log(title = "教材目录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{textbookIds}")
    public AjaxResult remove(@PathVariable Long[] textbookIds) {
        return toAjax(scTextbookService.deleteScTextbookByTextbookIds(textbookIds));
    }
}
