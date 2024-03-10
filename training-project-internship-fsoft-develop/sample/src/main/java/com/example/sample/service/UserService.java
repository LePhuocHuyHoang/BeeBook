package com.example.sample.service;


import com.example.sample.dto.CommentDTO;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.User;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.UserRespone;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;


public interface UserService {
    PagedResponse<UserRespone> getAllUsers(int page, int size);
    ResponseEntity<ApiResponse> deleteComment(Long commentId);
    List<CommentDTO> getAllComment(Long offset, Long fetch);
    ResponseEntity<ApiResponse> deleteUser(Long userId);
    ResponseEntity<?> getUser(Long id);
    List<SearchDTO> searchUser(String keyword);
    List<User> filterUser(String gender, Long DOB, BigDecimal minPoint, BigDecimal maxPoint);
    List<User> getTop3BestUsers();
    ResponseEntity<?> getRentedBook(Long userId, Long month, Long year, Long offset, Long fetch);
}
