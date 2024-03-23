package com.beebook.beebookproject.payloads.request;


import com.beebook.beebookproject.entities.Author;
import com.beebook.beebookproject.entities.Type;
import com.beebook.beebookproject.entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private float averageRating;
    private List<Author> authors = new ArrayList<>();
    private List<Type> types = new ArrayList<>();
    private List<User> users_comment = new ArrayList<>();
}
