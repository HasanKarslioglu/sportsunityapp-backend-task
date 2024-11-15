package com.sportsunity.backend.repository;

import com.sportsunity.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long id);
    Optional<Company> findByName(String name);
}
