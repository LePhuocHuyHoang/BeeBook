package com.example.sample.payloads.request;


import com.example.sample.entities.Author;
import com.example.sample.entities.Type;
import com.example.sample.entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter @Setter
public class BookRequest{

    private Long id;
    private String name;
    private String introduce;
    private Long ibsn;
    private Date publicationYear;
    private String publisher;
    private Long totalPages;
    private Long pointPrice;
    private String fileSource;
    private Long isFree;
    private Set<Author> authors = new HashSet<>();
    private Set<Type> types = new HashSet<>();
    private Set<User> users_comment;
}
