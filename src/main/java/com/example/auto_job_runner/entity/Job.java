package com.example.auto_job_runner.entity;

import com.example.auto_job_runner.enums.JobStatus;
import com.example.auto_job_runner.enums.JobType;
import com.example.auto_job_runner.enums.ExecutionStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    private String cronExpression;

    public JobType getJobType() {
        return jobType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
