package com.sportsunity.backend.service;

import com.sportsunity.backend.dto.CompanyDTO;
import com.sportsunity.backend.repository.CompanyRepository;
import com.sportsunity.backend.model.Company;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(String name) {
        // Check if a company with the same name already exists
        companyRepository.findByName(name)
                .ifPresent(existingCompany -> {
                    throw new IllegalArgumentException("Company with name '" + name + "' already exists.");
                });

        Company company = new Company();
        company.setName(name);
        // Save the company if no duplicates are found
        return companyRepository.save(company);
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
    }

    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with name: " + name));
    }

    public void deleteCompanyById(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }
    public void deleteCompanyByName(String name) {
        Company company = companyRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with name: " + name));
        companyRepository.delete(company);
    }
}
