package com.example.sample.service;

import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Book;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.BookResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.BookRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {

    PagedResponse<BookResponse> getAllBooks(int page, int size);

    ResponseEntity<?> getBook(Long id);

    ResponseEntity<ApiResponse> deleteBook(Long bookId);

    ResponseEntity<?> addBook(BookRequest BookRequest);

    ResponseEntity<?> updateBook(Long id, BookRequest newBook);
    List<SearchDTO> searchBook(String keyword);

    List<Book> getTop3BookSelling();

    List<Book> filterBook(String typeName, String authorName, BigDecimal minPrice, BigDecimal maxPrice);

}
