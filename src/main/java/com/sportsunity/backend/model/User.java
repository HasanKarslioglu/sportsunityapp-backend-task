package com.sportsunity.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    private String role;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference // Prevents circular references during JSON serialization.
    private Company company;

    // A User can have multiple Tasks, but each Task is associated with one User.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks;

    public User() {}

    // Getters and Setters
    public Long getId() {return id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public UserRole getRole() {
        return UserRole.valueOf(role);
    }
    public void setRole(UserRole role) {this.role = role.toString();}
    public Optional<Company> getCompany() {return Optional.ofNullable(company);}
    public void setCompany(Company company) {this.company = company;}
    public List<Task> getTasks() {return tasks;}
    public void setId(Long id) {this.id = id;}
}
