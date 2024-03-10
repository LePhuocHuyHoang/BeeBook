package com.example.sample.controller;

import com.example.sample.common.util.AppConstants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.CommentDTO;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.User;
import com.example.sample.exception.AccessDeniedException;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.UserRespone;
import com.example.sample.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = { "/user" })
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }
    @GetMapping("/all")
    public PagedResponse<UserRespone> getAllUsers(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return userService.getAllUsers(page, size);
    }
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId) {
        return userService.deleteComment(commentId);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
    @GetMapping("/comments")
    public ResponseEntity<List<CommentDTO>> getAllComments(
            @RequestParam(name = "offset", required = false, defaultValue = "0") Long offset,
            @RequestParam(name = "fetch", required = false, defaultValue = "10") Long fetch) {
        List<CommentDTO> comments = userService.getAllComment(offset, fetch);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable(name = "id") Long id) {
        return userService.getUser(id);
    }
    @GetMapping("/search")
    public ResponseEntity<List<SearchDTO>> searchUser(@RequestParam(name = "keyword") String keyword) {
        List<SearchDTO> users = userService.searchUser(keyword);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<User>> filterUser(@RequestParam(name = "gender", required = false) String gender,
                                                 @RequestParam(name = "DOB", required = false) Long DOB,
                                                 @RequestParam(name = "minPoint", required = false) BigDecimal minPoint,
                                                 @RequestParam(name = "maxPoint", required = false) BigDecimal maxPoint) {
        List<User> filterUser = userService.filterUser(gender, DOB, minPoint, maxPoint);
        return new ResponseEntity<>(filterUser, HttpStatus.OK);
    }
    @GetMapping("/top3")
    public ResponseEntity<List<User>> getTop3BestUsers() {
        List<User> topUsers = userService.getTop3BestUsers();
        return new ResponseEntity<>(topUsers, HttpStatus.OK);
    }
    @GetMapping("/getRentedBook")
    public ResponseEntity<?> getRentedBook(
            @RequestParam Long userId,
            @RequestParam(required = false) Long month,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) Long offset,
            @RequestParam(required = false) Long fetch,
            HttpServletRequest request
    ) {
        try {
            boolean isParamValid = false;
            if (request.getParameterValues("userId") != null && request.getParameterValues("userId").length > 1) {
                isParamValid = true;
            } else if (request.getParameterValues("month") != null && request.getParameterValues("month").length > 1) {
                isParamValid = true;
            }else if (request.getParameterValues("year") != null && request.getParameterValues("year").length > 1) {
                isParamValid = true;
            }
            else if (request.getParameterValues("offset") != null && request.getParameterValues("offset").length > 1) {
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

            return userService.getRentedBook(userId, month, year, offset, fetch);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
