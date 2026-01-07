package com.taaply.todo.tasks.dto;

import com.taaply.todo.common.enums.Priority;
import com.taaply.todo.common.enums.Status;
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
public class TaskFilterRequest {

    private Status status;
    private Priority priority;
    private UUID categoryId;
    private Instant dueDateBefore;
    private Instant dueDateAfter;
    private String search;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "DESC";

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    public String getSortBy() {
        return switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "duedate" -> "dueDate";
            case "priority" -> "priority";
            case "title" -> "title";
            default -> "createdAt";
        };
    }

    public String getSortDir() {
        if (sortDir != null && sortDir.equalsIgnoreCase("ASC")) {
            return "ASC";
        }
        return "DESC";
    }

    public int getPage() {
        return Math.max(0, page);
    }

    public int getSize() {
        if (size < 1)
            return 10;
        if (size > 100)
            return 100;
        return size;
    }
}
