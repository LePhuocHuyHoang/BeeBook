package com.beebook.beebookproject.payloads;

import com.beebook.beebookproject.entities.Author;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorRespone {

    private List<Author> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
