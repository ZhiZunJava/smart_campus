package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAffairWorkStudyJob;

public interface IScAffairWorkStudyJobService {
    ScAffairWorkStudyJob selectScAffairWorkStudyJobByJobId(Long jobId);

    List<ScAffairWorkStudyJob> selectScAffairWorkStudyJobList(ScAffairWorkStudyJob query);

    List<ScAffairWorkStudyJob> selectOpenWorkStudyJobs();

    int insertScAffairWorkStudyJob(ScAffairWorkStudyJob job);

    int updateScAffairWorkStudyJob(ScAffairWorkStudyJob job);

    int deleteScAffairWorkStudyJobByJobIds(Long[] jobIds);
}
