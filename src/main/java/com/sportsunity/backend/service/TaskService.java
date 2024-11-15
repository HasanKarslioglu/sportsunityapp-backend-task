package com.sportsunity.backend.service;

import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    // Retrieve all tasks associated with a given user
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    // Save a task to the repository
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    // Create a new task for a specified user
    public Task createTask(Long userId, String taskDescription) {
        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty or null.");
        }

        User user = userService.getUserById(userId);

        Task task = new Task();
        task.setDescription(taskDescription);
        task.setUser(user);
        saveTask(task);

        return task;
    }

    // Determine if a user can delete a task
    private boolean canDeleteTask(User user, UserRole role, Task task) {
        switch (role) {
            case SUPER_USER:
                return true; // Super users can delete any task
            case COMPANY_ADMIN:
                return task.getUser().getCompany().equals(user.getCompany()); // Can delete tasks within their company
            case STANDARD:
                return task.getUser().equals(user); // Can delete only their own tasks
            default:
                return false; // Unknown role, deletion is not allowed
        }
    }

    // Delete a task by its ID, only if the user has permission
    public void deleteTask(Long taskId, Long userId) {

        User user = userService.getUserById(userId);
        if (user == null) {throw new EntityNotFoundException("User not found with ID: " + userId);}

        Task task = getTaskById(taskId);
        if (task == null) {throw new EntityNotFoundException("Task not found with ID: " + taskId);}

        if (!hasPermissionToAccessTask(user, task)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have permission to delete task with ID " + taskId);
        }

        deleteTaskById(taskId);
    }

    // Delete a task by its ID without any additional checks
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // Retrieve a task by its ID and check if the user has permission to access it
    public Task getTask(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        User user = userService.getUserById(userId);

        if (!hasPermissionToAccessTask(user, task)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have permission to access task with ID " + taskId);
        }

        return task;
    }

    // Retrieve a task by its ID, throwing an EntityNotFoundException if not found
    public Task getTaskById(Long id) throws EntityNotFoundException {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
    }

    // Check if a user has permission to access a specific task
    private boolean hasPermissionToAccessTask(User user, Task task) {
        if (user == null || task == null) {
            return false;
        }

        switch (user.getRole()) {
            case SUPER_USER:
                return true;
            case COMPANY_ADMIN:
                return user.getCompany().equals(task.getUser().getCompany());
            case STANDARD:
                return user.getId().equals(task.getUser().getId());
            default:
                return false;
        }
    }

}
