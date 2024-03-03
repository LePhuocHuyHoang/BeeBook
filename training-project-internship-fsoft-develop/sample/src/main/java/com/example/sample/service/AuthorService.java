package com.example.sample.service;

import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Author;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.AuthorRespone;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.AuthorRequest;
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
