package com.sportsunity.backend.controller;

import com.sportsunity.backend.dto.TaskDTO;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Endpoint to create a task for a specific user
    @PostMapping
    public ResponseEntity<?> createTask(@RequestParam("userId") Long userId, @RequestBody String taskDescription) {
        try {
            Task task = taskService.createTask(userId, taskDescription);
            return ResponseEntity.status(201).body(new TaskDTO(task));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle case when the user is not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Handle invalid input
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Endpoint to fetch a specific task for a user
    @GetMapping
    public ResponseEntity<?> getTask(@RequestParam("taskId") Long taskId, @RequestParam("userId") Long userId) {
        try {
            Task task = taskService.getTask(taskId, userId);
            return ResponseEntity.ok(new TaskDTO(task));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle case when the task is not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Handle permission or validation errors
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Endpoint to delete a specific task for a user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId, @RequestParam("userId") Long userId) {
        try {
            taskService.deleteTask(taskId, userId);
            return ResponseEntity.ok("Task deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle case when the task is not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Handle permission or validation errors
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable("id") Long taskId,
            @RequestParam("userId") Long userId,
            @RequestBody String updatedDescription) {
        try {
            Task updatedTask = taskService.updateTask(taskId, userId, updatedDescription);
            return ResponseEntity.ok(new TaskDTO(updatedTask));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body("Permission Denied: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
