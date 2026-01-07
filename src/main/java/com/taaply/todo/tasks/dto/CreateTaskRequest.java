package com.taaply.todo.tasks.dto;

import com.taaply.todo.common.enums.Priority;
import com.taaply.todo.common.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    private UUID categoryId;

    private Priority priority = Priority.MEDIUM;

    private Status status = Status.PENDING;

    private Instant dueDate;
}
