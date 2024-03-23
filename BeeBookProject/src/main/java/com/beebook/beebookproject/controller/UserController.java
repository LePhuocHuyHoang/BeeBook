package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.common.util.AppConstants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.CommentDTO;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.UserRespone;
import com.beebook.beebookproject.service.UserService;
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
    @DeleteMapping()
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam(name = "userName")String username) {
        return userService.deleteUser(username);
    }
//    @GetMapping("/comment/all")
//    public ResponseEntity<List<CommentDTO>> getAllComments(
//            @RequestParam(name = "offset", required = false, defaultValue = "0") Long offset,
//            @RequestParam(name = "fetch", required = false, defaultValue = "10") Long fetch) {
//        List<CommentDTO> comments = userService.getAllComment(offset, fetch);
//        return new ResponseEntity<>(comments, HttpStatus.OK);
//    }
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
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getRentedBook(
            @RequestParam(required = false) Long month,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long limit,
            HttpServletRequest request
    ) {
        try {
            boolean isParamValid = false;
            if (request.getParameterValues("month") != null && request.getParameterValues("month").length > 1) {
                isParamValid = true;
            }else if (request.getParameterValues("year") != null && request.getParameterValues("year").length > 1) {
                isParamValid = true;
            }
            else if (request.getParameterValues("page") != null && request.getParameterValues("page").length > 1) {
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

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
            String jwt = jwtToken.getTokenValue();
            String username = Helpers.getUserByJWT(jwt);
            Long offset = (page - 1) * limit;
            return userService.getRentedBook(username, month, year, offset, limit);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(
                    new ApiResponse(Boolean.FALSE, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/bookmark/{id}")
    public ResponseEntity<?> getBookmarkByUserId(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
