package com.smart.common.core.domain.model;

/**
 * 扫码登录会话创建对象
 */
public class ScanLoginCreateBody {
    /**
     * 当前前端站点地址
     */
    private String clientBaseUrl;

    public String getClientBaseUrl() {
        return clientBaseUrl;
    }

    public void setClientBaseUrl(String clientBaseUrl) {
        this.clientBaseUrl = clientBaseUrl;
    }
}
