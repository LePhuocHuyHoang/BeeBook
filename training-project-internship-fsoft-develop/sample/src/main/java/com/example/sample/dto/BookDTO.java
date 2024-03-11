package com.example.sample.dto;

import com.example.sample.entities.Author;
import com.example.sample.entities.Type;
import com.example.sample.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Long pointPrice;
    private Long fileSource;
    private Long isFree;
    private List<Author> authors = new ArrayList<>();
    private List<Type> types = new ArrayList<>();
    private List<User> users_comment;
}
