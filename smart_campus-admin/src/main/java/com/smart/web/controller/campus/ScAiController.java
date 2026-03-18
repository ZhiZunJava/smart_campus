package com.smart.web.controller.campus;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.domain.ScAiTaskLog;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiModelTestDto;
import com.smart.system.domain.dto.AiPromptTestDto;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.IScAiPromptTemplateService;
import com.smart.system.service.IScAiTaskLogService;
import com.smart.system.service.ai.IAiGatewayService;

@RestController
@RequestMapping("/campus/ai")
public class ScAiController extends BaseController {
    @Autowired
    private IScAiModelConfigService scAiModelConfigService;
    @Autowired
    private IScAiPromptTemplateService scAiPromptTemplateService;
    @Autowired
    private IScAiTaskLogService scAiTaskLogService;
    @Autowired
    private IAiGatewayService aiGatewayService;

    @PreAuthorize("@ss.hasPermi('campus:ai:model:list')")
    @GetMapping("/model/list")
    public TableDataInfo modelList(ScAiModelConfig scAiModelConfig) {
        startPage();
        List<ScAiModelConfig> list = scAiModelConfigService.selectScAiModelConfigList(scAiModelConfig);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:add')")
    @Log(title = "AI模型配置", businessType = BusinessType.INSERT)
    @PostMapping("/model")
    public AjaxResult addModel(@Validated @RequestBody ScAiModelConfig scAiModelConfig) {
        scAiModelConfig.setCreateBy(getUsername());
        return toAjax(scAiModelConfigService.insertScAiModelConfig(scAiModelConfig));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:edit')")
    @Log(title = "AI模型配置", businessType = BusinessType.UPDATE)
    @PutMapping("/model")
    public AjaxResult editModel(@Validated @RequestBody ScAiModelConfig scAiModelConfig) {
        scAiModelConfig.setUpdateBy(getUsername());
        return toAjax(scAiModelConfigService.updateScAiModelConfig(scAiModelConfig));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:remove')")
    @Log(title = "AI模型配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/model/{modelIds}")
    public AjaxResult removeModel(@PathVariable Long[] modelIds) {
        return toAjax(scAiModelConfigService.deleteScAiModelConfigByModelIds(modelIds));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:prompt:list')")
    @GetMapping("/prompt/list")
    public TableDataInfo promptList(ScAiPromptTemplate scAiPromptTemplate) {
        startPage();
        List<ScAiPromptTemplate> list = scAiPromptTemplateService.selectScAiPromptTemplateList(scAiPromptTemplate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:prompt:add')")
    @Log(title = "Prompt模板", businessType = BusinessType.INSERT)
    @PostMapping("/prompt")
    public AjaxResult addPrompt(@Validated @RequestBody ScAiPromptTemplate scAiPromptTemplate) {
        scAiPromptTemplate.setCreateBy(getUsername());
        return toAjax(scAiPromptTemplateService.insertScAiPromptTemplate(scAiPromptTemplate));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:prompt:edit')")
    @Log(title = "Prompt模板", businessType = BusinessType.UPDATE)
    @PutMapping("/prompt")
    public AjaxResult editPrompt(@Validated @RequestBody ScAiPromptTemplate scAiPromptTemplate) {
        scAiPromptTemplate.setUpdateBy(getUsername());
        return toAjax(scAiPromptTemplateService.updateScAiPromptTemplate(scAiPromptTemplate));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:prompt:remove')")
    @Log(title = "Prompt模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/prompt/{templateIds}")
    public AjaxResult removePrompt(@PathVariable Long[] templateIds) {
        return toAjax(scAiPromptTemplateService.deleteScAiPromptTemplateByTemplateIds(templateIds));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:edit')")
    @PostMapping("/model/test")
    public AjaxResult testModel(@RequestBody AiModelTestDto testDto) {
        AiChatResponseVo responseVo = aiGatewayService.testModel(testDto);
        return success(responseVo);
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:edit')")
    @PostMapping("/model/test/stream")
    public SseEmitter testModelStream(@RequestBody AiModelTestDto testDto) {
        SseEmitter emitter = new SseEmitter(0L);
        try {
            AiChatRequestDto requestDto = new AiChatRequestDto();
            requestDto.setModelId(testDto.getModelId());
            requestDto.setBizType("model_test");
            requestDto.setSystemPrompt("You are a concise assistant.");
            requestDto.setUserPrompt(com.smart.common.utils.StringUtils.isEmpty(testDto.getPrompt()) ? "请只回复：连接正常"
                    : testDto.getPrompt());
            requestDto.setStream(Boolean.TRUE);
            requestDto.setDeepThinking(Boolean.FALSE);

            new Thread(() -> {
                try {
                    aiGatewayService.streamChat(requestDto, chunk -> {
                        try {
                            emitter.send(SseEmitter.event().name(chunk.getType()).data(chunk));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    emitter.send(SseEmitter.event().name("done").data("done"));
                } catch (Exception e) {
                    try {
                        emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                    } catch (IOException ignored) {
                    }
                } finally {
                    emitter.complete();
                }
            }).start();
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
            } catch (IOException ignored) {
            }
            emitter.complete();
        }
        return emitter;
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:model:edit')")
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody AiChatRequestDto requestDto) {
        AiChatResponseVo responseVo = aiGatewayService.chat(requestDto);
        return success(responseVo);
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:prompt:edit')")
    @PostMapping("/prompt/test")
    public AjaxResult testPrompt(@RequestBody AiPromptTestDto testDto) {
        ScAiPromptTemplate template = scAiPromptTemplateService
                .selectScAiPromptTemplateByTemplateId(testDto.getTemplateId());
        if (template == null) {
            return error("Prompt模板不存在");
        }
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(testDto.getModelId());
        requestDto.setBizType(template.getBizType());
        requestDto.setSystemPrompt(template.getPromptContent());
        requestDto.setUserPrompt(testDto.getUserPrompt());
        return success(aiGatewayService.chat(requestDto));
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:task:list')")
    @GetMapping("/task/list")
    public TableDataInfo taskList(ScAiTaskLog scAiTaskLog) {
        startPage();
        List<ScAiTaskLog> list = scAiTaskLogService.selectScAiTaskLogList(scAiTaskLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:ai:task:list')")
    @GetMapping("/task/{taskId}")
    public AjaxResult getTaskInfo(@PathVariable Long taskId) {
        return success(scAiTaskLogService.selectScAiTaskLogByTaskId(taskId));
    }
}
