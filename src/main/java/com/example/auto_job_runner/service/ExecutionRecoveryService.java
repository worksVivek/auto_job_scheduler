package com.example.auto_job_runner.service;

import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.repository.JobExecutionRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ExecutionRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionRecoveryService.class);

    private final JobExecutionRepository jobExecutionRepository;

    public ExecutionRecoveryService(JobExecutionRepository jobExecutionRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
    }

    // ===============================
    // Startup Recovery
    // ===============================

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void recoverStaleExecutions() {

        logger.info("Startup execution recovery started");

        List<JobExecution> staleExecutions = jobExecutionRepository
                .findByStatusAndCompletedAtIsNull(
                        ExecutionStatus.RUNNING);

        if (staleExecutions.isEmpty()) {
            logger.info("No stale executions found");
            return;
        }

        logger.warn("Recovering {} stale executions",
                staleExecutions.size());

        for (JobExecution execution : staleExecutions) {

            execution.setStatus(ExecutionStatus.ABORTED);
            execution.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            execution.setErrorMessage("Execution aborted due to system restart");

            logger.warn("Execution {} marked as ABORTED",
                    execution.getId());
        }

        jobExecutionRepository.saveAll(staleExecutions);

        logger.info("Startup recovery completed");
    }

    // ===============================
    // 2️⃣ Scheduled Timeout Detection
    // ===============================

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void detectTimedOutExecution() {

        long timeout = 5 * 60 * 1000;
        Timestamp threshold = new Timestamp(System.currentTimeMillis() - timeout);

        List<JobExecution> timedOutExecutions = jobExecutionRepository
                .findByStatusAndStartedAtBefore(
                        ExecutionStatus.RUNNING,
                        threshold);

        if (timedOutExecutions.isEmpty()) {
            return;
        }

        logger.warn("Detected {} timed-out executions",
                timedOutExecutions.size());

        for (JobExecution execution : timedOutExecutions) {

            execution.setStatus(ExecutionStatus.ABORTED);
            execution.setErrorMessage("Execution timed out");
            execution.setCompletedAt(
                    new Timestamp(System.currentTimeMillis()));

            logger.warn(
                    "Execution {} (job {}) timed out after {} ms",
                    execution.getId(),
                    execution.getJob().getId(),
                    System.currentTimeMillis()
                            - execution.getStartedAt().getTime());
        }

        jobExecutionRepository.saveAll(timedOutExecutions);
    }
}
