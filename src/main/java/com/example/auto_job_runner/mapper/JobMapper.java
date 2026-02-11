package com.example.auto_job_runner.mapper;


import com.example.auto_job_runner.dto.CreateJobRequest;
import com.example.auto_job_runner.dto.CreateJobResponse;
import com.example.auto_job_runner.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface JobMapper {
   JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
   Job toEntity(CreateJobRequest request);
    CreateJobResponse toResponse(Job job);


}
