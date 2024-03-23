package com.beebook.beebookproject.dto;

import com.beebook.beebookproject.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Book book;
    private double avg;
    private int rating;
//    private Long id;
//    private String name;
//    private Long introduce;
//    private Long ibsn;
//    private Long publicationYear;
//    private String publisher;
//    private Long totalPages;
//    private Long pointPrice;
//    private Long fileSource;
//    private Long isFree;
//    private List<Author> authors = new ArrayList<>();
//    private List<Type> types = new ArrayList<>();
//    private List<User> users_comment;
}
