package com.sportsunity.backend.service;

import com.sportsunity.backend.dto.TaskDTO;
import com.sportsunity.backend.dto.UserTasksDTO;
import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private CompanyService companyService;

    @Autowired
    @Lazy
    private TaskService taskService;

    // Constructor-based injection for other dependencies
    @Autowired
    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }
    // Retrieve a user by ID
    public User getUserById(Long id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    // Delete a user by ID
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // Get tasks for a user based on their role
    public List<UserTasksDTO> getUserTasks(Long userId) {
        User user;
        try {
            user = getUserById(userId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        UserRole role = user.getRole();
        List<Long> userIds;
        switch (role) {
            case SUPER_USER:
                userIds = getAllUserIds();  // Super users can view tasks of all users
                break;
            case COMPANY_ADMIN:
                userIds = getUserIdsByCompany(user.getCompany().orElseThrow());  // Company admins can view users in their company
                break;
            case STANDARD:
                userIds = List.of(userId);  // Standard users can only view their own tasks
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);  // In case an unexpected role is encountered
        }

        List<UserTasksDTO> userTasksList = new ArrayList<>();

        // For each userId, fetch the tasks and convert them to DTOs
        for (Long id : userIds) {
            user = getUserById(id);

            List<Task> tasks = taskService.getTasksByUser(user);
            if (tasks.isEmpty()) {continue;}

            List<TaskDTO> taskDTOs = tasks.stream()
                    .map(TaskDTO::new)
                    .collect(Collectors.toList());

            userTasksList.add(new UserTasksDTO(id, taskDTOs));
        }
        return userTasksList;
    }

    // Create a new user
    public User createUser(String companyName, String username, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty or null.");
        }

        User user = new User();
        user.setRole(role);
        user.setUsername(username);

        if (role != UserRole.SUPER_USER) {
            Company company = companyService.getCompanyByName(companyName);  // Only non-SUPER_USER roles require a company
            user.setCompany(company);
        }

        return userRepository.save(user);
    }

    // Retrieve all user IDs
    public List<Long> getAllUserIds() {
        return userRepository.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    // Retrieve all user IDs for a specific company
    public List<Long> getUserIdsByCompany(Company company) {
        return userRepository.findByCompany(company).stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
