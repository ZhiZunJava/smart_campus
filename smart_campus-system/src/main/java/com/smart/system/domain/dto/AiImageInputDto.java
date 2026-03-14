package com.smart.system.domain.dto;

/**
 * AI 多模态图片输入 DTO
 */
public class AiImageInputDto
{
    private String name;
    private String dataUrl;
    private String mimeType;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDataUrl()
    {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl)
    {
        this.dataUrl = dataUrl;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
}
