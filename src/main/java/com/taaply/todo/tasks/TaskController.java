package com.taaply.todo.tasks;

import com.taaply.todo.auth.UserPrincipal;
import com.taaply.todo.common.dto.ApiResponse;
import com.taaply.todo.common.dto.PagedResponse;
import com.taaply.todo.common.enums.Priority;
import com.taaply.todo.common.enums.Status;
import com.taaply.todo.tasks.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Instant dueDateBefore,
            @RequestParam(required = false) Instant dueDateAfter,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(status)
                .priority(priority)
                .categoryId(categoryId)
                .dueDateBefore(dueDateBefore)
                .dueDateAfter(dueDateAfter)
                .search(search)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .page(page)
                .size(size)
                .build();

        PagedResponse<TaskResponse> tasks = taskService.getAllTasks(filter, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        TaskResponse task = taskService.getTaskById(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        TaskResponse task = taskService.createTask(request, principal.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        TaskResponse task = taskService.updateTask(id, request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        taskService.deleteTask(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
    }
}
