package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScClass;

public interface IScClassService
{
    ScClass selectScClassByClassId(Long classId);

    List<ScClass> selectScClassList(ScClass scClass);

    int insertScClass(ScClass scClass);

    int updateScClass(ScClass scClass);

    int deleteScClassByClassIds(Long[] classIds);

    int deleteScClassByClassId(Long classId);
}
