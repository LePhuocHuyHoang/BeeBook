package com.example.sample.payloads;

import java.util.List;

import com.example.sample.dto.BookDTO;
import com.example.sample.entities.Book;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BookResponse {

    private List<Book> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;

}
