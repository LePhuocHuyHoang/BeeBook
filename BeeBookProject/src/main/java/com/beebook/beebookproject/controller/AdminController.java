package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.AppConstants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.entities.Author;
import com.beebook.beebookproject.entities.Book;
import com.beebook.beebookproject.entities.Type;
import com.beebook.beebookproject.hdfs.HadoopClient;
import com.beebook.beebookproject.payloads.*;
import com.beebook.beebookproject.payloads.request.AuthorRequest;
import com.beebook.beebookproject.payloads.request.BookRequest;
import com.beebook.beebookproject.payloads.request.TypeRequest;
import com.beebook.beebookproject.service.AuthorService;
import com.beebook.beebookproject.service.BookService;
import com.beebook.beebookproject.service.TypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private BookService bookService;
    private TypeService typeService;
    private AuthorService authorService;

    public AdminController(BookService bookService, TypeService typeService, AuthorService authorService) {
        this.bookService = bookService;
        this.typeService = typeService;
        this.authorService = authorService;
    }

    @GetMapping("/author/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public PagedResponse<AuthorRespone> getAllAuthors(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return authorService.getAllAuthors(page, size);
    }

    @GetMapping("/author")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAuthor(@RequestParam(name = "authorId") Long id) {
        return authorService.getAuthor(id);
    }

    @PostMapping("/author")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addAuthor(@RequestBody AuthorRequest authorRequest) {
        return authorService.addAuthor(authorRequest);
    }

    @PutMapping("/author")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateBook(@RequestParam(name = "authorId") Long id,
                                        @RequestBody AuthorRequest newAuthor) {
        return authorService.updateAuthor(id,newAuthor);
    }

    @DeleteMapping("/author")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteAuthor(@RequestParam(name = "authorId") Long authorId) {
        return authorService.deleteAuthor(authorId);
    }

    @GetMapping("/type/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public PagedResponse<TypeRespone> getAllTypes(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return typeService.getAllTypes(page, size);
    }

    @GetMapping("/type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getType(@RequestParam(name = "typeId") Long id) {
        return typeService.getType(id);
    }

    @PostMapping("/type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addType(@RequestBody TypeRequest typeRequest) {
        return typeService.addType(typeRequest);
    }

    @PutMapping("/type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateType(@RequestParam(name = "typeId") Long id,
                                        @RequestBody TypeRequest newType) {
        return typeService.updateType(id,newType);
    }

    @DeleteMapping("/type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteType(@RequestParam(name = "typeId") Long typeId) {
        return typeService.deleteType(typeId);
    }

    @GetMapping("/book/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public PagedResponse<BookResponse> getAllBooks(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);
        return bookService.getAllBooks(page, size);
    }

    @GetMapping("/book/getBookGuest")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getBookGuest(@RequestParam(name = "bookId") Long bookId) {
        return bookService.getBookGuest(bookId);
    }

    @PostMapping("/book")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest) {
        return bookService.addBook(bookRequest);
    }

    @PutMapping("/book")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateBook(@RequestParam(name = "bookId") Long id,
                                        @RequestBody BookRequest newBook) {
        return bookService.updateBook(id,newBook);
    }

    @DeleteMapping("/book")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteBook(@RequestParam(name = "bookId") Long bookId) {
        return bookService.deleteBook(bookId);
    }
    @GetMapping("/book/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Book>> searchBookAdmin(@RequestParam(name = "keyword") String keyword) {
        List<Book> books = bookService.searchBookAdmin(keyword);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
    @GetMapping("/type/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Type>> searchType(@RequestParam(name = "keyword") String keyword) {
        List<Type> types = typeService.searchType(keyword);
        return new ResponseEntity<>(types, HttpStatus.OK);
    }
    @GetMapping("/author/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Author>> searchAuthor(@RequestParam(name = "keyword") String keyword) {
        List<Author> authors = authorService.searchAuthor(keyword);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/author/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Author>> filterAuthor(@RequestParam(name = "birthYear", required = false) Long birthYear,
                                                     @RequestParam(name = "typeName", required = false) String typeName) {
        List<Author> filterAuthor = authorService.filterAuthor(birthYear, typeName);
        return new ResponseEntity<>(filterAuthor, HttpStatus.OK);
    }
    @GetMapping("/book/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Book>> filterBook(@RequestParam(name = "typeName", required = false) String typeName,
                                                 @RequestParam(name = "authorName", required = false) String authorName,
                                                 @RequestParam(name = "minPrice", required = false) BigDecimal minPointPrice,
                                                 @RequestParam(name = "maxPrice", required = false) BigDecimal maxPointPrice) {
        List<Book> filterBook = bookService.filterBook(typeName, authorName, minPointPrice, maxPointPrice);
        return new ResponseEntity<>(filterBook, HttpStatus.OK);
    }
}
