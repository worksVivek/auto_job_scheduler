package com.example.auto_job_runner.service;

import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.exception.ResourceNotFoundException;
import com.example.auto_job_runner.repository.JobExecutionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class JobAsyncExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(JobAsyncExecutorService.class);

    private final JobExecutionRepository jobExecutionRepository;

    public JobAsyncExecutorService(JobExecutionRepository jobExecutionRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @Async
    @Transactional
    public void executeJob(Long executionId) {

        MDC.put("executionId", String.valueOf(executionId));
        long startTime = System.currentTimeMillis();

        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Execution not found with id: " + executionId));

        try {
            Long jobId = execution.getJob().getId();
            MDC.put("jobId", String.valueOf(jobId));

            logger.info("Execution started");

            execution.setStatus(ExecutionStatus.RUNNING);
            execution.setStartedAt(new Timestamp(startTime));

            // 🔹 Simulate job logic
            Thread.sleep(3000);

            execution.setStatus(ExecutionStatus.SUCCESS);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage("Execution interrupted");

        } catch (Exception e) {

            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());

        } finally {

            long duration = System.currentTimeMillis() - startTime;

            execution.setCompletedAt(new Timestamp(System.currentTimeMillis()));

            jobExecutionRepository.save(execution);

            if (execution.getStatus() == ExecutionStatus.SUCCESS) {
                logger.info("Execution completed successfully in {} ms", duration);
            } else {
                logger.error("Execution failed after {} ms - {}", duration, execution.getErrorMessage());
            }

            MDC.clear();
        }
    }

}
