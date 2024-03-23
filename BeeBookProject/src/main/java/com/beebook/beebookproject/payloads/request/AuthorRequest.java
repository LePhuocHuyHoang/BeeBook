package com.beebook.beebookproject.payloads.request;

import com.beebook.beebookproject.entities.Book;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
public class AuthorRequest {
    private Long id;
    private String name;
    private Date dob;
    private String bio;
    private Set<Book> books = new HashSet<>();
}
