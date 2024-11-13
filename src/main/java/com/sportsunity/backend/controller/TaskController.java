package com.sportsunity.backend.controller;

import com.sportsunity.backend.dto.TaskDTO;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.service.TaskService;
import com.sportsunity.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestParam("userId") Long userId, @RequestBody String taskDescription) {
        User user;
        try {
            user = userService.getUserById(userId);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(404).body("User not found.");
        }

        Task task = new Task();
        task.setDescription(taskDescription);
        task.setUser(user);

        Task savedTask = taskService.saveTask(task);
        return ResponseEntity.status(201).body(new TaskDTO(savedTask));
    }

    @GetMapping
    public ResponseEntity<?> getTask(@RequestParam("taskId") Long taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found.");
        }
        return ResponseEntity.ok(new TaskDTO(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId, @RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found.");
        }

        try {
            taskService.deleteTask(taskId, user);
            return ResponseEntity.ok("Task deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
