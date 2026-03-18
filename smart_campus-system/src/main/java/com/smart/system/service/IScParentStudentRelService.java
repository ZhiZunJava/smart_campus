package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScParentStudentRel;

/**
 * 家长学生关系 Service 接口
 *
 * @author can
 */
public interface IScParentStudentRelService {
    ScParentStudentRel selectScParentStudentRelById(Long id);

    List<ScParentStudentRel> selectScParentStudentRelList(ScParentStudentRel scParentStudentRel);

    int insertScParentStudentRel(ScParentStudentRel scParentStudentRel);

    int updateScParentStudentRel(ScParentStudentRel scParentStudentRel);

    int deleteScParentStudentRelByIds(Long[] ids);

    int deleteScParentStudentRelById(Long id);
}
