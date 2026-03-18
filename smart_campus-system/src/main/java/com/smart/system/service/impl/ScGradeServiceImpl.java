package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScGrade;
import com.smart.system.mapper.ScGradeMapper;
import com.smart.system.service.IScGradeService;

@Service
public class ScGradeServiceImpl implements IScGradeService {
    @Autowired
    private ScGradeMapper scGradeMapper;

    @Override
    public ScGrade selectScGradeByGradeId(Long gradeId) {
        return scGradeMapper.selectScGradeByGradeId(gradeId);
    }

    @Override
    public List<ScGrade> selectScGradeList(ScGrade scGrade) {
        return scGradeMapper.selectScGradeList(scGrade);
    }

    @Override
    public int insertScGrade(ScGrade scGrade) {
        return scGradeMapper.insertScGrade(scGrade);
    }

    @Override
    public int updateScGrade(ScGrade scGrade) {
        return scGradeMapper.updateScGrade(scGrade);
    }

    @Override
    public int deleteScGradeByGradeIds(Long[] gradeIds) {
        return scGradeMapper.deleteScGradeByGradeIds(gradeIds);
    }

    @Override
    public int deleteScGradeByGradeId(Long gradeId) {
        return scGradeMapper.deleteScGradeByGradeId(gradeId);
    }
}
