package com.example.sample.entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "\"user\"")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 255, nullable = false)
    private String lastName;

    @Column(name = "DOB", nullable = false)
    private Date dob;

    @Column(name = "username", length = 255, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "point", nullable = false)
    private Long point;

    @Column(name = "gender", length = 255, nullable = false)
    private String gender;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
//
//    @ManyToMany(mappedBy = "users_rating", fetch = FetchType.LAZY)
//    private Set<Book> books_rating;
//
//    @ManyToMany(mappedBy = "users_report", fetch = FetchType.LAZY)
//    private Set<Book> books_report;
//
    @ManyToMany(mappedBy = "users_comment", fetch = FetchType.LAZY)
    private Set<Book> books_comment;
//
//    @ManyToMany(mappedBy = "users_bookmark", fetch = FetchType.LAZY)
//    private Set<Book> books_bookmark;
//
//    @ManyToMany(mappedBy = "users_report", fetch = FetchType.LAZY)
//    private Set<Book> books_rental;
    // Constructors, Getters, and Setters
}