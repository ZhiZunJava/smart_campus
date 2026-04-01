package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAffairWorkStudyJob;

public interface ScAffairWorkStudyJobMapper {
    ScAffairWorkStudyJob selectScAffairWorkStudyJobByJobId(Long jobId);

    List<ScAffairWorkStudyJob> selectScAffairWorkStudyJobList(ScAffairWorkStudyJob query);

    int insertScAffairWorkStudyJob(ScAffairWorkStudyJob job);

    int updateScAffairWorkStudyJob(ScAffairWorkStudyJob job);

    int deleteScAffairWorkStudyJobByJobId(Long jobId);

    int deleteScAffairWorkStudyJobByJobIds(Long[] jobIds);
}
