package com.sportsunity.backend.dto;

import java.util.List;

public class UserTasksDTO {
    private Long userId;

    // This field is used to avoid circular references when serializing the user object
    private List<TaskDTO> tasks;

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
