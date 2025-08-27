package com.gca.repository.criteria;

import com.gca.model.Training;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TrainingSpecification {

    protected static Specification<Training> dateRangePredicate(@Nullable LocalDate from, @Nullable LocalDate to) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), to));
            }

            return predicate;
        };
    }
}
