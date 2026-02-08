package ru.ssau.todo.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Task {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;

    public Long getId() {return id; };
    public void setId(Long id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public TaskStatus getStatus() {return status;}
    public void setStatus(TaskStatus status) {this.status = status;}

    public Long getCreatedBy() {return createdBy;}
    public void setCreatedBy(Long createdBy) {this.createdBy = createdBy;}

    public LocalDateTime getCreatedAt() {return createdAt; }
    public void setCreatedAt(LocalDateTime createAt) {this.createdAt = createAt;}
}

