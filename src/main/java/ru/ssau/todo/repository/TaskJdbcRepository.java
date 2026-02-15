package ru.ssau.todo.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("JDBC")
public class TaskJdbcRepository implements TaskRepository{

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TaskJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Task create(Task task){
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        task.setCreatedAt(LocalDateTime.now());
        String sql = "INSERT INTO task (title, status, createdBy, createdAt) VALUES (:title, :status, :createdBy, :createdAt) RETURNING id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title",task.getTitle());
        params.addValue("status",task.getStatus().toString());
        params.addValue("createdBy",task.getCreatedBy());
        params.addValue("createdAt",task.getCreatedAt());
        task.setId(namedParameterJdbcTemplate.queryForObject(sql, params, Long.class));
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        String sql = "SELECT * FROM task WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id",id);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params,new UserRowMapper()));
    }


    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        String sql = "SELECT * FROM task WHERE createdBy = :userId AND createdAt >= :from AND createdAt <= :to";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId",userId);
        params.addValue("from",from);
        params.addValue("to",to);
        return namedParameterJdbcTemplate.query(sql, params,new UserRowMapper());

    }

    @Override
    public void update(Task task) throws TaskNotFoundException {

        String sql = "UPDATE task SET title = :title, status = :status WHERE id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title",task.getTitle());
        params.addValue("status",task.getStatus().toString());
        params.addValue("id",task.getId());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM task WHERE id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id",id);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        String sql = "SELECT COUNT(*) FROM task WHERE (status = :open OR status = :in_progress) AND createdBy = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId",userId);
        params.addValue("open", TaskStatus.OPEN.name());
        params.addValue("in_progress",TaskStatus.IN_PROGRESS.name());
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }
}
