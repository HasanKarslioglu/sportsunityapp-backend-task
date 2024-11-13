package com.sportsunity.backend.dto;


import com.sportsunity.backend.model.Company;

import java.util.List;
import java.util.stream.Collectors;
import com.sportsunity.backend.model.User;

public class CompanyDTO {
    private Long id;
    private String name;
    private List<Long> userIds;

    public CompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.userIds = company.getUsers() != null
                ? company.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList())
                : List.of();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
}
