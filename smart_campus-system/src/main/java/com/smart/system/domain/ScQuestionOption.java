package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题目选项对象 sc_question_option
 *
 * @author Codex
 */
public class ScQuestionOption extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long optionId;
    private Long questionId;
    private String optionKey;
    private String optionContent;
    private String isRight;

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getOptionKey() { return optionKey; }
    public void setOptionKey(String optionKey) { this.optionKey = optionKey; }
    public String getOptionContent() { return optionContent; }
    public void setOptionContent(String optionContent) { this.optionContent = optionContent; }
    public String getIsRight() { return isRight; }
    public void setIsRight(String isRight) { this.isRight = isRight; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("optionId", getOptionId())
            .append("questionId", getQuestionId())
            .append("optionKey", getOptionKey())
            .append("optionContent", getOptionContent())
            .append("isRight", getIsRight())
            .toString();
    }
}
