package com.sportsunity.backend.controller;

import com.sportsunity.backend.dto.TaskDTO;
import com.sportsunity.backend.dto.UserDTO;
import com.sportsunity.backend.dto.UserTasksDTO;
import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.service.UserService;
import com.sportsunity.backend.service.TaskService;
import com.sportsunity.backend.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private TaskService taskService;

    // Existing createUser endpoint
    @PostMapping
    public ResponseEntity<?> createUser(@RequestParam("companyName") String name,
                                        @RequestParam("username") String username,
                                        @RequestParam("role") UserRole role) {
        Company company = null;
        try {
            if (role != UserRole.SUPER_USER) {
                company = companyService.getCompanyByName(name);
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Company not found with name: " + name);
        }

        User user = new User();
        user.setCompany(company);
        user.setRole(role);
        user.setUsername(username);
        userService.createUser(user);

        return ResponseEntity.status(201).body(new UserDTO(user));
    }


    // Get a specific user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new UserDTO(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with id: " + userId);
        }
    }

    // Delete a specific user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.status(204).build(); // No Content
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with id: " + userId);
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getUserTasks(@RequestParam("userId") Long userId) {
        User user;
        try {
             user = userService.getUserById(userId);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(404).body("User not found with id: " + userId);
        }

        UserRole role = user.getRole();
        List<Long> userIds;
        switch (role) {
            case SUPER_USER:
                userIds = userService.getAllUserIds();
                break;
            case COMPANY_ADMIN:
                userIds = userService.getUserIdsByCompany(user.getCompany().orElseThrow());
                break;
            case STANDARD:
                userIds = List.of(userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        List<UserTasksDTO> userTasksList = new ArrayList<>();
        System.out.println(userIds.toString());

        for (Long id : userIds) {

            List<Task> tasks = taskService.getTasksByUser(user);
            if (tasks.isEmpty()) {continue;}

            List<TaskDTO> taskDTOs = tasks.stream()
                    .map(TaskDTO::new)
                    .collect(Collectors.toList());

            userTasksList.add(new UserTasksDTO(id, taskDTOs));
        }

        return ResponseEntity.ok(userTasksList);
    }
}
