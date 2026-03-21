package com.smart.system.mapper;

import com.smart.system.domain.ScUserPointAccount;

public interface ScUserPointAccountMapper {
    ScUserPointAccount selectScUserPointAccountByUserId(Long userId);

    int insertScUserPointAccount(ScUserPointAccount scUserPointAccount);

    int updateScUserPointAccount(ScUserPointAccount scUserPointAccount);
}

