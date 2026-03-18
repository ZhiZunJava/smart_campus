package com.smart.system.domain.campusvo;

/**
 * 题目选项展示 VO
 *
 * @author can
 */
public class QuestionOptionVo {
    private Long optionId;
    private String optionKey;
    private String optionContent;

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
    }
}
