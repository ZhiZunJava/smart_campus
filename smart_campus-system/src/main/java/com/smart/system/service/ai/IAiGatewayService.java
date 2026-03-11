package com.smart.system.service.ai;

import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiModelTestDto;

/**
 * AI 网关服务
 *
 * @author Codex
 */
public interface IAiGatewayService
{
    AiChatResponseVo testModel(AiModelTestDto testDto);

    AiChatResponseVo chat(AiChatRequestDto requestDto);
}
