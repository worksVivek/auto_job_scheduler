package com.example.auto_job_runner.dto;

import com.example.auto_job_runner.enums.JobType;
import com.example.auto_job_runner.enums.ExecutionStatus;

import java.sql.Timestamp;

public class JobResponse {
    private long id;
    private String name;
    private JobType jobType;
    private String cronExpression;
    private ExecutionStatus status;
    private Timestamp createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public JobResponse(long id, String name, JobType jobType, String cronExpression, ExecutionStatus status, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.jobType = jobType;
        this.cronExpression = cronExpression;
        this.status = status;
        this.createdAt = createdAt;
    }
  public JobResponse(){

  }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
