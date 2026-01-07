package com.taaply.todo.tasks;

import com.taaply.todo.categories.CategoryEntity;
import com.taaply.todo.categories.CategoryRepository;
import com.taaply.todo.common.dto.PagedResponse;
import com.taaply.todo.common.exceptions.BadRequestException;
import com.taaply.todo.common.exceptions.ForbiddenException;
import com.taaply.todo.common.exceptions.ResourceNotFoundException;
import com.taaply.todo.tasks.dto.*;
import com.taaply.todo.users.UserEntity;
import com.taaply.todo.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getAllTasks(TaskFilterRequest filter, UUID userId) {
        Specification<TaskEntity> spec = TaskSpecification.withFilters(
                userId,
                filter.getStatus(),
                filter.getPriority(),
                filter.getCategoryId(),
                filter.getDueDateBefore(),
                filter.getDueDateAfter(),
                filter.getSearch());

        String sortBy = filter.getSortBy();
        if ("priority".equals(sortBy)) {
            sortBy = "priorityOrder";
        }

        Sort sort = Sort.by(
                filter.getSortDir().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<TaskEntity> page = taskRepository.findAll(spec, pageable);

        List<TaskResponse> tasks = page.getContent()
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());

        return PagedResponse.of(
                tasks,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId, UUID userId) {
        TaskEntity task = findTaskAndValidateOwnership(taskId, userId);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, UUID userId) {
        UserEntity user = userService.findById(userId);

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = validateAndGetCategory(request.getCategoryId(), userId);
        }

        TaskEntity task = TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .priority(request.getPriority() != null ? request.getPriority()
                        : com.taaply.todo.common.enums.Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : com.taaply.todo.common.enums.Status.PENDING)
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        TaskEntity savedTask = taskRepository.save(task);
        log.info("Created task: {} for user: {}", savedTask.getId(), userId);

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID userId) {
        TaskEntity task = findTaskAndValidateOwnership(taskId, userId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (Boolean.TRUE.equals(request.getClearCategory())) {
            task.setCategory(null);
        } else if (request.getCategoryId() != null) {
            CategoryEntity category = validateAndGetCategory(request.getCategoryId(), userId);
            task.setCategory(category);
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (Boolean.TRUE.equals(request.getClearDueDate())) {
            task.setDueDate(null);
        } else if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        TaskEntity savedTask = taskRepository.save(task);
        log.info("Updated task: {}", savedTask.getId());

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId, UUID userId) {
        TaskEntity task = findTaskAndValidateOwnership(taskId, userId);
        taskRepository.delete(task);
        log.info("Deleted task: {}", taskId);
    }

    private TaskEntity findTaskAndValidateOwnership(UUID taskId, UUID userId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to access this task");
        }

        return task;
    }

    private CategoryEntity validateAndGetCategory(UUID categoryId, UUID userId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category not found: " + categoryId));

        if (!category.getUser().getId().equals(userId)) {
            throw new BadRequestException("Invalid category: " + categoryId);
        }

        return category;
    }
}
