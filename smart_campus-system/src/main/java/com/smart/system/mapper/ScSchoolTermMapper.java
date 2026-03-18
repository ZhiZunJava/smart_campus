package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScSchoolTerm;

public interface ScSchoolTermMapper {
    ScSchoolTerm selectScSchoolTermByTermId(Long termId);

    List<ScSchoolTerm> selectScSchoolTermList(ScSchoolTerm scSchoolTerm);

    int insertScSchoolTerm(ScSchoolTerm scSchoolTerm);

    int updateScSchoolTerm(ScSchoolTerm scSchoolTerm);

    int deleteScSchoolTermByTermId(Long termId);

    int deleteScSchoolTermByTermIds(Long[] termIds);
}
