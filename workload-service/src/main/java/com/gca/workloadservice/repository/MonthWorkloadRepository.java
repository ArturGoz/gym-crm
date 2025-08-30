package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.MonthWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthWorkloadRepository extends JpaRepository<MonthWorkload, Long> {
}
