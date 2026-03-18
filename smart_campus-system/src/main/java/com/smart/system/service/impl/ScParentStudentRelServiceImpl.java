package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.mapper.ScParentStudentRelMapper;
import com.smart.system.service.IScParentStudentRelService;

/**
 * 家长学生关系 Service 实现
 *
 * @author can
 */
@Service
public class ScParentStudentRelServiceImpl implements IScParentStudentRelService {
    @Autowired
    private ScParentStudentRelMapper scParentStudentRelMapper;

    @Override
    public ScParentStudentRel selectScParentStudentRelById(Long id) {
        return scParentStudentRelMapper.selectScParentStudentRelById(id);
    }

    @Override
    public List<ScParentStudentRel> selectScParentStudentRelList(ScParentStudentRel scParentStudentRel) {
        return scParentStudentRelMapper.selectScParentStudentRelList(scParentStudentRel);
    }

    @Override
    public int insertScParentStudentRel(ScParentStudentRel scParentStudentRel) {
        return scParentStudentRelMapper.insertScParentStudentRel(scParentStudentRel);
    }

    @Override
    public int updateScParentStudentRel(ScParentStudentRel scParentStudentRel) {
        return scParentStudentRelMapper.updateScParentStudentRel(scParentStudentRel);
    }

    @Override
    public int deleteScParentStudentRelByIds(Long[] ids) {
        return scParentStudentRelMapper.deleteScParentStudentRelByIds(ids);
    }

    @Override
    public int deleteScParentStudentRelById(Long id) {
        return scParentStudentRelMapper.deleteScParentStudentRelById(id);
    }
}
