package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.common.util.AppConstants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Book;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.payloads.BookResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.BookRequest;
import com.beebook.beebookproject.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @GetMapping()
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getBook(@RequestParam(name = "bookId") Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);
        return bookService.getBook(bookId,username);
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody BookRequest bookRequest) {
        return bookService.addBook(bookRequest);
    }

    @PutMapping()
    public ResponseEntity<?> updateBook(@RequestParam(name = "bookId") Long id,
                                                   @RequestBody BookRequest newBook) {
        return bookService.updateBook(id,newBook);
    }
    @DeleteMapping()
    public ResponseEntity<ApiResponse> deleteBook(@RequestParam(name = "bookId") Long bookId) {
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
                                                 @RequestParam(name = "minPrice", required = false) BigDecimal minPointPrice,
                                                 @RequestParam(name = "maxPrice", required = false) BigDecimal maxPointPrice) {
        List<Book> filterBook = bookService.filterBook(typeName, authorName, minPointPrice, maxPointPrice);
        return new ResponseEntity<>(filterBook, HttpStatus.OK);
    }

    @PostMapping("/ratingBook")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse> ratingBook(@RequestParam Long bookId,
                                                  @RequestParam Long rating,
                                                  HttpServletRequest request) {
        boolean isParamValid = request.getParameterValues("bookId").length > 1 || request.getParameterValues("rating").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }
        if (rating < 1 || rating > 5) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid rating value"), HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);
        return bookService.ratingBook(username, bookId, rating);
    }
    @PostMapping("/commentBook")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse> commentBook(
            @RequestParam Long bookId,
            @RequestParam String comment,
            HttpServletRequest request) {

        boolean isParamValid = request.getParameterValues("bookId").length > 1
                || request.getParameterValues("comment").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);

        return bookService.commentBook(username, bookId, comment);
    }

    @PostMapping("/reportBook")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse> reportBook(

            @RequestParam Long bookId,
            @RequestParam String report,
            HttpServletRequest request) {
        boolean isParamValid = request.getParameterValues("bookId").length > 1
                || request.getParameterValues("report").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);

        return bookService.reportBook(username, bookId, report);
    }

    @GetMapping("/getComments")
    public ResponseEntity<?> getComment(
            @RequestParam Long bookId,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long limit,
            HttpServletRequest request
    ) {
        try {
            boolean isParamValid = false;
            if (request.getParameterValues("bookId") != null && request.getParameterValues("bookId").length > 1) {
                isParamValid = true;
            }else if (request.getParameterValues("page") != null && request.getParameterValues("page").length > 1) {
                isParamValid = true;
            } else if (request.getParameterValues("limit") != null && request.getParameterValues("limit").length > 1) {
                isParamValid = true;
            }
            if (isParamValid) {
                throw new AccessDeniedException("Only one param value on each attribute should be provided");
            }
            if (page == null) {
                page =Long.valueOf(1) ;
            }
            if (limit == null) {
                limit = Long.valueOf(5);
            }

            return bookService.getComment(bookId, page, limit);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/featured")
    public ResponseEntity<List<Map<String, Object>>> getFeaturedBooks(
            @RequestParam(name = "top", required = false, defaultValue = "5") int top) {
        List<Map<String, Object>> books = bookService.getFeaturedBooks(top);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/newbooks")
    public List<Map<String, Object>> getNewBooks() {
        return bookService.getNewBooks();
    }
}
