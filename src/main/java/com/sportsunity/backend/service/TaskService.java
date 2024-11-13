package com.sportsunity.backend.service;

import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public List<Task> getTasksForCompany(Company company) {
        return taskRepository.findByCompany(company);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);  // Standard user sees only their own tasks
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    private boolean canDeleteTask(User user, UserRole role, Task task) {
        switch (role) {
            case SUPER_USER:
                return true; // Super users can delete any task
            case COMPANY_ADMIN:
                // Company-Admin can delete tasks belonging to users in their company
                return task.getUser().getCompany().equals(user.getCompany());
            case STANDARD:
                // Standard users can delete only their own tasks
                return task.getUser().equals(user);
            default:
                return false; // If the role is unknown, return false
        }
    }

    public void deleteTask(Long id, User user) {
        Task task = getTaskById(id);
        UserRole role = user.getRole();

        if (canDeleteTask(user, role, task)) {
            taskRepository.delete(task);
        } else {
            throw new IllegalArgumentException("User does not have permission to delete this task.");
        }
    }

    // Example method to fetch a task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

}
