package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findTrainerWorkloadsByUsername(String trainerUsername);

    void deleteTrainerWorkloadsByUsername(String trainerUsername);
}
