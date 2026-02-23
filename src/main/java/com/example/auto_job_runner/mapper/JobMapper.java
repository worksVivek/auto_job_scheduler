package com.example.auto_job_runner.mapper;

import com.example.auto_job_runner.dto.JobExecutionResponse;
import com.example.auto_job_runner.dto.JobRequest;
import com.example.auto_job_runner.dto.JobResponse;
import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.entity.JobExecution;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Job toEntity(JobRequest request);

    JobResponse toResponse(Job job);

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "job.name", target = "jobName")
    JobExecutionResponse toExecutionResponse(JobExecution execution);

}
