package com.example.sample.controller;

import com.example.sample.common.util.AppConstants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Book;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.BookResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.BookRequest;
import com.example.sample.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = { "/book" })
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }

    @GetMapping("/all")
    public PagedResponse<BookResponse> getAllBooks(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return bookService.getAllBooks(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id) {
        return bookService.getBook(id);
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest) {
        return bookService.addBook(bookRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable(name = "id") Long id,
                                                   @RequestBody BookRequest newBook) {
        return bookService.updateBook(id,newBook);
    }
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long bookId) {
        return bookService.deleteBook(bookId);
    }
    @GetMapping("/search")
    public ResponseEntity<List<SearchDTO>> searchBook(@RequestParam(name = "keyword") String keyword) {
        List<SearchDTO> books = bookService.searchBook(keyword);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
    @GetMapping("/top3")
    public ResponseEntity<List<Book>> getTop3BookSelling() {
        List<Book> topBooks = bookService.getTop3BookSelling();
        return new ResponseEntity<>(topBooks, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBook(@RequestParam(name = "typeName", required = false) String typeName,
                                                 @RequestParam(name = "authorName", required = false) String authorName,
                                                 @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                                 @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice) {
        List<Book> filterBook = bookService.filterBook(typeName, authorName, minPrice, maxPrice);
        return new ResponseEntity<>(filterBook, HttpStatus.OK);
    }
}
