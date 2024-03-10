package com.example.sample.service.impl;

import com.example.sample.common.constant.Constants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.CommentDTO;
import com.example.sample.dto.RentalReceiptDTO;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.User;
import com.example.sample.exception.ResourceNotFoundException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.UserRespone;
import com.example.sample.repositories.UserRepository;
import com.example.sample.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private static final String USER_STR = "User";

    @Autowired
    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public PagedResponse<UserRespone> getAllUsers(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Sort sortInfo = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sortInfo);
        Page<User> usersPage = userRepository.findAll(pageable);

        if (usersPage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), usersPage.getNumber(), usersPage.getSize(), usersPage.getTotalElements(),
                    usersPage.getTotalPages(), usersPage.isLast());
        }

        List<UserRespone> userResponses = new ArrayList<>();
            UserRespone userResponse = modelMapper.map(usersPage, UserRespone.class);
            userResponse.setContent(usersPage.getContent());
            userResponse.setPageNumber(usersPage.getNumber());
            userResponse.setPageSize(usersPage.getSize());
            userResponse.setTotalElements(usersPage.getTotalElements());
            userResponse.setTotalPages(usersPage.getTotalPages());
            userResponse.setLastPage(usersPage.isLast());
            userResponses.add(userResponse);
        return new PagedResponse<>(userResponses, usersPage.getNumber(), usersPage.getSize(), usersPage.getTotalElements(), usersPage.getTotalPages(),
                usersPage.isLast());
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> deleteComment(Long commentId) {
        boolean exists = userRepository.existsById(commentId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Comment with the provided ID does not exist."));
        }
        userRepository.deleteComment(commentId);
        return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }

    @Override
    public List<CommentDTO> getAllComment(Long offset, Long fetch) {
        List<Object[]> commentObjects = userRepository.getAllComment(offset, fetch);
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for (Object[] commentObject : commentObjects) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComment_id((Long) commentObject[0]);
            commentDTO.setBook_id((Long) commentObject[1]);
            commentDTO.setUser_id((Long) commentObject[2]);
            commentDTO.setComment((String) commentObject[3]);
            commentDTO.setCreated_at((Date) commentObject[4]);
            commentDTOs.add(commentDTO);
        }
        return commentDTOs;
    }
    @Override
    public ResponseEntity<ApiResponse> deleteUser(Long userId) {
        boolean exists = userRepository.existsUserById(userId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User with the provided ID does not exist."));
        }
        userRepository.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }
    @Override
    public ResponseEntity<?> getUser(Long id) {
        boolean exists = userRepository.existsUserById(id);
        if (exists) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User with the provided ID does not exist."));
        }
    }
    @Override
    public List<SearchDTO> searchUser(String keyword) {
        List<User> users = userRepository.searchUser(keyword);
        List<SearchDTO> searchDTOs = new ArrayList<>();
        for(User user : users){
            String fullName = user.getFirstName() + " " + user.getLastName();
            SearchDTO searchDTO = new SearchDTO(user.getId(), fullName);
            searchDTOs.add(searchDTO);
        }
        return searchDTOs;
    }
    @Override
    public List<User> filterUser(String gender, Long DOB, BigDecimal minPoint, BigDecimal maxPoint) {
        return  userRepository.filterUser(gender, DOB, minPoint, maxPoint);
    }
    @Override
    public List<User> getTop3BestUsers() {
        return userRepository.getTop3BestUsers();
    }
    @Override
    public ResponseEntity<?> getRentedBook(Long userId, Long month, Long year, Long offset, Long fetch) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(USER_STR, Constants.ID, userId));
        if(offset < 0 || fetch < 0) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "The value of the parameter cannot be negative"), HttpStatus.BAD_REQUEST);
        }
        List<Object[]> objects = userRepository.getRentedBook(userId, month, year, offset, fetch);
        List<RentalReceiptDTO> receiptDTOs = new ArrayList<RentalReceiptDTO>();
        for(Object[] obj: objects) {
            Long bookId = (Long) obj[0];
            String bookName = (String) obj[1];
            Date date = (Date) obj[2];
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String rentalDate = formatter.format(date);
            Long pointPrice = (Long) obj[3];
            receiptDTOs.add(new RentalReceiptDTO(bookId, bookName, rentalDate, pointPrice));
        }
        return new ResponseEntity<List<RentalReceiptDTO>>(receiptDTOs, HttpStatus.OK);
    }
}
