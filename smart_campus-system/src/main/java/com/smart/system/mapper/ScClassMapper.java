package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScClass;

public interface ScClassMapper {
    ScClass selectScClassByClassId(Long classId);

    List<ScClass> selectScClassList(ScClass scClass);

    int insertScClass(ScClass scClass);

    int updateScClass(ScClass scClass);

    int deleteScClassByClassId(Long classId);

    int deleteScClassByClassIds(Long[] classIds);
}
