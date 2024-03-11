package com.example.sample.entities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

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

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "role_user",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles = new HashSet<>();
//
//    @ManyToMany(mappedBy = "users_rating", fetch = FetchType.LAZY)
//    private Set<Book> books_rating;
//
//    @ManyToMany(mappedBy = "users_report", fetch = FetchType.LAZY)
//    private Set<Book> books_report;
//
//    @ManyToMany(mappedBy = "users_comment", fetch = FetchType.EAGER)
//    @JsonIgnoreProperties("users_comment")
//    private List<Book> books_comment = new ArrayList<>();
//
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "bookmark",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @JsonIgnoreProperties("users")
    List<Book> bookmark = new ArrayList<>();
//
//    @ManyToMany(mappedBy = "users_report", fetch = FetchType.LAZY)
//    private Set<Book> books_rental;
    // Constructors, Getters, and Setters
}