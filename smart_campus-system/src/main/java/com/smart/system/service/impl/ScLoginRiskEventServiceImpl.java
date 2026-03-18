package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLoginRiskEvent;
import com.smart.system.mapper.ScLoginRiskEventMapper;
import com.smart.system.service.IScLoginRiskEventService;

/**
 * 登录风险事件 Service 实现
 *
 * @author can
 */
@Service
public class ScLoginRiskEventServiceImpl implements IScLoginRiskEventService {
    @Autowired
    private ScLoginRiskEventMapper scLoginRiskEventMapper;

    @Override
    public ScLoginRiskEvent selectScLoginRiskEventById(Long id) {
        return scLoginRiskEventMapper.selectScLoginRiskEventById(id);
    }

    @Override
    public List<ScLoginRiskEvent> selectScLoginRiskEventList(ScLoginRiskEvent scLoginRiskEvent) {
        return scLoginRiskEventMapper.selectScLoginRiskEventList(scLoginRiskEvent);
    }

    @Override
    public int insertScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent) {
        return scLoginRiskEventMapper.insertScLoginRiskEvent(scLoginRiskEvent);
    }

    @Override
    public int updateScLoginRiskEvent(ScLoginRiskEvent scLoginRiskEvent) {
        return scLoginRiskEventMapper.updateScLoginRiskEvent(scLoginRiskEvent);
    }

    @Override
    public int deleteScLoginRiskEventByIds(Long[] ids) {
        return scLoginRiskEventMapper.deleteScLoginRiskEventByIds(ids);
    }

    @Override
    public int deleteScLoginRiskEventById(Long id) {
        return scLoginRiskEventMapper.deleteScLoginRiskEventById(id);
    }
}
