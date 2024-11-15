package com.sportsunity.backend;

import com.sportsunity.backend.dto.UserTasksDTO;
import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.UserRepository;
import com.sportsunity.backend.service.CompanyService;
import com.sportsunity.backend.service.TaskService;
import com.sportsunity.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.persistence.EntityNotFoundException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserService userService;

    private Company company;
    private User user;

    @BeforeEach
    public void setUp() {
        company = new Company();
        company.setName("Tech Giants");

        user = new User();
        user.setUsername("john_doe");
        user.setRole(UserRole.STANDARD);
        user.setCompany(company);
    }

    @Test
    public void testCreateUser() {
        when(companyService.getCompanyByName("Tech Giants")).thenReturn(company);

        // Mocking userRepository to save the user and return the saved user
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Calling the service method to create the user
        User createdUser = userService.createUser("Tech Giants", "john_doe", UserRole.STANDARD);

        // Asserting that the created user is not null and has the expected properties
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("john_doe");
        assertThat(createdUser.getRole()).isEqualTo(UserRole.STANDARD);
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("john_doe");
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        try {
            userService.getUserById(999L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("User not found with ID: 999");
        }
    }

    @Test
    public void testDeleteUserById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserById_NotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        try {
            userService.deleteUserById(999L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("User not found with ID: 999");
        }
    }

    @Test
    public void testGetUserTasks_UserNotFound() {
        try {
            userService.getUserTasks(999L);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("User not found with ID: 999");
        }
    }
}
