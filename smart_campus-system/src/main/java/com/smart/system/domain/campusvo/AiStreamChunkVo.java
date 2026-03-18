package com.smart.system.domain.campusvo;

/**
 * AI 流式片段 VO
 */
public class AiStreamChunkVo
{
    private String type;
    private String content;

    public AiStreamChunkVo()
    {
    }

    public AiStreamChunkVo(String type, String content)
    {
        this.type = type;
        this.content = content;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
