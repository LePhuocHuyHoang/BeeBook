package com.example.sample.dto;

import com.example.sample.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private Long id;
    private String name;
    private Date dob;
    private String bio;
    private List<Book> books = new ArrayList<>();
}
