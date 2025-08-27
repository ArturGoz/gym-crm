package com.gca.repository.criteria;

import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.model.Training;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class TrainerTrainingSpecification extends TrainingSpecification {

    public static Specification<Training> findByCriteria(TrainingTrainerCriteriaFilter searchFilter) {
        return trainerUsernamePredicate(searchFilter.getTrainerUsername())
                .and(dateRangePredicate(searchFilter.getFromDate(), searchFilter.getToDate()))
                .and(traineeNamePredicate(searchFilter.getTraineeName()));
    }

    private static Specification<Training> trainerUsernamePredicate(@Nullable String username) {
        return (root, query, cb) -> {
            if (isNotBlank(username)) {
                return cb.equal(root.get("trainer").get("user").get("username"), username);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Training> traineeNamePredicate(@Nullable String traineeName) {
        return (root, query, cb) -> {
            if (isNotBlank(traineeName)) {
                return cb.equal(root.get("trainee").get("user").get("username"), traineeName);
            }
            return cb.conjunction();
        };
    }
}
