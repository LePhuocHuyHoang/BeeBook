package com.beebook.beebookproject.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

//    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
//    private Set<User> users = new HashSet<>();

    // Constructors, Getters, and Setters
}
