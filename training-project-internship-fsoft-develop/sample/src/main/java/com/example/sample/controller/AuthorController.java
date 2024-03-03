package com.example.sample.controller;

import com.example.sample.common.util.AppConstants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Author;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.AuthorRespone;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.AuthorRequest;
import com.example.sample.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = { "/author" })
public class AuthorController {
    private AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }
    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }

    @GetMapping("/all")
    public PagedResponse<AuthorRespone> getAllAuthors(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return authorService.getAllAuthors(page, size);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable(name = "id") Long id) {
        return authorService.getAuthor(id);
    }
    @PostMapping
    public ResponseEntity<?> addAuthor(@RequestBody AuthorRequest authorRequest) {
        return authorService.addAuthor(authorRequest);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable(name = "id") Long id,
                                                  @RequestBody AuthorRequest newAuthor) {
        return authorService.updateAuthor(id,newAuthor);
    }
    @DeleteMapping("/{authorId}")
    public ResponseEntity<ApiResponse> deleteAuthor(@PathVariable Long authorId) {
        return authorService.deleteAuthor(authorId);
    }
    @GetMapping("/search")
    public ResponseEntity<List<SearchDTO>> searchAuthor(@RequestParam(name = "keyword") String keyword) {
        List<SearchDTO> authors = authorService.searchAuthor(keyword);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }
    @GetMapping("/top3")
    public ResponseEntity<List<Author>> getTop3BestAuthors() {
        List<Author> topAuthors = authorService.getTop3BestAuthors();
        return new ResponseEntity<>(topAuthors, HttpStatus.OK);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Author>> filterAuthor(@RequestParam(name = "birthYear", required = false) Long birthYear,
                                                 @RequestParam(name = "typeName", required = false) String typeName) {
        List<Author> filterAuthor = authorService.filterAuthor(birthYear, typeName);
        return new ResponseEntity<>(filterAuthor, HttpStatus.OK);
    }
}
