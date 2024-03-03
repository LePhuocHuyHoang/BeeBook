package com.example.sample.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "book_name", length = 255, nullable = false)
    private String name;

    @Column(name = "introduce", nullable = false)
    private Long introduce;

    @Column(name = "IBSN", nullable = false)
    private Long ibsn;

    @Column(name = "publication_year", nullable = false)
    private Long publicationYear;

    @Column(name = "publisher", length = 255, nullable = false)
    private String publisher;

    @Column(name = "total_pages", nullable = false)
    private Long totalPages;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "file_source", nullable = false)
    private Long fileSource;

    @Column(name = "is_free", nullable = false)
    private Long isFree;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_type",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<Type> types = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comment", // Tên của bảng trung gian
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users_comment;
//
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "rating",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users_rating;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "report",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users_report;
//
//
//    // Định nghĩa quan hệ Many-to-Many với bảng User
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "bookmark", // Tên của bảng trung gian
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users_bookmark;
//
//    // Định nghĩa quan hệ Many-to-Many với bảng User
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "rental_receipt", // Tên của bảng trung gian
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users_rental;

}
