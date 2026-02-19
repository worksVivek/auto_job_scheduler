package com.example.auto_job_runner.service;

import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.exception.ResourceNotFoundException;
import com.example.auto_job_runner.mapper.JobMapper;
import com.example.auto_job_runner.repository.JobExecutionRepository;
import com.example.auto_job_runner.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.auto_job_runner.dto.JobExecutionResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;

@Service
public class JobExecutionService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobAsyncExecutorService jobAsyncExecutorService;
    private final JobMapper jobMapper;

    private static final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);

    public JobExecutionService(JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository,
            JobAsyncExecutorService jobAsyncExecutorService,
            JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobAsyncExecutorService = jobAsyncExecutorService;
        this.jobMapper = jobMapper;
    }

    public Long triggerJob(Long jobId) {

        // 1️⃣ Fetch job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // 2️⃣ Create execution record
        JobExecution execution = new JobExecution();
        execution.setJob(job);
        execution.setStatus(ExecutionStatus.CREATED);
        // 3️⃣ Save execution
        JobExecution savedExecution = jobExecutionRepository.save(execution);

        // 4️⃣ Trigger async execution
        jobAsyncExecutorService.executeJob(savedExecution.getId());

        // 5️⃣ Return execution ID
        return savedExecution.getId();
    }

    public Page<JobExecutionResponse> getAllExecutions(Pageable pageable) {
        Page<JobExecution> jobExecutions = jobExecutionRepository.findAll(pageable);
        return jobExecutions.map(jobMapper::toExecutionResponse);
    }

    public Page<JobExecutionResponse> getExecutionsByJobId(Long jobId, Pageable pageable) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        Page<JobExecution> jobExecutions = jobExecutionRepository.findByJobId(jobId, pageable);
        return jobExecutions.map(jobMapper::toExecutionResponse);

    }

}
