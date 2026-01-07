package com.taaply.todo.tasks;

import com.taaply.todo.common.enums.Priority;
import com.taaply.todo.common.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskSpecification {

    public static Specification<TaskEntity> withFilters(
            UUID userId,
            Status status,
            Priority priority,
            UUID categoryId,
            Instant dueDateBefore,
            Instant dueDateAfter,
            String search) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (dueDateBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), dueDateBefore));
            }

            if (dueDateAfter != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), dueDateAfter));
            }

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        searchPattern);
                Predicate descriptionMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        searchPattern);
                predicates.add(criteriaBuilder.or(titleMatch, descriptionMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
