package com.example.auto_job_runner.controller;

import com.example.auto_job_runner.common.ApiResponse;

import com.example.auto_job_runner.dto.JobRequest;
import com.example.auto_job_runner.dto.JobResponse;
import com.example.auto_job_runner.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<JobResponse>> saveJob(@Valid @RequestBody JobRequest jobRequest) {
        JobResponse createdjob = jobService.createJob(jobRequest);
        ApiResponse<JobResponse> apiResponse = new ApiResponse<JobResponse>(
                true, "Job created successfully", createdjob);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {

        JobResponse job = jobService.getJobById(id);

        ApiResponse<JobResponse> response = new ApiResponse<>(true, "Job fetched successfully", job);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getJob() {
        List<JobResponse> jobList = jobService.getJobs();

        ApiResponse<List<JobResponse>> response = new ApiResponse<>(true, "jobs fetched successfully", jobList);

        return ResponseEntity.ok(response);

    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updatejob(@PathVariable long id,
            @Valid @RequestBody JobRequest request) {
        JobResponse job = jobService.updatejob(id, request);

        ApiResponse<JobResponse> response = new ApiResponse<>(true, "Job updated successfully", job);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteById(@PathVariable long id) {
        jobService.deleteById(id);
        ApiResponse<Object> response = new ApiResponse<>(true, "Job deleted successfully", null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/jobs/{id}/activate")
    public ResponseEntity<ApiResponse<JobResponse>> activateJob(@PathVariable Long id) {
        JobResponse job = jobService.activatejob(id);
        ApiResponse<JobResponse> response = new ApiResponse<>(true, "Job Activated successfully", job);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/jobs/{id}/deactivate")
    public ResponseEntity<ApiResponse<JobResponse>> deactivateJob(@PathVariable Long id) {
        JobResponse job = jobService.deactivateJob(id);
        ApiResponse<JobResponse> response = new ApiResponse<>(true, "Job De-activated successfully", job);
        return ResponseEntity.ok(response);
    }

}
