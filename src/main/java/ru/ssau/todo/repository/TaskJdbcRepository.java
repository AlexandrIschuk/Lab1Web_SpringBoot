package ru.ssau.todo.repository;

import ru.ssau.todo.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskJdbcRepository implements TaskRepository{
    @Override
    public Task create(Task task) {
        return null;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Task getTask(long id) {
        return null;
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return List.of();
    }

    @Override
    public void update(Task task) throws Exception {

    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return 0;
    }
}
