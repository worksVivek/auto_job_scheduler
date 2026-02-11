package com.example.auto_job_runner.service;

import com.example.auto_job_runner.dto.CreateJobRequest;
import com.example.auto_job_runner.dto.CreateJobResponse;
import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.enums.Status;
import com.example.auto_job_runner.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class JobService {
  private JobRepository jobRepository;
  public JobService(JobRepository jobRepository){
    this.jobRepository=jobRepository;
  }
    public CreateJobResponse createJob(CreateJobRequest request){
      Job job = new Job();
      job.setJobType(request.getJobType());
      job.setName(request.getName());
      job.setCronExpression(request.getCronExpression());
      job.setStatus(Status.CREATED);
      job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      Job savedJob = jobRepository.save(job);
      CreateJobResponse response = new CreateJobResponse();
      response.setId(savedJob.getId());
      response.setName(savedJob.getName());
      response.setJobType(savedJob.getJobType());
      response.setCronExpression(savedJob.getCronExpression());
      response.setStatus(savedJob.getStatus());
      response.setCreatedAt(savedJob.getCreatedAt());

      return response;

    }
}
