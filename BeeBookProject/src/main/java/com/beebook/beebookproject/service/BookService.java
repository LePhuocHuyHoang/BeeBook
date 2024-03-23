package com.beebook.beebookproject.service;

import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Book;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.BookResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.BookRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BookService {

    PagedResponse<BookResponse> getAllBooks(int page, int size);

    ResponseEntity<?> getBook(Long bookId, String username);

    ResponseEntity<ApiResponse> deleteBook(Long bookId);

    ResponseEntity<?> addBook(BookRequest BookRequest);

    ResponseEntity<?> updateBook(Long id, BookRequest newBook);
    List<SearchDTO> searchBook(String keyword);

    List<Book> getTop3BookSelling();

    List<Book> filterBook(String typeName, String authorName, BigDecimal minPointPrice, BigDecimal maxPointPrice);

    ResponseEntity<ApiResponse> ratingBook(String userId, Long bookId, Long rating);
    ResponseEntity<ApiResponse> commentBook(String userName, Long bookId, String comment);
    ResponseEntity<ApiResponse> reportBook(String userName, Long bookId, String reportContent);
    ResponseEntity<?> getComment(Long bookId, Long page, Long limit);
    List<Map<String, Object>> getFeaturedBooks(int top);

    List<Map<String, Object>> getNewBooks();

}
