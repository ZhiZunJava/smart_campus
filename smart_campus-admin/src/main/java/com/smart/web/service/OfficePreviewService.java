package com.smart.web.service;

import com.smart.web.domain.OfficePreviewTaskVo;

public interface OfficePreviewService {
    OfficePreviewTaskVo submitPreviewTask(String resourcePath);

    OfficePreviewTaskVo getPreviewTask(String taskId);
}
