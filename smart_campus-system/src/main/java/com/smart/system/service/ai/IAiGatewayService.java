package com.smart.system.service.ai;

import java.util.function.Consumer;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.campusvo.AiStreamChunkVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiModelTestDto;

/**
 * AI 网关服务
 *
 * @author can
 */
public interface IAiGatewayService {
    AiChatResponseVo testModel(AiModelTestDto testDto);

    AiChatResponseVo chat(AiChatRequestDto requestDto);

    AiChatResponseVo streamChat(AiChatRequestDto requestDto, Consumer<AiStreamChunkVo> onChunk);
}
