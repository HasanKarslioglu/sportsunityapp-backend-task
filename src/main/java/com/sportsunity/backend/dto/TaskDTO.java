package com.sportsunity.backend.dto;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;

import java.util.List;

public class TaskDTO {
    private Long id;
    private String description;
    private Long userId;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.userId = task.getUser().getId();
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }
}

