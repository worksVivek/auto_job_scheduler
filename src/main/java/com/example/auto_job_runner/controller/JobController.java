package com.example.auto_job_runner.controller;

import com.example.auto_job_runner.dto.CreateJobRequest;
import com.example.auto_job_runner.dto.CreateJobResponse;
import com.example.auto_job_runner.entity.Job;
import com.example.auto_job_runner.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
          this.jobService=jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<CreateJobResponse> saveJob(@RequestBody CreateJobRequest jobRequest){
        CreateJobResponse createdjob = jobService.createJob(jobRequest);
        System.out.println(createdjob.getId() + " " + createdjob.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdjob);


    }
}
