package com.sportsunity.backend;

import com.sportsunity.backend.model.Task;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.TaskRepository;
import com.sportsunity.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("Hasan");
        testUser.setRole(UserRole.STANDARD);
        userRepository.save(testUser);
    }

    @Test
    public void testCreateTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .param("username", "Hasan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"New Task Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("New Task Description"));

        // Verify the task was saved in the database
        Task task = taskRepository.findAll().get(0);
        assertThat(task.getDescription()).isEqualTo("New Task Description");
        assertThat(task.getUser()).isEqualTo(testUser);
    }

    @Test
    public void testGetTasks() throws Exception {
        // Add a task for the user in the database
        Task task = new Task();
        task.setDescription("Existing Task");
        task.setUser(testUser);
        taskRepository.save(task);

        mockMvc.perform(get("/tasks")
                        .param("username", "Hasan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Existing Task"));
    }

    @Test
    public void testDeleteTask() throws Exception {
        // Add a task for the user in the database
        Task task = new Task();
        task.setDescription("Task to deleted");
        task.setUser(testUser);

        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", task.getId())
                        .param("username", "Hasan"))
                .andExpect(status().isOk());

        // Verify the task was deleted from the database
        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }
}
