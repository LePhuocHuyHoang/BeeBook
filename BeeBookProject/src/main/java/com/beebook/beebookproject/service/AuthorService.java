package com.beebook.beebookproject.service;

import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Author;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.AuthorRespone;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.AuthorRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthorService {
    PagedResponse<AuthorRespone> getAllAuthors(int page, int size);
    ResponseEntity<?> getAuthor(Long id);
    ResponseEntity<?> addAuthor(AuthorRequest authorRequest);
    ResponseEntity<?> updateAuthor(Long id, AuthorRequest newAuthor);
    ResponseEntity<ApiResponse> deleteAuthor(Long id);
    List<SearchDTO> searchAuthor(String keyword);
    List<Author> getTop3BestAuthors();
    List<Author> filterAuthor(Long birthYear, String typeName);
}
