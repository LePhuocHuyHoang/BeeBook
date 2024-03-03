package com.example.sample.dto;

import com.example.sample.entities.Author;
import com.example.sample.entities.Type;
import com.example.sample.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String name;
    private Long introduce;
    private Long ibsn;
    private Long publicationYear;
    private String publisher;
    private Long totalPages;
    private Long price;
    private Long fileSource;
    private Long isFree;
    private Set<Author> authors = new HashSet<>();
    private Set<Type> types = new HashSet<>();
    private Set<User> users_comment;
}
