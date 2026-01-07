package com.taaply.todo.tasks.dto;

import com.taaply.todo.categories.dto.CategoryResponse;
import com.taaply.todo.common.enums.Priority;
import com.taaply.todo.common.enums.Status;
import com.taaply.todo.tasks.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private CategoryResponse category;
    private Priority priority;
    private Status status;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;

    public static TaskResponse fromEntity(TaskEntity entity) {
        TaskResponseBuilder builder = TaskResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .dueDate(entity.getDueDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getCategory() != null) {
            builder.category(CategoryResponse.fromEntity(entity.getCategory()));
        }

        return builder.build();
    }
}
