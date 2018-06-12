package com.filip.springbootblog.jpa.repositories;

import com.filip.springbootblog.jpa.models.BatchJob;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchJobRepository extends CrudRepository<BatchJob, Long> {
    List<BatchJob> findByJobName(String jobName, Sort sort);

}
