package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.RentalReceiptDTO;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.dto.UserDto;
import com.beebook.beebookproject.dto.UserRegistrationDto;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.exception.ResourceNotFoundException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.UserRespone;
import com.beebook.beebookproject.repositories.BookRepository;
import com.beebook.beebookproject.repositories.UserRepository;
import com.beebook.beebookproject.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

@Autowired
private BookRepository bookRepository;

    private static final String USER_STR = "User";

    private final String secretKey = "*()!*#A#&*VDC";

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

//    @Override
//    public List<CommentDTO> getAllComment(Long offset, Long fetch) {
//        List<Object[]> commentObjects = userRepository.getAllComment(offset, fetch);
//        List<CommentDTO> commentDTOs = new ArrayList<>();
//
//        for (Object[] commentObject : commentObjects) {
//            CommentDTO commentDTO = new CommentDTO();
//            commentDTO.setComment_id((Long) commentObject[0]);
//            commentDTO.setBook_id((Long) commentObject[1]);
//            commentDTO.setUser_id((Long) commentObject[2]);
//            commentDTO.setComment((String) commentObject[3]);
//            commentDTO.setCreated_at((Date) commentObject[4]);
//            commentDTOs.add(commentDTO);
//        }
//        return commentDTOs;
//    }
    @Override
    public ResponseEntity<ApiResponse> deleteUser(String username) {
        User userOptional = userRepository.findByUsername(username);
        if (userOptional.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User with the provided " + username + " does not exist."));
        }
        Long userId = userOptional.getId();
        userRepository.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }
    @Override
    public ResponseEntity<ApiResponse> deleteUserByUserName(String userName) {
        boolean exists = userRepository.existsUserByUserName(userName);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User with the provided userName does not exist."));
        }
        userRepository.deleteUserByUserName(userName);
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
    public ResponseEntity<?> getRentedBook(String userName, Long month, Long year, Long offset, Long fetch) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(userName));
        if(!user.isPresent()){
            return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
        }
        Long userId = user.get().getId();
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

    @Override
    public User getById(Long userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);

    }

//    @Override
//    public void uploadProfilePicture(Long userId, String key, MultipartFile file, StorageProvider storageProvider) throws Exception {
//        User user = getById(userId);
//        String savedKey = bunnyNetService.uploadProfilePicture(file,null,key);
//        user.setStorageProvider(storageProvider);
//        user.setStorageId(savedKey);
//        userRepository.save(user);
//    }
//
//    @Override
//    public ResponseDataModel add(User userEntity) {
//        int responseCode = Constants.RESULT_CD_FAIL;
//        String responseMsg = StringUtils.EMPTY;
//        try {
//            if (findByUserName(userEntity.getUsername()) != null) {
//                responseMsg = "UserName is duplicated";
//                responseCode = Constants.RESULT_CD_DUPL;
//            } else {
//                MultipartFile[] logoFiles = userEntity.getAvatarFiles();
//                if (logoFiles != null && logoFiles[0].getSize() > 0) {
//                    String imagePath = FileHelper.addNewFile(userFolderPath, logoFiles);
//                    userEntity.setAvatar(imagePath);
//                }
//                userRepository.saveAndFlush(userEntity);
//                responseMsg = "User is added successfully";
//                responseCode = Constants.RESULT_CD_SUCCESS;
//            }
//        } catch (Exception e) {
//            responseMsg = "Error when adding user";
//        }
//        return new ResponseDataModel(responseCode, responseMsg);
//    }


    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setDob(registrationDto.getDob());
        user.setUsername(registrationDto.getUsername());
        user.setPassword(encrytePassword(registrationDto.getPassword() + secretKey));
        user.setPoint(registrationDto.getPoint());
        user.setGender(registrationDto.getGender());
        user.setAvatarFiles(registrationDto.getImage());
        return userRepository.save(user);
    }

    public String encrytePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private List<Map<String, Object>> modifyBooksInfo(List<Map<String, Object>> booksInfo) {
        List<Map<String, Object>> modifiedBooksInfo = new ArrayList<>();

        for (Map<String, Object> bookInfo : booksInfo) {
            Map<String, Object> modifiedBookInfo = new HashMap<>(bookInfo); // Tạo một bản sao của bản đồ để thay đổi
            String typesJson = (String) modifiedBookInfo.get("types");
            List<Map<String, Object>> types = parseTypesJson(typesJson);
            modifiedBookInfo.put("types", types);

            String authorsJson = (String) modifiedBookInfo.get("authors");
            List<Map<String, Object>> authors = parseAuthorsJson(authorsJson);
            modifiedBookInfo.put("authors", authors);

            modifiedBooksInfo.add(modifiedBookInfo);
        }

        return modifiedBooksInfo;
    }

    private List<Map<String, Object>> parseTypesJson(String typesJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(typesJson, new TypeReference<List<Map<String, Object>>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> parseAuthorsJson(String authorsJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(authorsJson, new TypeReference<List<Map<String, Object>>>(){});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public List<Map<String, Object>> getBookmark(String username) {
        User user = userRepository.findByUsername(username);
        List<Map<String, Object>> bookmark = userRepository.getBookmark(user.getId());
        return modifyBooksInfo(bookmark);
    }

    @Override
    public ResponseEntity<?> getProfile(String userName) {
        boolean isUserExists = Optional.ofNullable(userRepository.findByUsername(userName)).isPresent();
        if(!isUserExists){
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "User don't exists"), HttpStatus.BAD_REQUEST);
        }
        Object[] objects = userRepository.getProfile(userName).getFirst();
        UserDto userDto = new UserDto();
        System.out.print((String) objects[0]);
        userDto.setUserName((String) objects[0]);
        userDto.setFirstName((String) objects[1]);
        userDto.setLastName((String) objects[2]);
        userDto.setDob((Date) objects[3]);
        userDto.setPoint((Long) objects[4]);
        userDto.setGender((String) objects[5]);
        userDto.setEmail((String) objects[6]);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

}
