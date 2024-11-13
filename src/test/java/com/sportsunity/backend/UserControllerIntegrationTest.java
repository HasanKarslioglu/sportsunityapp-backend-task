package com.sportsunity.backend;

import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.UserRole;
import com.sportsunity.backend.repository.CompanyRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        companyRepository.deleteAll();

        // Create a test company
        testCompany = new Company();
        testCompany.setName("Tech Corp");
        companyRepository.save(testCompany);
    }

    @Test
    public void testCreateUser() throws Exception {
        // Perform a POST request to create a new user
        mockMvc.perform(post("/api/v1/users")
                        .param("companyId", testCompany.getId().toString())
                        .param("username", "sema")
                        .param("role", "STANDARD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("sema"))
                .andExpect(jsonPath("$.role").value("STANDARD"))
                .andExpect(jsonPath("$.company.name").value("Tech Corp"));

        // Verify the user was saved in the database
        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getUsername()).isEqualTo("sema");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.STANDARD);
        assertThat(savedUser.getCompany().map(Company::getName).orElse("")).isEqualTo("Tech Corp");
    }

    @Test
    public void testGetUsersInCompany_WithAdminRole() throws Exception {
        // Add a user with COMPANY_ADMIN role to the test company
        User adminUser = new User();
        adminUser.setUsername("admin_user");
        adminUser.setRole(UserRole.COMPANY_ADMIN);
        adminUser.setCompany(testCompany);
        userRepository.save(adminUser);

        // Perform a GET request to fetch users in the company with COMPANY_ADMIN role
        mockMvc.perform(get("/api/v1/users")
                        .param("companyId", testCompany.getId().toString())
                        .param("role", "COMPANY_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin_user"))
                .andExpect(jsonPath("$[0].role").value("COMPANY_ADMIN"));
    }

    @Test
    public void testGetUsersInCompany_WithForbiddenRole() throws Exception {
        // Add a user with STANDARD role to the test company
        User standardUser = new User();
        standardUser.setRole(UserRole.STANDARD);
        standardUser.setCompany(testCompany);
        standardUser.setUsername("standard_user");
        userRepository.save(standardUser);

        // Perform a GET request to fetch users in the company with STANDARD role, which should be forbidden
        mockMvc.perform(get("/api/v1/users")
                        .param("companyId", testCompany.getId().toString())
                        .param("role", "STANDARD"))
                .andExpect(status().isForbidden());
    }
}
