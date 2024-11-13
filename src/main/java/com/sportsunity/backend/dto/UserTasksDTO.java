package com.sportsunity.backend.dto;

import java.util.List;

public class UserTasksDTO {
    private Long userId;
    private List<TaskDTO> tasks;

    // Constructor, Getters, and Setters
    public UserTasksDTO(Long userId, List<TaskDTO> tasks) {
        this.userId = userId;
        this.tasks = tasks;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
}
