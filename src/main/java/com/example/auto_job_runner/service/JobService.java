package com.example.auto_job_runner.service;

import org.springframework.scheduling.support.CronExpression;

import com.example.auto_job_runner.dto.JobRequest;
import com.example.auto_job_runner.dto.JobResponse;
import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.enums.JobStatus;
import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.exception.ResourceNotFoundException;
import com.example.auto_job_runner.mapper.JobMapper;
import com.example.auto_job_runner.repository.JobRepository;
import org.springframework.stereotype.Service;
import com.example.auto_job_runner.service.SchedulerService;
import java.sql.Timestamp;
import java.util.List;

@Service
public class JobService {
  private JobRepository jobRepository;
  private SchedulerService schedulerService;
  private final JobMapper jobMapper;

  public JobService(JobRepository jobRepository, SchedulerService schedulerService, JobMapper jobMapper) {
    this.jobRepository = jobRepository;
    this.schedulerService = schedulerService;
    this.jobMapper = jobMapper;
  }

  public JobResponse createJob(JobRequest request) {
    Job job = jobMapper.toEntity(request);
    validateCron(request.getCronExpression());
    job.setJobStatus(JobStatus.INACTIVE);
    job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    Job savedJob = jobRepository.save(job);
    return jobMapper.toResponse(savedJob);
  }

  public List<JobResponse> getJobs() {
    List<Job> jobs = jobRepository.findAll();
    return jobs.stream()
        .map(jobMapper::toResponse)
        .toList();
  }

  public JobResponse getJobById(long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

    return jobMapper.toResponse(job);
  }

  public JobResponse updatejob(long id, JobRequest request) {
    Job existingJob = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    if (existingJob.getJobStatus() == JobStatus.ACTIVE) {
      throw new IllegalStateException("DeActivate the job before updating");
    }
    String oldCron = existingJob.getCronExpression();
    String newCron = request.getCronExpression();
    validateCron(request.getCronExpression());
    existingJob.setName(request.getName());
    existingJob.setCronExpression(request.getCronExpression());
    existingJob.setJobType(request.getJobType());

    Job savedJob = jobRepository.save(existingJob);
    if (!java.util.Objects.equals(oldCron, newCron) && existingJob.getJobStatus() == JobStatus.ACTIVE) {
      schedulerService.rescheduleJob(savedJob);
    }
    return jobMapper.toResponse(savedJob);
  }

  public void deleteById(long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    schedulerService.cancelJob(id);
    jobRepository.delete(job);
  }

  public JobResponse activatejob(long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

    if (job.getJobStatus() == JobStatus.ACTIVE) {
      throw new IllegalStateException("Job is already active");
    }
    job.setJobStatus(JobStatus.ACTIVE);
    Job savedJob = jobRepository.save(job);
    schedulerService.registerJob(savedJob);
    return jobMapper.toResponse(savedJob);
  }

  public JobResponse deactivateJob(long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    if (job.getJobStatus() == JobStatus.INACTIVE) {
      throw new IllegalStateException("Job is already inactive");
    }
    job.setJobStatus(JobStatus.INACTIVE);
    schedulerService.cancelJob(id);
    return jobMapper.toResponse(jobRepository.save(job));
  }

  private void validateCron(String cron) {
    try {
      CronExpression.parse(cron);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid cron expression: " + cron);
    }
  }
}
