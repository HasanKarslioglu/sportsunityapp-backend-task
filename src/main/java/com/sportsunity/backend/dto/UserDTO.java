package com.sportsunity.backend.dto;
import com.sportsunity.backend.model.Company;
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
        this.role = user.getRole().toString();
        this.companyId = user.getCompany().map(Company::getId).orElse(null); // Handle the case where the company may not be assigned to the user (SUPER_USER)

        // Map User's tasks to TaskDTO list (if present)
        this.tasks = user.getTasks() != null ?
                user.getTasks().stream()
                        .map(TaskDTO::new)  // Map each Task to TaskDTO
                        .collect(Collectors.toList())
                : List.of();
    }

    public Long getUserId() {return userId;}
    public String getUsername() {return username;}
    public Long getCompanyId() {return companyId;}
    public String getRole() {return role;}
    public List<TaskDTO> getTasks() {return tasks == null ? List.of() : tasks;} // Return empty list if tasks are null

}

