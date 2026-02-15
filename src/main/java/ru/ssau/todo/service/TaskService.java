package ru.ssau.todo.service;

import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.repository.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private int activeTasks = 10;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        validateCountOfActiveTasks(task);
        return taskRepository.create(task);
    }

    private void validateCountOfActiveTasks(Task task) {
        long l = countActiveTasksByUserId(task.getCreatedBy());
        if (l >= activeTasks) {
            throw new IllegalStateException(String.format("User with id %d cannot have more than %d active tasks. Current count: %d", task.getCreatedBy(), activeTasks,l));
        }
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from, to, userId);
    }

    public void update(Task task) throws TaskNotFoundException {
        Task task1 = taskRepository.findById(task.getId()).orElseThrow();
        if ((task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CLOSED) || ((task1.getStatus() == TaskStatus.IN_PROGRESS && task.getStatus() == TaskStatus.OPEN) || (task1.getStatus() == TaskStatus.OPEN && task.getStatus() == TaskStatus.IN_PROGRESS))) {
            taskRepository.update(task);
        } else {
            validateCountOfActiveTasks(task1);
            taskRepository.update(task);
        }
    }

    public void deleteById(long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        LocalDateTime time = task.getCreatedAt();
        if (time.isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalStateException("The task was created less than 5 minutes ago.");
        }
        taskRepository.deleteById(id);

    }

    public long countActiveTasksByUserId(long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }
}
