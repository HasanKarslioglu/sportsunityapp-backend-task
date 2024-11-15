package com.sportsunity.backend.controller;

import com.sportsunity.backend.dto.UserDTO;
import com.sportsunity.backend.dto.UserTasksDTO;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestParam("companyName") String companyName,
                                        @RequestParam("username") String username,
                                        @RequestParam("role") UserRole role) {
        try {
            User user = userService.createUser(companyName, username, role);
            return ResponseEntity.status(201).body(new UserDTO(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage()); // Handle invalid input
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage()); // Handle case when company is not found
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Endpoint to get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new UserDTO(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("User not found with id: " + userId); // Handle case when user is not found
        }
    }

    // Endpoint to delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.status(204).body("User deleted successfully with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle case when user deletion fails
        }
    }

    // Endpoint to get tasks assigned to a user
    @GetMapping("/tasks")
    public ResponseEntity<?> getUserTasks(@RequestParam("userId") Long userId) {
        try {
            List<UserTasksDTO> userTasks = userService.getUserTasks(userId);
            return ResponseEntity.ok(userTasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle case when user or tasks are not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // Handle invalid input or bad request
        }
    }
}
