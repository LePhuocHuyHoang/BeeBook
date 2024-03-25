package com.beebook.beebookproject.service;


import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.dto.UserRegistrationDto;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.UserRespone;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface UserService {
    PagedResponse<UserRespone> getAllUsers(int page, int size);
    ResponseEntity<ApiResponse> deleteComment(Long commentId);
//    List<CommentDTO> getAllComment(Long offset, Long fetch);
    ResponseEntity<ApiResponse> deleteUser(String username);
    ResponseEntity<ApiResponse> deleteUserByUserName(String userName);
    ResponseEntity<?> getUser(Long id);


    List<SearchDTO> searchUser(String keyword);
    List<User> filterUser(String gender, Long DOB, BigDecimal minPoint, BigDecimal maxPoint);
    List<User> getTop3BestUsers();
    ResponseEntity<?> getRentedBook(String userName, Long month, Long year, Long offset, Long fetch);

    // Save User
    User save(UserRegistrationDto registrationDto);
    //User update(User user, Long userId);
    User getById(Long userId);
    void deleteUserById(Long userId);

    //Bunny cloud storage
    //void uploadProfilePicture(Long userId, String key, MultipartFile file, StorageProvider storageProvider) throws Exception;

    //Local storage
    //public ResponseDataModel add(User userEntity);

    public User findByUserName(String userName);

    // Save User
//    User save(UserRegistrationDto registrationDto);

    List<Map<String, Object>> getBookmark(String username);
    ResponseEntity<?> getProfile(String userName);
}
