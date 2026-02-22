package com.example.auto_job_runner.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auto_job_runner.entity.JobExecution;
import com.example.auto_job_runner.enums.ExecutionStatus;
import java.util.List;
import java.sql.Timestamp;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    Page<JobExecution> findByJobId(Long jobId, Pageable pageable);

    List<JobExecution> findByStatusAndStartedAtBefore(ExecutionStatus status, Timestamp timestamp);

    List<JobExecution> findByStatusAndCompletedAtIsNull(
            ExecutionStatus status);
    long countByStatus(ExecutionStatus status);

}
