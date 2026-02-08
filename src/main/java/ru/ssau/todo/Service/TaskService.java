package ru.ssau.todo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task){
        if(countActiveTasksByUserId(task.getCreatedBy()) >= 10){
            throw new IllegalStateException(
                    String.format(
                            "User with id %d cannot have more than 10 active tasks. Current count: %d",
                            task.getCreatedBy(),
                            countActiveTasksByUserId(task.getCreatedBy())
                    )
            );
        }
       return taskRepository.create(task);
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return taskRepository.findAll(from,to,userId);

    }

    public void update(Task task) throws Exception {
        taskRepository.update(task);
    }

    public void deleteById(long id) {
        Task task = taskRepository.getTask(id);
        LocalDateTime time = task.getCreatedAt();
        if(time.isAfter(LocalDateTime.now().minusMinutes(5))){
            throw new IllegalStateException("The task was created less than 5 minutes ago.");
        }
        taskRepository.deleteById(id);

    }
    public long countActiveTasksByUserId(long userId) {
         return taskRepository.countActiveTasksByUserId(userId);
    }
}
