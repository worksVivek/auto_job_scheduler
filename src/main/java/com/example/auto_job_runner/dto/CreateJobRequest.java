package com.example.auto_job_runner.dto;

import com.example.auto_job_runner.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateJobRequest {

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotNull
    private JobType jobType;

    @NotBlank(message = "Cron expression should not be blank")
    private String cronExpression;

    // GETTERS
    public String getName() {
        return name;
    }

    public JobType getJobType() {
        return jobType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
