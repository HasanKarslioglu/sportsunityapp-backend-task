package com.sportsunity.backend.repository;

import com.sportsunity.backend.model.User;
import com.sportsunity.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByCompany(Company company);
}

