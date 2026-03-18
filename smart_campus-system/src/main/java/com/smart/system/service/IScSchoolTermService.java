package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScSchoolTerm;

public interface IScSchoolTermService {
    ScSchoolTerm selectScSchoolTermByTermId(Long termId);

    List<ScSchoolTerm> selectScSchoolTermList(ScSchoolTerm scSchoolTerm);

    int insertScSchoolTerm(ScSchoolTerm scSchoolTerm);

    int updateScSchoolTerm(ScSchoolTerm scSchoolTerm);

    int deleteScSchoolTermByTermIds(Long[] termIds);

    int deleteScSchoolTermByTermId(Long termId);
}
