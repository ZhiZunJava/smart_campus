package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLoginRiskEvent;

/**
 * 登录风险事件 Mapper
 *
 * @author Codex
 */
public interface ScLoginRiskEventMapper
{
    ScLoginRiskEvent selectScLoginRiskEventById(Long id);

    List<ScLoginRiskEvent> selectScLoginRiskEventList(ScLoginRiskEvent scLoginRiskEvent);

    int insertScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent);

    int updateScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent);

    int deleteScLoginRiskEventById(Long id);

    int deleteScLoginRiskEventByIds(Long[] ids);
}
