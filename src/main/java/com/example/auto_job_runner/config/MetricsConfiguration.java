package com.example.auto_job_runner.config;

import com.example.auto_job_runner.enums.ExecutionStatus;
import com.example.auto_job_runner.repository.JobExecutionRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class MetricsConfiguration {

    private final MeterRegistry meterRegistry;
    private final JobExecutionRepository jobExecutionRepository;

    public MetricsConfiguration(MeterRegistry meterRegistry,
                                JobExecutionRepository jobExecutionRepository) {
        this.meterRegistry = meterRegistry;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @PostConstruct
    public void registerGauges() {

        Gauge.builder("job.executions.running",
                        () -> jobExecutionRepository.countByStatus(ExecutionStatus.RUNNING))
                .description("Number of currently running job executions")
                .register(meterRegistry);
    }
}
