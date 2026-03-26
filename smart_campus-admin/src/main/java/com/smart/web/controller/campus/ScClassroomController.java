package com.smart.web.controller.campus;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.utils.poi.ExcelUtil;
import com.smart.system.domain.ScClassroom;
import com.smart.system.service.IScClassroomService;

@RestController
@RequestMapping("/campus/classroom")
public class ScClassroomController extends BaseController {
    @Autowired
    private IScClassroomService scClassroomService;

    @PreAuthorize("@ss.hasPermi('campus:classroom:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScClassroom scClassroom) {
        startPage();
        List<ScClassroom> list = scClassroomService.selectScClassroomList(scClassroom);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:export')")
    @Log(title = "教室管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ScClassroom scClassroom) {
        List<ScClassroom> list = scClassroomService.selectScClassroomList(scClassroom);
        ExcelUtil<ScClassroom> util = new ExcelUtil<ScClassroom>(ScClassroom.class);
        util.exportExcel(response, list, "教室数据");
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:import')")
    @Log(title = "教室管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateSupport", defaultValue = "true") boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }
        ExcelUtil<ScClassroom> util = new ExcelUtil<ScClassroom>(ScClassroom.class);
        List<ScClassroom> rows = util.importExcel(file.getInputStream());
        return success(scClassroomService.importScClassroom(rows, updateSupport, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<ScClassroom> util = new ExcelUtil<ScClassroom>(ScClassroom.class);
        util.importTemplateExcel(response, "教室数据导入模板");
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:query')")
    @GetMapping("/{classroomId}")
    public AjaxResult getInfo(@PathVariable Long classroomId) {
        return success(scClassroomService.selectScClassroomByClassroomId(classroomId));
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:add')")
    @Log(title = "教室管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScClassroom scClassroom) {
        scClassroom.setCreateBy(getUsername());
        return toAjax(scClassroomService.insertScClassroom(scClassroom));
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:edit')")
    @Log(title = "教室管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScClassroom scClassroom) {
        scClassroom.setUpdateBy(getUsername());
        return toAjax(scClassroomService.updateScClassroom(scClassroom));
    }

    @PreAuthorize("@ss.hasPermi('campus:classroom:remove')")
    @Log(title = "教室管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{classroomIds}")
    public AjaxResult remove(@PathVariable Long[] classroomIds) {
        return toAjax(scClassroomService.deleteScClassroomByClassroomIds(classroomIds));
    }
}
