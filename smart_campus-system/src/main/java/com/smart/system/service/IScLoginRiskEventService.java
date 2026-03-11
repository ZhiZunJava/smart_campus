package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLoginRiskEvent;

/**
 * 登录风险事件 Service 接口
 *
 * @author Codex
 */
public interface IScLoginRiskEventService
{
    ScLoginRiskEvent selectScLoginRiskEventById(Long id);

    List<ScLoginRiskEvent> selectScLoginRiskEventList(ScLoginRiskEvent scLoginRiskEvent);

    int insertScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent);

    int updateScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent);

    int deleteScLoginRiskEventByIds(Long[] ids);

    int deleteScLoginRiskEventById(Long id);
}
