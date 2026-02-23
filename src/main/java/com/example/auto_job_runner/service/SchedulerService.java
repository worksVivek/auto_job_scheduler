package com.example.auto_job_runner.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;

import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.enums.JobStatus;
import com.example.auto_job_runner.repository.JobRepository;

@Service
public class SchedulerService {

    private final TaskScheduler taskScheduler;
    private final JobExecutionService jobExecutionService;
    private final JobRepository jobRepository;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public SchedulerService(TaskScheduler taskScheduler,
            JobExecutionService jobExecutionService,
            JobRepository jobRepository) {
        this.taskScheduler = taskScheduler;
        this.jobExecutionService = jobExecutionService;
        this.jobRepository = jobRepository;
    }

    // ========================================
    // Register New Job
    // ========================================
    public void registerJob(Job job) {
        schedule(job);
    }

    // ========================================
    // Reschedule Existing Job
    // ========================================
    public void rescheduleJob(Job job) {
        cancelJob(job.getId());
        schedule(job);
    }

    // ========================================
    // Cancel Job
    // ========================================
    public void cancelJob(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.get(jobId);

        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(jobId);
        }
    }

    // ========================================
    // Internal Scheduling Logic
    // ========================================
    private void schedule(Job job) {

        if (job.getCronExpression() == null ||
                job.getCronExpression().isBlank()) {
            return;
        }

        Runnable task = () -> {
            try {
                jobExecutionService.triggerJob(job.getId());
            } catch (Exception e) {
                // Avoid crashing scheduler thread
                e.printStackTrace();
            }
        };

        CronTrigger trigger = new CronTrigger(job.getCronExpression());

        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);

        scheduledTasks.put(job.getId(), future);
    }

    // ========================================
    // Load All Jobs on Startup
    // ========================================
    @EventListener(ApplicationReadyEvent.class)
    public void loadJobs() {

        List<Job> jobs = jobRepository.findByJobStatus(JobStatus.ACTIVE);

        for (Job job : jobs) {
            registerJob(job);
        }
    }
}