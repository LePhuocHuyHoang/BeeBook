package com.beebook.beebookproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "\"user\"")
@Builder
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", length = 255, nullable = true)
    private String firstName;

    @Column(name = "last_name", length = 255, nullable = true)
    private String lastName;

    @Column(name = "DOB", length = 255, nullable = true)
    private Date dob;

    @Column(name = "email", length = 255, nullable = true)
    private String email;

    @Column(name = "username", length = 255, nullable = true)
    private String username;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "point", length = 255, nullable = true)
    private Long point;

    @Column(name = "gender", length = 255, nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Transient
    @Column(name = "avatar", nullable = true)
    private String avatar;

    @Transient
    private MultipartFile[] avatarFiles;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "bookmark",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @JsonIgnoreProperties("users")
    List<Book> bookmark = new ArrayList<>();


}
