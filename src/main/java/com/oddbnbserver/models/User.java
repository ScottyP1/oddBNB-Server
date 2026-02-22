package com.oddbnbserver.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    public enum Role {
        ADMIN,
        HOST,
        GUEST
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.GUEST;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Listing> hostedListings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "author")
    private List<Review> reviewsWritten;

    public User() {
    }

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password_hash = password;
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password_hash;
    }

    public void setPassword(String password) {
        this.password_hash = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
