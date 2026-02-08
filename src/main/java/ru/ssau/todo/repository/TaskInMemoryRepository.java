package ru.ssau.todo.repository;

import org.springframework.context.annotation.Profile;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("inMemory")
public class TaskInMemoryRepository implements TaskRepository {

    private final Map<Long, Task> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    @Override
    public Task create(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        long newId = idCounter.getAndIncrement();
        task.setId(newId);
        task.setCreatedAt(LocalDateTime.now());
        storage.put(newId, task);
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Task getTask(long id) {
        return storage.get(id);
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return storage.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> {
                    LocalDateTime createdAt = task.getCreatedAt();
                    return (createdAt.isEqual(from) || createdAt.isAfter(from)) &&
                            (createdAt.isEqual(to) || createdAt.isBefore(to));
                })
                .collect(Collectors.toList());
    }

    @Override
    public void update(Task task) throws Exception {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        Long taskId = task.getId();
        Task task1 = getTask(taskId);
        if (taskId == null || !storage.containsKey(taskId)) {
            throw new Exception("Task with id " + taskId + " not found");
        }
        task1.setStatus(task.getStatus());
        task1.setTitle(task.getTitle());
        storage.put(taskId, task1);
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return storage.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> {
                    TaskStatus status = task.getStatus();
                    return status == TaskStatus.OPEN || status == TaskStatus.IN_PROGRESS;
                })
                .count();
    }
}
