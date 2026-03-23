package com.smart.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;
import com.smart.common.xss.Xss;

/**
 * 通知公告表 sys_notice
 * 
 * @author ruoyi
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    /** 业务分类（TODO/MESSAGE/NOTICE） */
    private String bizCategory;

    /** 摘要 */
    private String noticeSummary;

    /** 接收范围（ALL/STUDENT/TEACHER/PARENT/CUSTOM） */
    private String receiverScope;

    /** 指定接收用户ID，英文逗号分隔 */
    private String receiverUserIds;

    /** 动作类型 */
    private String actionType;

    /** 动作路径 */
    private String actionPath;

    /** 动作目标ID */
    private Long actionTargetId;

    /** 是否置顶 */
    private String pinned;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 是否已读（查询字段） */
    private String readFlag;

    /** 已读时间（查询字段） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public String getBizCategory()
    {
        return bizCategory;
    }

    public void setBizCategory(String bizCategory)
    {
        this.bizCategory = bizCategory;
    }

    public String getNoticeSummary()
    {
        return noticeSummary;
    }

    public void setNoticeSummary(String noticeSummary)
    {
        this.noticeSummary = noticeSummary;
    }

    public String getReceiverScope()
    {
        return receiverScope;
    }

    public void setReceiverScope(String receiverScope)
    {
        this.receiverScope = receiverScope;
    }

    public String getReceiverUserIds()
    {
        return receiverUserIds;
    }

    public void setReceiverUserIds(String receiverUserIds)
    {
        this.receiverUserIds = receiverUserIds;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getActionPath()
    {
        return actionPath;
    }

    public void setActionPath(String actionPath)
    {
        this.actionPath = actionPath;
    }

    public Long getActionTargetId()
    {
        return actionTargetId;
    }

    public void setActionTargetId(Long actionTargetId)
    {
        this.actionTargetId = actionTargetId;
    }

    public String getPinned()
    {
        return pinned;
    }

    public void setPinned(String pinned)
    {
        this.pinned = pinned;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }

    public String getReadFlag()
    {
        return readFlag;
    }

    public void setReadFlag(String readFlag)
    {
        this.readFlag = readFlag;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("noticeTitle", getNoticeTitle())
            .append("noticeType", getNoticeType())
            .append("noticeContent", getNoticeContent())
            .append("status", getStatus())
            .append("bizCategory", getBizCategory())
            .append("noticeSummary", getNoticeSummary())
            .append("receiverScope", getReceiverScope())
            .append("receiverUserIds", getReceiverUserIds())
            .append("actionType", getActionType())
            .append("actionPath", getActionPath())
            .append("actionTargetId", getActionTargetId())
            .append("pinned", getPinned())
            .append("publishTime", getPublishTime())
            .append("expireTime", getExpireTime())
            .append("readFlag", getReadFlag())
            .append("readTime", getReadTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
