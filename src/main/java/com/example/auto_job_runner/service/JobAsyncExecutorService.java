package com.example.auto_job_runner.service;

import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.exception.ResourceNotFoundException;
import com.example.auto_job_runner.repository.JobExecutionRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
    private final Counter successCounter;
    private final Counter failedCounter;
    private final Timer executionTimer;

    public JobAsyncExecutorService(JobExecutionRepository jobExecutionRepository, MeterRegistry meterRegistry) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.successCounter = meterRegistry.counter("job.executions.success");
        this.failedCounter = meterRegistry.counter("job.executions.failed");
        this.executionTimer = meterRegistry.timer("job.executions.duration");
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
            successCounter.increment();
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            execution.setStatus(ExecutionStatus.FAILED);
            failedCounter.increment();
            execution.setErrorMessage("Execution interrupted");

        } catch (Exception e) {

            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            failedCounter.increment();

        } finally {

            long duration = System.currentTimeMillis() - startTime;
            executionTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
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
