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
import com.smart.system.domain.ScAffairWorkStudyJob;
import com.smart.system.service.IScAffairWorkStudyJobService;

@RestController
@RequestMapping("/campus/affair/workStudyJob")
public class ScAffairWorkStudyJobController extends BaseController {
    @Autowired
    private IScAffairWorkStudyJobService scAffairWorkStudyJobService;

    @PreAuthorize("@ss.hasPermi('campus:affairWorkStudyJob:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScAffairWorkStudyJob query) {
        startPage();
        List<ScAffairWorkStudyJob> list = scAffairWorkStudyJobService.selectScAffairWorkStudyJobList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:affairWorkStudyJob:query')")
    @GetMapping("/{jobId}")
    public AjaxResult getInfo(@PathVariable Long jobId) {
        return success(scAffairWorkStudyJobService.selectScAffairWorkStudyJobByJobId(jobId));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairWorkStudyJob:add')")
    @Log(title = "勤工助学岗位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScAffairWorkStudyJob job) {
        job.setCreateBy(getUsername());
        return toAjax(scAffairWorkStudyJobService.insertScAffairWorkStudyJob(job));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairWorkStudyJob:edit')")
    @Log(title = "勤工助学岗位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScAffairWorkStudyJob job) {
        job.setUpdateBy(getUsername());
        return toAjax(scAffairWorkStudyJobService.updateScAffairWorkStudyJob(job));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairWorkStudyJob:remove')")
    @Log(title = "勤工助学岗位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) {
        return toAjax(scAffairWorkStudyJobService.deleteScAffairWorkStudyJobByJobIds(jobIds));
    }
}
