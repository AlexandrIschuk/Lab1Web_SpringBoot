package ru.ssau.todo.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ssau.todo.Service.TaskService;
import ru.ssau.todo.entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.repository.TaskRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Task> createTask(@RequestBody Task task) throws Exception {
        Task task1 = taskService.create(task);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task1.getId())
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        //return taskRepository.create(task);
        return new ResponseEntity<>(task1, headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {

        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Task> getTasks(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to, @RequestParam Long userId) {
        return taskService.findAll(from, to, userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<Task>> updateTask(@PathVariable long id, @RequestBody Task task) {
        try {
            task.setId(id);
            taskService.update(task);
            return ResponseEntity.ok(taskService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id) {
        taskService.deleteById(id);
    }

    @GetMapping("/active/count")
    public Map<String, Long> countActiveTask(@RequestParam long userId) {
        long count = taskService.countActiveTasksByUserId(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("activeTasksCount", count);
        return response;
    }
}
