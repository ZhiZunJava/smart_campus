package com.smart.system.domain.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public class UserProfileAdvisorBindDto {
    @NotEmpty(message = "档案列表不能为空")
    private List<Long> profileIds;

    private Long advisorUserId;

    public List<Long> getProfileIds() {
        return profileIds;
    }

    public void setProfileIds(List<Long> profileIds) {
        this.profileIds = profileIds;
    }

    public Long getAdvisorUserId() {
        return advisorUserId;
    }

    public void setAdvisorUserId(Long advisorUserId) {
        this.advisorUserId = advisorUserId;
    }
}
