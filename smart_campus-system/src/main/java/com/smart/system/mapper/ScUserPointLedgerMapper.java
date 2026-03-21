package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScUserPointLedger;

public interface ScUserPointLedgerMapper {
    List<ScUserPointLedger> selectScUserPointLedgerList(ScUserPointLedger scUserPointLedger);

    int insertScUserPointLedger(ScUserPointLedger scUserPointLedger);
}

