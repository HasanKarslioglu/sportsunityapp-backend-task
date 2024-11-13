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

    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody String name) {
        try {
            Company createdCompany = companyService.saveCompany(name.trim());
            return ResponseEntity.status(201).body(new CompanyDTO(createdCompany));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CompanyDTO(companyService.getCompanyById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<CompanyDTO> getCompanyByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(new CompanyDTO(companyService.getCompanyByName(name)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/name/{name}")
    public ResponseEntity<String> deleteCompanyByName(@PathVariable String name) {
        try {
            companyService.deleteCompanyByName(name);
            return ResponseEntity.status(200).body("{\"name\": " + name + "}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Company not found with name: " + name);
        }
    }
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteCompanyById(@PathVariable Long id) {
        try {
            companyService.deleteCompanyById(id);
            return ResponseEntity.status(200).body("{\"id\": " + id + "}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Company not found with id: " + id);
        }
    }
}
