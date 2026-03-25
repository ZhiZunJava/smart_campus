package com.smart.web.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.web.domain.OfficePreviewTaskVo;
import com.smart.web.service.OfficePreviewService;

@RestController
@RequestMapping("/common/office")
public class OfficePreviewController {
    private final OfficePreviewService officePreviewService;

    public OfficePreviewController(OfficePreviewService officePreviewService) {
        this.officePreviewService = officePreviewService;
    }

    @PostMapping("/preview")
    public AjaxResult submit(@RequestParam String resource) {
        OfficePreviewTaskVo task = officePreviewService.submitPreviewTask(resource);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("taskId", task.getTaskId());
        ajax.put("status", task.getStatus());
        ajax.put("fileName", task.getFileName());
        ajax.put("message", task.getMessage());
        return ajax;
    }

    @GetMapping("/preview/status")
    public AjaxResult status(@RequestParam String taskId) {
        OfficePreviewTaskVo task = officePreviewService.getPreviewTask(taskId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("taskId", task.getTaskId());
        ajax.put("status", task.getStatus());
        ajax.put("fileName", task.getFileName());
        ajax.put("message", task.getMessage());
        return ajax;
    }
}
