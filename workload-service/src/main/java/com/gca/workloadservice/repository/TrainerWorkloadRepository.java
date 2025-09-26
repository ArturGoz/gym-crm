package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findTrainerWorkloadsByUsername(String trainerUsername);

    void deleteTrainerWorkloadsByUsername(String trainerUsername);
}
