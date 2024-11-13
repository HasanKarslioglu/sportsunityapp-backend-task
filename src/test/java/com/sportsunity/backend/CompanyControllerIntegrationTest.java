package com.sportsunity.backend;

import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    public void setUp() {
        companyRepository.deleteAll();
    }

    @Test
    public void testCreateCompany() throws Exception {
        String companyName = "New Tech Corp";

        // Perform the first POST request to create the company
        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(companyName))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Tech Corp"));

        // Perform the second POST request to create the same company
        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(companyName))
                .andExpect(status().isBadRequest());

        // Verify that the company still exists in the repository, only one entry
        assertThat(companyRepository.count()).isEqualTo(1);
    }


    @Test
    public void testGetCompanyById() throws Exception {
        // Create a company to retrieve
        Company company = new Company();
        company.setName("Sports Unity");
        companyRepository.save(company);

        // Perform GET request to retrieve the company by ID
        mockMvc.perform(get("/api/v1/companies/{id}", company.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sports Unity"));

        // Case where company does not exist
        mockMvc.perform(get("/api/v1/companies/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCompany() throws Exception {
        // Create a company to delete
        Company company = new Company();
        company.setName("Tech Giants");
        companyRepository.save(company);

        long id = company.getId();
        // Perform DELETE request to remove the company
        mockMvc.perform(delete("/api/v1/companies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        // Verify that the company was deleted
        assertThat(companyRepository.existsById(company.getId())).isFalse();

        // Case where company does not exist
        mockMvc.perform(delete("/api/v1/companies/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCompanyByName() throws Exception {
        // Create and save a company
        Company company = new Company();
        company.setName("New Tech Corp");
        companyRepository.save(company);

        // Perform a GET request to get the company by name
        mockMvc.perform(get("/api/v1/companies/name/{name}", "New Tech Corp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Tech Corp"));
    }

    @Test
    public void testGetCompanyByName_NotFound() throws Exception {
        // Case where company name does not exist
        mockMvc.perform(get("/api/v1/companies/name/{name}", "NonExistent Corp"))
                .andExpect(status().isNotFound());
    }
}
