package com.sportsunity.backend;

import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.repository.CompanyRepository;
import com.sportsunity.backend.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityNotFoundException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {

    // Mocking the dependencies of the CompanyService
    @Mock
    private CompanyRepository companyRepository;

    // Injecting the mocks into the CompanyService
    @InjectMocks
    private CompanyService companyService;

    // Initializing mocks before each test method
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // OpenMocks will initialize all the mocks
    }

    // Test case for saving a new company
    @Test
    public void testSaveCompany() {
        String companyName = "New Tech Corp";
        Company company = new Company();
        company.setName(companyName);

        when(companyRepository.findByName(companyName)).thenReturn(java.util.Optional.empty()); // No company found by this name
        when(companyRepository.save(any(Company.class))).thenReturn(company); // Mocking the save method to return the company

        Company savedCompany = companyService.saveCompany(companyName);

        assertThat(savedCompany.getName()).isEqualTo(companyName);

        verify(companyRepository).findByName(companyName);
        verify(companyRepository).save(any(Company.class));
    }

    // Test case for attempting to save a company that already exists
    @Test
    public void testSaveCompany_ThrowsException_WhenCompanyAlreadyExists() {
        String companyName = "Existing Tech Corp";
        Company existingCompany = new Company();
        existingCompany.setName(companyName);

        // Mocking the behavior of companyRepository to return an existing company
        when(companyRepository.findByName(companyName)).thenReturn(java.util.Optional.of(existingCompany));

        try {
            // Calling the method under test and expecting an exception
            companyService.saveCompany(companyName);
        } catch (IllegalArgumentException e) {
            // Verifying the exception message
            assertThat(e.getMessage()).isEqualTo("Company with name '" + companyName + "' already exists.");
        }

        // Verifying that save was never called because the company already exists
        verify(companyRepository).findByName(companyName);
        verify(companyRepository, never()).save(any(Company.class));
    }

    // Test case for getting a company by ID
    @Test
    public void testGetCompanyById() {
        Company company = new Company();
        company.setName("Sports Unity");
        Long companyId = company.getId();

        // Mocking the behavior of companyRepository
        when(companyRepository.findById(companyId)).thenReturn(java.util.Optional.of(company));

        // Calling the method under test
        Company retrievedCompany = companyService.getCompanyById(companyId);

        // Asserting the result
        assertThat(retrievedCompany.getId()).isEqualTo(companyId);
        assertThat(retrievedCompany.getName()).isEqualTo("Sports Unity");

        // Verifying the interaction with the mock
        verify(companyRepository).findById(companyId);
    }

    // Test case for attempting to get a company by ID when it doesn't exist
    @Test
    public void testGetCompanyById_ThrowsException_WhenCompanyNotFound() {
        Long nonExistentId = 999L;

        // Mocking the behavior of companyRepository to return an empty Optional (no company found)
        when(companyRepository.findById(nonExistentId)).thenReturn(java.util.Optional.empty());

        try {
            // Calling the method under test and expecting an exception
            companyService.getCompanyById(nonExistentId);
        } catch (IllegalArgumentException e) {
            // Verifying the exception message
            assertThat(e.getMessage()).isEqualTo("Company not found with id: " + nonExistentId);
        }

        // Verifying the interaction with the mock
        verify(companyRepository).findById(nonExistentId);
    }

    // Test case for getting a company by name
    @Test
    public void testGetCompanyByName() {
        String companyName = "Tech Giants";
        Company company = new Company();
        company.setName(companyName);

        // Mocking the behavior of companyRepository
        when(companyRepository.findByName(companyName)).thenReturn(java.util.Optional.of(company));

        // Calling the method under test
        Company retrievedCompany = companyService.getCompanyByName(companyName);

        // Asserting the result
        assertThat(retrievedCompany.getName()).isEqualTo(companyName);

        // Verifying the interaction with the mock
        verify(companyRepository).findByName(companyName);
    }

    // Test case for attempting to get a company by name when it doesn't exist
    @Test
    public void testGetCompanyByName_ThrowsException_WhenCompanyNotFound() {
        String nonExistentName = "NonExistent Corp";

        // Mocking the behavior of companyRepository to return an empty Optional (no company found)
        when(companyRepository.findByName(nonExistentName)).thenReturn(java.util.Optional.empty());

        try {
            // Calling the method under test and expecting an exception
            companyService.getCompanyByName(nonExistentName);
        } catch (EntityNotFoundException e) {
            // Verifying the exception message
            assertThat(e.getMessage()).isEqualTo("Company not found with name: " + nonExistentName);
        }

        // Verifying the interaction with the mock
        verify(companyRepository).findByName(nonExistentName);
    }

    // Test case for deleting a company by ID
    @Test
    public void testDeleteCompanyById() {
        Company company = new Company();
        Long companyId = company.getId();

        // Mocking the behavior of companyRepository to return true (company exists)
        when(companyRepository.existsById(companyId)).thenReturn(true);

        // Calling the method under test
        companyService.deleteCompanyById(companyId);

        // Verifying the interaction with the mock (deleteById should be called)
        verify(companyRepository).deleteById(companyId);
    }

    // Test case for attempting to delete a company by ID that doesn't exist
    @Test
    public void testDeleteCompanyById_ThrowsException_WhenCompanyNotFound() {
        Long nonExistentId = 999L;

        // Mocking the behavior of companyRepository to return false (company doesn't exist)
        when(companyRepository.existsById(nonExistentId)).thenReturn(false);

        try {
            // Calling the method under test and expecting an exception
            companyService.deleteCompanyById(nonExistentId);
        } catch (EntityNotFoundException e) {
            // Verifying the exception message
            assertThat(e.getMessage()).isEqualTo("Company not found with id: " + nonExistentId);
        }

        // Verifying that deleteById was never called because the company doesn't exist
        verify(companyRepository, never()).deleteById(nonExistentId);
    }

    // Test case for deleting a company by name
    @Test
    public void testDeleteCompanyByName() {
        String companyName = "Tech Giants";
        Company company = new Company();
        company.setName(companyName);

        // Mocking the behavior of companyRepository to return the company
        when(companyRepository.findByName(companyName)).thenReturn(java.util.Optional.of(company));

        // Calling the method under test
        companyService.deleteCompanyByName(companyName);

        // Verifying the interaction with the mock (delete should be called)
        verify(companyRepository).delete(company);
    }

    // Test case for attempting to delete a company by name that doesn't exist
    @Test
    public void testDeleteCompanyByName_ThrowsException_WhenCompanyNotFound() {
        String nonExistentName = "NonExistent Corp";

        // Mocking the behavior of companyRepository to return an empty Optional (no company found)
        when(companyRepository.findByName(nonExistentName)).thenReturn(java.util.Optional.empty());

        try {
            // Calling the method under test and expecting an exception
            companyService.deleteCompanyByName(nonExistentName);
        } catch (EntityNotFoundException e) {
            // Verifying the exception message
            assertThat(e.getMessage()).isEqualTo("Company not found with name: " + nonExistentName);
        }

        // Verifying that delete was never called because the company doesn't exist
        verify(companyRepository, never()).delete(any(Company.class));
    }
}
