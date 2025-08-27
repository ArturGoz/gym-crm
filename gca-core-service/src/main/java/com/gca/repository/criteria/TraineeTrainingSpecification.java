package com.gca.repository.criteria;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.model.Training;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class TraineeTrainingSpecification extends TrainingSpecification {

    public static Specification<Training> findByCriteria(TrainingTraineeCriteriaFilter searchFilter) {
        return traineeUsernamePredicate(searchFilter.getTraineeUsername())
                .and(dateRangePredicate(searchFilter.getFromDate(), searchFilter.getToDate()))
                .and(trainerNamePredicate(searchFilter.getTrainerName()))
                .and(trainingTypePredicate(searchFilter.getTrainingTypeName()));
    }

    private static Specification<Training> traineeUsernamePredicate(@Nullable String username) {
        return (root, query, cb) -> {
            if (isNotBlank(username)) {
                return cb.equal(root.get("trainee").get("user").get("username"), username);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Training> trainerNamePredicate(@Nullable String trainerName) {
        return (root, query, cb) -> {
            if (isNotBlank(trainerName)) {
                return cb.equal(root.get("trainer").get("user").get("username"), trainerName);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Training> trainingTypePredicate(@Nullable String trainingType) {
        return (root, query, cb) -> {
            if (isNotBlank(trainingType)) {
                return cb.equal(cb.lower(root.get("type").get("name")), trainingType.toLowerCase());
            }
            return cb.conjunction();
        };
    }
}
