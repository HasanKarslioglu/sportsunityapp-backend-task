package com.sportsunity.backend.controller;

import com.sportsunity.backend.dto.CompanyDTO;
import com.sportsunity.backend.model.Company;
import com.sportsunity.backend.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // Endpoint to create a new company
    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody String name) {
        try {
            Company createdCompany = companyService.saveCompany(name.trim());
            return ResponseEntity.status(201).body(new CompanyDTO(createdCompany));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Handle company with the same name already exists
        }
    }

    // Endpoint to fetch a company by its ID
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        try {
            Company company = companyService.getCompanyById(id);
            return ResponseEntity.ok(new CompanyDTO(company));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle company not found
        }
    }

    // Endpoint to fetch a company by its name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCompanyByName(@PathVariable String name) {
        try {
            Company company = companyService.getCompanyByName(name);
            return ResponseEntity.ok(new CompanyDTO(company));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle company not found
        }
    }

    // Endpoint to delete a company by its name
    @DeleteMapping("/name/{name}")
    public ResponseEntity<?> deleteCompanyByName(@PathVariable String name) {
        try {
            companyService.deleteCompanyByName(name);
            return ResponseEntity.status(200).body("{\"name\": " + name + "}");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle company not found
        }
    }

    // Endpoint to delete a company by its ID
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteCompanyById(@PathVariable Long id) {
        try {
            companyService.deleteCompanyById(id);
            return ResponseEntity.status(200).body("{\"id\": " + id + "}");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Handle company not found
        }
    }
}
