package com.smart.web.controller.campus;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.utils.poi.ExcelUtil;
import com.smart.system.domain.ScStudentScore;
import com.smart.system.domain.dto.ScStudentScoreImportDto;
import com.smart.system.domain.dto.ScStudentScorePublishDto;
import com.smart.system.service.IScStudentScoreService;

@RestController
@RequestMapping("/campus/studentScore")
public class ScStudentScoreController extends BaseController {
    @Autowired
    private IScStudentScoreService scStudentScoreService;

    @PreAuthorize("@ss.hasPermi('campus:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScStudentScore scStudentScore) {
        startPage();
        return getDataTable(scStudentScoreService.selectScStudentScoreList(scStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:list')")
    @GetMapping("/overview")
    public AjaxResult overview(ScStudentScore scStudentScore) {
        return success(scStudentScoreService.buildScoreOverview(scStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:query')")
    @GetMapping("/{scoreId}")
    public AjaxResult getInfo(@PathVariable Long scoreId) {
        return success(scStudentScoreService.selectScStudentScoreByScoreId(scoreId));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:add')")
    @Log(title = "课程成绩", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScStudentScore scStudentScore) {
        scStudentScore.setCreateBy(getUsername());
        return toAjax(scStudentScoreService.insertScStudentScore(scStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScStudentScore scStudentScore) {
        scStudentScore.setUpdateBy(getUsername());
        return toAjax(scStudentScoreService.updateScStudentScore(scStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-save")
    public AjaxResult batchSave(@RequestBody List<ScStudentScore> scores) {
        return success(scStudentScoreService.saveBatchScores(scores, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:add')")
    @Log(title = "课程成绩", businessType = BusinessType.INSERT)
    @PostMapping("/init/{classCourseId}")
    public AjaxResult init(@PathVariable Long classCourseId) {
        return success(scStudentScoreService.initScoresByClassCourse(classCourseId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.UPDATE)
    @PostMapping("/sync/{classCourseId}")
    public AjaxResult sync(@PathVariable Long classCourseId) {
        return success(scStudentScoreService.syncExamScores(classCourseId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.UPDATE)
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody ScStudentScorePublishDto dto) {
        return success(scStudentScoreService.publishScores(dto, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, Long classCourseId,
            @RequestParam(value = "updateSupport", defaultValue = "true") boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }
        if (classCourseId == null) {
            return error("请先选择班级课程");
        }
        ExcelUtil<ScStudentScoreImportDto> util = new ExcelUtil<ScStudentScoreImportDto>(ScStudentScoreImportDto.class);
        List<ScStudentScoreImportDto> rows = util.importExcel(file.getInputStream());
        return success(scStudentScoreService.importStudentScores(classCourseId, rows, updateSupport, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:score:query')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, Long classCourseId) {
        ExcelUtil<ScStudentScoreImportDto> util = new ExcelUtil<ScStudentScoreImportDto>(ScStudentScoreImportDto.class);
        if (classCourseId == null) {
            util.importTemplateExcel(response, "课程成绩导入模板");
            return;
        }
        List<ScStudentScoreImportDto> rows = scStudentScoreService.buildImportTemplateRows(classCourseId);
        util.exportExcel(response, rows, "课程成绩导入模板");
    }

    @PreAuthorize("@ss.hasPermi('campus:score:remove')")
    @Log(title = "课程成绩", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds) {
        return toAjax(scStudentScoreService.deleteScStudentScoreByScoreIds(scoreIds));
    }
}
