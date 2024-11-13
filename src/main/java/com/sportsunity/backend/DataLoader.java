package com.sportsunity.backend;

import com.sportsunity.backend.model.*;
import com.sportsunity.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;


//@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;

    public DataLoader(UserRepository userRepository, TaskRepository taskRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Company company = new Company();
        company.setName("Sports Unity");
        companyRepository.save(company);

        User adminUser = new User();
        adminUser.setUsername("adminUser");
        adminUser.setRole(UserRole.COMPANY_ADMIN);
        adminUser.setCompany(company);

        User standardUser = new User();
        standardUser.setUsername("standardUser");
        standardUser.setRole(UserRole.STANDARD);
        standardUser.setCompany(company);

        User superUser = new User();
        superUser.setUsername("superUser");
        superUser.setRole(UserRole.SUPER_USER);

        userRepository.save(adminUser);
        userRepository.save(standardUser);
        userRepository.save(superUser);

        // Create tasks for users
        Task task1 = new Task();
        task1.setDescription("Standard User Task");
        task1.setUser(standardUser);

        Task task2 = new Task();
        task2.setDescription("Admin User Task");
        task2.setUser(adminUser);

        taskRepository.save(task1);
        taskRepository.save(task2);
    }
}
