package com.smart.common.core.domain.model;

/**
 * 扫码登录确认对象
 */
public class ScanLoginConfirmBody extends LoginBody {
    /**
     * 扫码会话票据
     */
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
