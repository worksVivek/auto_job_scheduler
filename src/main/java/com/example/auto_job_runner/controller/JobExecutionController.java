package com.example.auto_job_runner.controller;

import com.example.auto_job_runner.common.ApiResponse;
import com.example.auto_job_runner.dto.JobExecutionResponse;
import com.example.auto_job_runner.service.JobExecutionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JobExecutionController {

    private final JobExecutionService jobExecutionService;

    public JobExecutionController(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    // =====================================================
    // 1️⃣ Trigger Job Execution
    // POST /api/jobs/{jobId}/trigger
    // =====================================================
    @PostMapping("/jobs/{jobId}/trigger")
    public ResponseEntity<ApiResponse<Long>> triggerJob(@PathVariable Long jobId) {

        Long executionId = jobExecutionService.triggerJob(jobId);
        if (executionId == null) {
            ApiResponse<Long> response = new ApiResponse<>(false,
                    "Job is already running",
                    null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        ApiResponse<Long> response = new ApiResponse<>(true,
                "Job triggered successfully",
                executionId);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // 2️⃣ Get Execution By ID
    // GET /api/executions/{executionId}
    // =====================================================
    @GetMapping("/executions/{executionId}")
    public ResponseEntity<ApiResponse<JobExecutionResponse>> getExecutionById(
            @PathVariable Long executionId) {

        JobExecutionResponse execution = jobExecutionService.getExecutionById(executionId);

        ApiResponse<JobExecutionResponse> response = new ApiResponse<>(true,
                "Execution fetched successfully",
                execution);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // 3️⃣ Get Executions By Job (Paginated)
    // GET /api/jobs/{jobId}/executions?page=0&size=5
    // =====================================================
    @GetMapping("/jobs/{jobId}/executions")
    public ResponseEntity<ApiResponse<Page<JobExecutionResponse>>> getExecutionsByJobId(
            @PathVariable Long jobId,
            Pageable pageable) {

        Page<JobExecutionResponse> executions = jobExecutionService.getExecutionsByJobId(jobId, pageable);

        ApiResponse<Page<JobExecutionResponse>> response = new ApiResponse<>(true,
                "Job executions fetched successfully",
                executions);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // 4️⃣ Get All Executions (Optional Global View)
    // GET /api/executions?page=0&size=10
    // =====================================================
    @GetMapping("/executions")
    public ResponseEntity<ApiResponse<Page<JobExecutionResponse>>> getAllExecutions(
            Pageable pageable) {

        Page<JobExecutionResponse> executions = jobExecutionService.getAllExecutions(pageable);

        ApiResponse<Page<JobExecutionResponse>> response = new ApiResponse<>(true,
                "All executions fetched successfully",
                executions);

        return ResponseEntity.ok(response);
    }
}
