package com.gca.repository;

import com.gca.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM trainees t WHERE t.user_id =" +
            " (SELECT u.id FROM users u WHERE u.username = :username)", nativeQuery = true)
    void deleteByUsername(@Param("username") String username);
}
