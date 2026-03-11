package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScClass;
import com.smart.system.mapper.ScClassMapper;
import com.smart.system.service.IScClassService;

@Service
public class ScClassServiceImpl implements IScClassService
{
    @Autowired
    private ScClassMapper scClassMapper;

    @Override
    public ScClass selectScClassByClassId(Long classId)
    {
        return scClassMapper.selectScClassByClassId(classId);
    }

    @Override
    public List<ScClass> selectScClassList(ScClass scClass)
    {
        return scClassMapper.selectScClassList(scClass);
    }

    @Override
    public int insertScClass(ScClass scClass)
    {
        return scClassMapper.insertScClass(scClass);
    }

    @Override
    public int updateScClass(ScClass scClass)
    {
        return scClassMapper.updateScClass(scClass);
    }

    @Override
    public int deleteScClassByClassIds(Long[] classIds)
    {
        return scClassMapper.deleteScClassByClassIds(classIds);
    }

    @Override
    public int deleteScClassByClassId(Long classId)
    {
        return scClassMapper.deleteScClassByClassId(classId);
    }
}
