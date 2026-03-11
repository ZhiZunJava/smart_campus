package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScParentStudentRel;

/**
 * 家长学生关系 Mapper
 *
 * @author Codex
 */
public interface ScParentStudentRelMapper
{
    ScParentStudentRel selectScParentStudentRelById(Long id);

    List<ScParentStudentRel> selectScParentStudentRelList(ScParentStudentRel scParentStudentRel);

    int insertScParentStudentRel(ScParentStudentRel scParentStudentRel);

    int updateScParentStudentRel(ScParentStudentRel scParentStudentRel);

    int deleteScParentStudentRelById(Long id);

    int deleteScParentStudentRelByIds(Long[] ids);
}
