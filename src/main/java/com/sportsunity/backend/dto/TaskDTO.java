package com.sportsunity.backend.dto;

import com.sportsunity.backend.model.Task;

public class TaskDTO {
    private Long id;
    private String description;
    private Long userId;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        // Handle possible null user (user might not be assigned to the task)
        this.userId = task.getUser() != null ? task.getUser().getId() : null;
    }

    public Long getId() {return id;}
    public String getDescription() {return description;}
    public Long getUserId() {return userId;}
}
