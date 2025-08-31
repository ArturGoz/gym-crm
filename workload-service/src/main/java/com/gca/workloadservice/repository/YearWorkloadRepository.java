package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.YearWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearWorkloadRepository extends JpaRepository<YearWorkload, Long> {
}
