package com.example.auto_job_runner.dto;

import com.example.auto_job_runner.enums.ExecutionStatus;

import java.sql.Timestamp;

public class JobExecutionResponse {

    private Long id;
    private Long jobId;
    private String jobName;
    private ExecutionStatus status;
    private Timestamp startedAt;
    private Timestamp completedAt;
    private String errorMessage;

    public JobExecutionResponse() {
    }

    public JobExecutionResponse(Long id, Long jobId, String jobName, ExecutionStatus status,
            Timestamp startedAt, Timestamp completedAt, String errorMessage) {
        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
    }

    // ===== Getters =====

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // ===== Setters =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
