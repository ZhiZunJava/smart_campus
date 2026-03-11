package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 登录风险事件对象 sc_login_risk_event
 *
 * @author Codex
 */
public class ScLoginRiskEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String loginIp;

    private String loginLocation;

    private String deviceFingerprint;

    private String deviceType;

    private String browser;

    private String os;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    private String riskType;

    private BigDecimal riskScore;

    private String riskLevel;

    private String disposeAction;

    private String disposeStatus;

    private String evidenceJson;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getLoginIp()
    {
        return loginIp;
    }

    public void setLoginIp(String loginIp)
    {
        this.loginIp = loginIp;
    }

    public String getLoginLocation()
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation)
    {
        this.loginLocation = loginLocation;
    }

    public String getDeviceFingerprint()
    {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint)
    {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public String getBrowser()
    {
        return browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    public String getRiskType()
    {
        return riskType;
    }

    public void setRiskType(String riskType)
    {
        this.riskType = riskType;
    }

    public BigDecimal getRiskScore()
    {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore)
    {
        this.riskScore = riskScore;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getDisposeAction()
    {
        return disposeAction;
    }

    public void setDisposeAction(String disposeAction)
    {
        this.disposeAction = disposeAction;
    }

    public String getDisposeStatus()
    {
        return disposeStatus;
    }

    public void setDisposeStatus(String disposeStatus)
    {
        this.disposeStatus = disposeStatus;
    }

    public String getEvidenceJson()
    {
        return evidenceJson;
    }

    public void setEvidenceJson(String evidenceJson)
    {
        this.evidenceJson = evidenceJson;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("loginIp", getLoginIp())
            .append("loginLocation", getLoginLocation())
            .append("deviceFingerprint", getDeviceFingerprint())
            .append("deviceType", getDeviceType())
            .append("browser", getBrowser())
            .append("os", getOs())
            .append("loginTime", getLoginTime())
            .append("riskType", getRiskType())
            .append("riskScore", getRiskScore())
            .append("riskLevel", getRiskLevel())
            .append("disposeAction", getDisposeAction())
            .append("disposeStatus", getDisposeStatus())
            .append("evidenceJson", getEvidenceJson())
            .append("remark", getRemark())
            .toString();
    }
}
