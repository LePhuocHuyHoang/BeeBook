package com.beebook.beebookproject.dto;

import com.beebook.beebookproject.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
