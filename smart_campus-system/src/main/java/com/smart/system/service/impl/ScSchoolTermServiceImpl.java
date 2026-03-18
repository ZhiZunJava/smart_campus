package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.mapper.ScSchoolTermMapper;
import com.smart.system.service.IScSchoolTermService;

@Service
public class ScSchoolTermServiceImpl implements IScSchoolTermService {
    @Autowired
    private ScSchoolTermMapper scSchoolTermMapper;

    public ScSchoolTerm selectScSchoolTermByTermId(Long termId) {
        return scSchoolTermMapper.selectScSchoolTermByTermId(termId);
    }

    public List<ScSchoolTerm> selectScSchoolTermList(ScSchoolTerm scSchoolTerm) {
        return scSchoolTermMapper.selectScSchoolTermList(scSchoolTerm);
    }

    public int insertScSchoolTerm(ScSchoolTerm scSchoolTerm) {
        return scSchoolTermMapper.insertScSchoolTerm(scSchoolTerm);
    }

    public int updateScSchoolTerm(ScSchoolTerm scSchoolTerm) {
        return scSchoolTermMapper.updateScSchoolTerm(scSchoolTerm);
    }

    public int deleteScSchoolTermByTermIds(Long[] termIds) {
        return scSchoolTermMapper.deleteScSchoolTermByTermIds(termIds);
    }

    public int deleteScSchoolTermByTermId(Long termId) {
        return scSchoolTermMapper.deleteScSchoolTermByTermId(termId);
    }
}
