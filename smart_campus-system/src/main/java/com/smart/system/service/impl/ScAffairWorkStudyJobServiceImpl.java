package com.smart.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAffairWorkStudyJob;
import com.smart.system.mapper.ScAffairWorkStudyJobMapper;
import com.smart.system.service.IScAffairWorkStudyJobService;

@Service
public class ScAffairWorkStudyJobServiceImpl implements IScAffairWorkStudyJobService {
    @Autowired
    private ScAffairWorkStudyJobMapper scAffairWorkStudyJobMapper;

    @Override
    public ScAffairWorkStudyJob selectScAffairWorkStudyJobByJobId(Long jobId) {
        return scAffairWorkStudyJobMapper.selectScAffairWorkStudyJobByJobId(jobId);
    }

    @Override
    public List<ScAffairWorkStudyJob> selectScAffairWorkStudyJobList(ScAffairWorkStudyJob query) {
        return scAffairWorkStudyJobMapper.selectScAffairWorkStudyJobList(query);
    }

    @Override
    public List<ScAffairWorkStudyJob> selectOpenWorkStudyJobs() {
        Date now = new Date();
        ScAffairWorkStudyJob query = new ScAffairWorkStudyJob();
        query.setPublishStatus("0");
        return scAffairWorkStudyJobMapper.selectScAffairWorkStudyJobList(query).stream()
                .filter(item -> item.getOpenStartTime() == null || !now.before(item.getOpenStartTime()))
                .filter(item -> item.getOpenEndTime() == null || !now.after(item.getOpenEndTime()))
                .collect(Collectors.toList());
    }

    @Override
    public int insertScAffairWorkStudyJob(ScAffairWorkStudyJob job) {
        if (StringUtils.isBlank(job.getJobCode())) {
            job.setJobCode(buildDefaultJobCode());
        }
        if (StringUtils.isBlank(job.getSalaryUnit())) {
            job.setSalaryUnit("MONTH");
        }
        if (job.getPublishStatus() == null) {
            job.setPublishStatus("0");
        }
        if (job.getAppliedCount() == null) {
            job.setAppliedCount(0);
        }
        return scAffairWorkStudyJobMapper.insertScAffairWorkStudyJob(job);
    }

    @Override
    public int updateScAffairWorkStudyJob(ScAffairWorkStudyJob job) {
        if (StringUtils.isBlank(job.getJobCode())) {
            job.setJobCode(buildDefaultJobCode());
        }
        return scAffairWorkStudyJobMapper.updateScAffairWorkStudyJob(job);
    }

    @Override
    public int deleteScAffairWorkStudyJobByJobIds(Long[] jobIds) {
        return scAffairWorkStudyJobMapper.deleteScAffairWorkStudyJobByJobIds(jobIds);
    }

    private String buildDefaultJobCode() {
        long suffix = System.currentTimeMillis() % 10000000000L;
        return "WS" + suffix;
    }
}
