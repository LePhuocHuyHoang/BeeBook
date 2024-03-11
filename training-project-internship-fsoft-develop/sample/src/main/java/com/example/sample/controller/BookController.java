package com.example.sample.controller;

import com.example.sample.common.util.AppConstants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Book;
import com.example.sample.exception.AccessDeniedException;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.BookResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.BookRequest;
import com.example.sample.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/ratingBook")
    public ResponseEntity<ApiResponse> ratingBook(@RequestParam Long userId,
                                                  @RequestParam Long bookId, @RequestParam Long rating,
                                                  HttpServletRequest request) {
        boolean isParamValid = request.getParameterValues("userId").length > 1
                || request.getParameterValues("bookId").length > 1 || request.getParameterValues("rating").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }
        if (rating < 1 || rating > 5) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid rating value"), HttpStatus.BAD_REQUEST);
        }
        return bookService.ratingBook(userId, bookId, rating);
    }

    @GetMapping("/commentBook")
    public ResponseEntity<ApiResponse> commentBook(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam String comment,
            HttpServletRequest request) {

        boolean isParamValid = request.getParameterValues("userId").length > 1
                || request.getParameterValues("bookId").length > 1
                || request.getParameterValues("comment").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }
        return bookService.commentBook(userId, bookId, comment);
    }

    @GetMapping("/reportBook")
    public ResponseEntity<ApiResponse> reportBook(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam String report,
            HttpServletRequest request) {
        boolean isParamValid = request.getParameterValues("userId").length > 1
                || request.getParameterValues("bookId").length > 1
                || request.getParameterValues("report").length > 1;
        if (isParamValid) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, "Only one param value on each attribute should be provided"),
                    HttpStatus.BAD_REQUEST);
        }
        return bookService.reportBook(userId, bookId, report);
    }

    @GetMapping("/getComment")
    public ResponseEntity<?> getComment(
            @RequestParam Long bookId,
            @RequestParam(required = false) Long offset,
            @RequestParam(required = false) Long fetch,
            HttpServletRequest request
    ) {
        try {
            boolean isParamValid = false;
            if (request.getParameterValues("bookId") != null && request.getParameterValues("bookId").length > 1) {
                isParamValid = true;
            }else if (request.getParameterValues("offset") != null && request.getParameterValues("offset").length > 1) {
                isParamValid = true;
            } else if (request.getParameterValues("fetch") != null && request.getParameterValues("fetch").length > 1) {
                isParamValid = true;
            }
            if (isParamValid) {
                throw new AccessDeniedException("Only one param value on each attribute should be provided");
            }
            if (offset == null) {
                offset =Long.valueOf(0) ;
            }
            if (fetch == null) {
                fetch = Long.valueOf(5);
            }

            return bookService.getComment(bookId, offset, fetch);
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
