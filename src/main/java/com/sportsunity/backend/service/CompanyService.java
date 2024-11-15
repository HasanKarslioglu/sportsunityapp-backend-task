package com.sportsunity.backend.service;

import com.sportsunity.backend.repository.CompanyRepository;
import com.sportsunity.backend.model.Company;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    // Save a new Company with the given name.
    // Throws IllegalArgumentException if a Company with the same name already exists.
    public Company saveCompany(String name) {
        companyRepository.findByName(name)
                .ifPresent(existingCompany -> {
                    throw new IllegalArgumentException("Company with name '" + name + "' already exists.");
                });

        Company company = new Company();
        company.setName(name);
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
