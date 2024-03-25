package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.entities.Book;
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
import com.beebook.beebookproject.repositories.BookRepository;
import com.beebook.beebookproject.repositories.UserRepository;
import com.beebook.beebookproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(value = { "/user" })
public class UserController {
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

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
    @DeleteMapping("/comment")
    public ResponseEntity<ApiResponse> deleteComment(@RequestParam(name = "commentId") Long commentId) {
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
    @GetMapping()
    public ResponseEntity<?> getUser(@RequestParam(name = "userId") Long id) {
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
    @GetMapping("/bookmark")
    public ResponseEntity<List<Map<String, Object>>> getBookmark() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);
        List<Map<String, Object>> bookmarkList = userService.getBookmark(username);
        return new ResponseEntity<>(bookmarkList, HttpStatus.OK);
    }

    @PostMapping("/bookmark/{bookId}")
    public ResponseEntity<Map<String, String>> addBookToBookmark(@PathVariable Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Unauthorized"));
        }

        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);

        User user = userRepository.findByUsername(username);

        Map<String, String> response = new HashMap<>();

        if (user != null) {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();

                if (!user.getBookmark().contains(book)) {
                    user.getBookmark().add(book);
                    userRepository.save(user);
                    response.put("message", "Book added to bookmark successfully.");
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                } else {
                    response.put("message", "Book already exists in bookmark.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            } else {
                response.put("message", "Book not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/bookmark/{bookId}")
    public ResponseEntity<Map<String, String>> removeBookmark(@PathVariable Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Unauthorized"));
        }

        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);

        User user = userRepository.findByUsername(username);

        Map<String, String> response = new HashMap<>();

        if (user != null) {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();

                if (user.getBookmark().contains(book)) {
                    user.getBookmark().remove(book);
                    userRepository.save(user);
                    response.put("message", "Book removed from bookmark successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Book does not exist in bookmark.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.put("message", "Book not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @GetMapping("/getProfile")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getProfile(
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String username = Helpers.getUserByJWT(jwt);
        return userService.getProfile(username);
    }
}
