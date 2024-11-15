package com.sportsunity.backend;

import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.TaskRepository;
import com.sportsunity.backend.service.TaskService;
import com.sportsunity.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Company company;

    @BeforeEach
    public void setUp() {
        // Initialize a sample company object
        company = new Company();
        company.setName("Tech Giants");
    }

    @Test
    public void testCreateTask() {
        // Create a mock user and task
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setDescription("Complete project documentation");

        // Mock behavior of UserService and TaskRepository
        when(userService.getUserById(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Call the method to test
        Task createdTask = taskService.createTask(1L, "Complete project documentation");

        // Verify results
        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getDescription()).isEqualTo("Complete project documentation");
        assertThat(createdTask.getUser()).isEqualTo(user);
    }

    @Test
    public void testGetTaskById() {
        // Set up a sample task
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setDescription("Complete project documentation");

        // Mock repository behavior
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        // Test retrieving the task by ID
        Task foundTask = taskService.getTaskById(1L);

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getDescription()).isEqualTo("Complete project documentation");
    }

    @Test
    public void testGetTaskById_NotFound() {
        // Simulate task not found in repository
        when(taskRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        try {
            taskService.getTaskById(999L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Task not found with ID: 999");
        }
    }

    @Test
    public void testDeleteTask() {
        // Set up user and task for deletion
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setDescription("Test task");

        // Mock repository and service behavior
        when(userService.getUserById(1L)).thenReturn(user);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        doNothing().when(taskRepository).deleteById(1L);

        // Call delete method
        taskService.deleteTask(1L, 1L);

        // Verify that repository delete method was called
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteTask_NotFound() {
        // Mock user
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        when(userService.getUserById(1L)).thenReturn(user);

        // Simulate deleting a task that does not exist
        try {
            taskService.deleteTask(999L, 1L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Task not found with ID: 999");
        }
    }

    @Test
    public void testDeleteTask_UserNotAllowed() {
        // Mock two users, one authorized and one unauthorized
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("jane_doe");
        differentUser.setRole(UserRole.STANDARD);
        differentUser.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);

        // Mock service and repository behavior
        when(userService.getUserById(2L)).thenReturn(differentUser);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        // Attempt deletion by unauthorized user
        try {
            taskService.deleteTask(1L, 2L);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("User with ID 2 does not have permission to delete task with ID 1");
        }
    }

    @Test
    public void testGetTasksByUser() {
        // Mock user and tasks
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setDescription("Complete project documentation");

        when(taskRepository.findByUser(user)).thenReturn(List.of(task));

        // Retrieve tasks by user
        List<Task> tasks = taskService.getTasksByUser(user);

        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getDescription()).isEqualTo("Complete project documentation");
    }

    @Test
    public void testGetTasksByUser_EmptyList() {
        // Mock user with no tasks
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        when(taskRepository.findByUser(user)).thenReturn(List.of());

        // Verify empty task list
        List<Task> tasks = taskService.getTasksByUser(user);

        assertThat(tasks).isEmpty();
    }

    @Test
    public void testGetTask() {
        // Mock user and task
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setDescription("Complete project documentation");

        // Mock service and repository
        when(userService.getUserById(1L)).thenReturn(user);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        // Retrieve task with user validation
        Task retrievedTask = taskService.getTask(1L, 1L);

        assertThat(retrievedTask).isNotNull();
        assertThat(retrievedTask.getDescription()).isEqualTo("Complete project documentation");
    }

    @Test
    public void testGetTask_UserNotAllowed() {
        // Mock two users and a task
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);

        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("jane_doe");
        differentUser.setRole(UserRole.STANDARD);
        differentUser.setCompany(company);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);

        // Mock behavior
        when(userService.getUserById(2L)).thenReturn(differentUser);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        // Attempt task access by unauthorized user
        try {
            taskService.getTask(1L, 2L);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("User with ID 2 does not have permission to access task with ID 1");
        }
    }
}
