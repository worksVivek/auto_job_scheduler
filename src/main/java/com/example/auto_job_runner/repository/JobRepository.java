package com.example.auto_job_runner.repository;

import com.example.auto_job_runner.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job,Long> {

}
