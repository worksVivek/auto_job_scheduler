package com.example.auto_job_runner.service;

import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.exception.ResourceNotFoundException;
import com.example.auto_job_runner.mapper.JobMapper;
import com.example.auto_job_runner.repository.JobExecutionRepository;
import com.example.auto_job_runner.repository.JobRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.auto_job_runner.dto.JobExecutionResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.Counter;

import java.sql.Timestamp;

@Service
public class JobExecutionService {

        private final Counter executionTotalCounter;

        private final JobRepository jobRepository;
        private final JobExecutionRepository jobExecutionRepository;
        private final JobAsyncExecutorService jobAsyncExecutorService;
        private final JobMapper jobMapper;

        private static final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);

        public JobExecutionService(JobRepository jobRepository,
                        JobExecutionRepository jobExecutionRepository,
                        JobAsyncExecutorService jobAsyncExecutorService,
                        JobMapper jobMapper,
                        MeterRegistry meterRegistry) {
                this.jobRepository = jobRepository;
                this.jobExecutionRepository = jobExecutionRepository;
                this.jobAsyncExecutorService = jobAsyncExecutorService;
                this.jobMapper = jobMapper;
                this.executionTotalCounter = meterRegistry
                                .counter("job.executions.total");
        }

        // =====================================================
        // Trigger Job
        // =====================================================
        public Long triggerJob(Long jobId) {

                logger.info("Triggering job with id: {}", jobId);

                Job job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Job not found with id: " + jobId));

                JobExecution execution = new JobExecution();
                execution.setJob(job);
                execution.setStatus(ExecutionStatus.CREATED);

                JobExecution savedExecution = jobExecutionRepository.save(execution);
                executionTotalCounter.increment();

                logger.info("Execution {} created for job {}",
                                savedExecution.getId(),
                                jobId);

                jobAsyncExecutorService.executeJob(savedExecution.getId());

                return savedExecution.getId();
        }

        // =====================================================
        // Get All Executions
        // =====================================================
        public Page<JobExecutionResponse> getAllExecutions(Pageable pageable) {

                return jobExecutionRepository.findAll(pageable)
                                .map(jobMapper::toExecutionResponse);
        }

        // =====================================================
        // Get Executions By Job
        // =====================================================
        public Page<JobExecutionResponse> getExecutionsByJobId(
                        Long jobId,
                        Pageable pageable) {

                if (!jobRepository.existsById(jobId)) {
                        throw new ResourceNotFoundException(
                                        "Job not found with id: " + jobId);
                }

                logger.info("Fetching executions for job {}", jobId);

                return jobExecutionRepository
                                .findByJobId(jobId, pageable)
                                .map(jobMapper::toExecutionResponse);
        }

        // =====================================================
        // Get Execution By Id
        // =====================================================
        public JobExecutionResponse getExecutionById(Long executionId) {

                JobExecution execution = jobExecutionRepository.findById(executionId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Execution not found with id: "
                                                                + executionId));

                return jobMapper.toExecutionResponse(execution);
        }
}
