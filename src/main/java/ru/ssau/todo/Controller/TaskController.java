package ru.ssau.todo.Controller;

import ru.ssau.todo.entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.repository.TaskInMemoryRepository;
import ru.ssau.todo.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task){
        return taskRepository.create(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id){
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Task> getTasks(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to, @RequestParam Long userId){
        return taskRepository.findAll(from, to, userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable long id, @RequestBody Task task) {
        try{
            task.setId(id);
            taskRepository.update(task);
            return ResponseEntity.ok(task);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id){
        taskRepository.deleteById(id);
    }

    @GetMapping("/active/count")
    public Map<String,Long> countActiveTask(@RequestParam long userId){
        long count = taskRepository.countActiveTasksByUserId(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("activeTasksCount", count);
        return response;
    }
}
