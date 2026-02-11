package com.example.auto_job_runner.service;

import com.example.auto_job_runner.dto.CreateJobRequest;
import com.example.auto_job_runner.dto.CreateJobResponse;
import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.enums.Status;
import com.example.auto_job_runner.mapper.JobMapper;
import com.example.auto_job_runner.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class JobService {
  private JobRepository jobRepository;


  private final JobMapper jobMapper;


  public JobService(JobRepository jobRepository,JobMapper jobMapper){
    this.jobRepository=jobRepository;
    this.jobMapper=jobMapper;
  }
    public CreateJobResponse createJob(CreateJobRequest request){
      Job job = jobMapper.toEntity(request);
      job.setStatus(Status.CREATED);
      job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      Job savedJob = jobRepository.save(job);


      return JobMapper.INSTANCE.toResponse(savedJob);

    }
}
