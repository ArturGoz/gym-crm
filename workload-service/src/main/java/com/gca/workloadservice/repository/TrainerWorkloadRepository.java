package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
}
