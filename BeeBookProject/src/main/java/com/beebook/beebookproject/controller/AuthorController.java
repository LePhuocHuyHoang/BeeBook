package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.AppConstants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Author;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.AuthorRespone;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.AuthorRequest;
import com.beebook.beebookproject.service.AuthorService;
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
    @GetMapping()
    public ResponseEntity<?> getAuthor(@RequestParam(name = "authorId") Long id) {
        return authorService.getAuthor(id);
    }
    @PostMapping
    public ResponseEntity<?> addAuthor(@RequestBody AuthorRequest authorRequest) {
        return authorService.addAuthor(authorRequest);
    }
    @PutMapping("")
    public ResponseEntity<?> updateBook(@RequestParam(name = "authorId") Long id,
                                                  @RequestBody AuthorRequest newAuthor) {
        return authorService.updateAuthor(id,newAuthor);
    }
    @DeleteMapping()
    public ResponseEntity<ApiResponse> deleteAuthor(@RequestParam(name = "authorId") Long authorId) {
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
