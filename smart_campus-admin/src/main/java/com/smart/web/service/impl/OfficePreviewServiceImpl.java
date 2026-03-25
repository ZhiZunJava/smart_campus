package com.smart.web.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.stereotype.Service;
import com.smart.common.config.RuoYiConfig;
import com.smart.common.constant.Constants;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.common.utils.file.FileUtils;
import com.smart.web.core.config.OfficePreviewProperties;
import com.smart.web.domain.OfficePreviewTaskVo;
import com.smart.web.service.OfficePreviewService;

@Service
public class OfficePreviewServiceImpl implements OfficePreviewService {
    private final OfficePreviewProperties properties;
    private final Map<String, OfficePreviewTaskVo> taskStore = new ConcurrentHashMap<>();
    private final Map<String, Path> taskTargetStore = new ConcurrentHashMap<>();

    public OfficePreviewServiceImpl(OfficePreviewProperties properties) {
        this.properties = properties;
    }

    @Override
    public OfficePreviewTaskVo submitPreviewTask(String resourcePath) {
        if (!properties.isEnabled()) {
            throw new ServiceException("Office 预览功能未启用");
        }
        if (StringUtils.isEmpty(resourcePath)) {
            throw new ServiceException("文件地址不能为空");
        }
        if (resourcePath.toLowerCase().endsWith(".pdf")) {
            OfficePreviewTaskVo ready = new OfficePreviewTaskVo();
            ready.setTaskId(buildTaskId(resourcePath));
            ready.setStatus("SUCCESS");
            ready.setFileName(resourcePath);
            ready.setMessage("PDF 文件无需转换");
            taskStore.put(ready.getTaskId(), ready);
            return ready;
        }

        try {
            Path source = resolveSourcePath(resourcePath);
            Path target = resolveTargetPath(source);
            String taskId = buildTaskId(source.toString());

            if (Files.exists(target) && Files.getLastModifiedTime(target).toMillis() >= Files.getLastModifiedTime(source).toMillis()) {
                OfficePreviewTaskVo ready = new OfficePreviewTaskVo();
                ready.setTaskId(taskId);
                ready.setStatus("SUCCESS");
                ready.setFileName(buildTargetUrl(target));
                ready.setMessage("命中已生成的 PDF 预览");
                taskStore.put(taskId, ready);
                taskTargetStore.put(taskId, target);
                return ready;
            }

            OfficePreviewTaskVo current = taskStore.get(taskId);
            if (current != null && ("PENDING".equals(current.getStatus()) || "PROCESSING".equals(current.getStatus()))) {
                return current;
            }

            OfficePreviewTaskVo task = new OfficePreviewTaskVo();
            task.setTaskId(taskId);
            task.setStatus("PENDING");
            task.setMessage("预览任务已提交");
            taskStore.put(taskId, task);
            taskTargetStore.put(taskId, target);

            CompletableFuture.runAsync(() -> processTask(taskId, source, target));
            return task;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Office 文件预览任务提交失败：" + e.getMessage());
        }
    }

    @Override
    public OfficePreviewTaskVo getPreviewTask(String taskId) {
        OfficePreviewTaskVo task = taskStore.get(taskId);
        if (task == null) {
            throw new ServiceException("预览任务不存在或已过期");
        }
        Path target = taskTargetStore.get(taskId);
        if (target != null && Files.exists(target) && !"SUCCESS".equals(task.getStatus())) {
            task.setStatus("SUCCESS");
            task.setFileName(buildTargetUrl(target));
            task.setMessage("检测到预览文件已生成");
        }
        return task;
    }

    private void processTask(String taskId, Path source, Path target) {
        OfficePreviewTaskVo task = taskStore.get(taskId);
        if (task == null) return;
        task.setStatus("PROCESSING");
        task.setMessage("正在生成预览文件");
        try {
            Files.createDirectories(target.getParent());
            Path workingDir = Paths.get(StringUtils.defaultIfEmpty(properties.getWorkingDir(), RuoYiConfig.getProfile() + "/office-work"));
            Files.createDirectories(workingDir);
            convert(source.toFile(), target.toFile(), workingDir.toFile());
            task.setStatus("SUCCESS");
            task.setFileName(buildTargetUrl(target));
            task.setMessage("预览文件生成成功");
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setMessage(e.getMessage());
        }
    }

    private Path resolveSourcePath(String resourcePath) {
        String cleanPath = FileUtils.stripPrefix(resourcePath);
        Path source = Paths.get(RuoYiConfig.getProfile(), cleanPath);
        if (!Files.exists(source)) {
            throw new ServiceException("源文件不存在，无法预览");
        }
        return source;
    }

    private Path resolveTargetPath(Path source) {
        String sourceName = source.getFileName().toString();
        int dotIndex = sourceName.lastIndexOf('.');
        String baseName = dotIndex > -1 ? sourceName.substring(0, dotIndex) : sourceName;
        String targetName = baseName + ".pdf";
        Path outputDir = Paths.get(StringUtils.defaultIfEmpty(properties.getOutputDir(), RuoYiConfig.getProfile() + "/preview"));
        return outputDir.resolve(targetName);
    }

    private String buildTargetUrl(Path target) {
        return Constants.RESOURCE_PREFIX + "/preview/" + target.getFileName();
    }

    private void convert(File source, File target, File workingDir) throws OfficeException {
        String officeHome = resolveOfficeHome();
        LocalOfficeManager.Builder builder = LocalOfficeManager.builder().workingDir(workingDir);
        if (StringUtils.isNotEmpty(officeHome)) {
            builder.officeHome(officeHome);
        }

        LocalOfficeManager officeManager = builder.build();
        try {
            officeManager.start();
            LocalConverter.make(officeManager).convert(source).to(target).execute();
        } catch (OfficeException e) {
            throw new ServiceException("请确认服务器已安装 LibreOffice，且 office.preview.officeHome 配置正确");
        } finally {
            officeManager.stop();
        }
    }

    private String resolveOfficeHome() {
        if (StringUtils.isNotEmpty(properties.getOfficeHome())) {
            return properties.getOfficeHome();
        }
        String[] candidates = {
                "C:/Program Files/LibreOffice",
                "C:/Program Files (x86)/LibreOffice",
                "/usr/lib/libreoffice",
                "/usr/lib64/libreoffice",
                "/opt/libreoffice",
                "/Applications/LibreOffice.app/Contents"
        };
        for (String candidate : candidates) {
            if (Files.exists(Paths.get(candidate))) {
                return candidate;
            }
        }
        throw new ServiceException("未找到 LibreOffice 安装目录，请在 application.yml 中配置 office.preview.officeHome");
    }

    private String buildTaskId(String seed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(seed.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            return String.valueOf(seed.hashCode());
        }
    }
}
