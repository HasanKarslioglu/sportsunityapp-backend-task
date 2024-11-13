package com.sportsunity.backend.dto;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    private Long userId;
    private String username;
    private Long companyId;
    private String role;
    private List<TaskDTO> tasks;

    public UserDTO(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.companyId = user.getCompany().map(company -> company.getId()).orElse(null);
        this.tasks = user.getTasks() != null ?
                user.getTasks().stream()
                        .map(TaskDTO::new)
                        .collect(Collectors.toList())
                : List.of();
        this.role = user.getRole().toString();
    }
    public Long getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public Long getCompanyId() {
        return companyId;
    }
    public String getRole() {
        return role;
    }
    public List<TaskDTO> getTasks() {
        return tasks == null ? List.of() : tasks;
    }

}

