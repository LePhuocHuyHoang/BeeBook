package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.common.constant.Constants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.BookDTO;
import com.beebook.beebookproject.dto.CommentDTO;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Book;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResourceNotFoundException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.BookResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.BookRequest;
import com.beebook.beebookproject.repositories.BookRepository;
import com.beebook.beebookproject.repositories.UserRepository;
import com.beebook.beebookproject.service.BookService;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    private static final String BOOK_STR = "Book";

    private ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PagedResponse<BookResponse> getAllBooks(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Sort sortInfo = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sortInfo);


        Page<Book> booksPage = bookRepository.findAll(pageable);
        System.out.println(booksPage.getContent());

        if (booksPage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), booksPage.getNumber(), booksPage.getSize(), booksPage.getTotalElements(),
                    booksPage.getTotalPages(), booksPage.isLast());
        }
        List<BookResponse> bookResponses = new ArrayList<>();
        BookResponse bookResponse = modelMapper.map(booksPage, BookResponse.class);
        bookResponse.setContent(booksPage.getContent());
        bookResponse.setPageNumber(booksPage.getNumber());
        bookResponse.setPageSize(booksPage.getSize());
        bookResponse.setTotalElements(booksPage.getTotalElements());
        bookResponse.setTotalPages(booksPage.getTotalPages());
        bookResponse.setLastPage(booksPage.isLast());
        bookResponses.add(bookResponse);
        return new PagedResponse<>(bookResponses, booksPage.getNumber(), booksPage.getSize(), booksPage.getTotalElements(), booksPage.getTotalPages(),
                booksPage.isLast());
    }

    @Override
    public ResponseEntity<?> getBook(Long bookId, String username) {
        boolean exists = bookRepository.existsById(bookId);

        if (exists) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
            User user = userRepository.findByUsername(username);
            double avg = bookRepository.averageRating(bookId);
            int rating = userRepository.getUserRating(user.getId(), bookId);
            BookDTO bookDTO = new BookDTO(book, avg, rating);
            return ResponseEntity.ok(bookDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Book with the provided ID does not exist."));
        }
    }

    @Override
    public ResponseEntity<?> addBook(BookRequest bookRequest) {
        try {
            if (bookRepository.existsByName(bookRequest.getName())) {
                throw new AccessDeniedException("Book with the same name already exists");
            }
            Book book = new Book();
            modelMapper.map(bookRequest, book);
            Book newBook = bookRepository.save(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
        } catch (AccessDeniedException e) {
            ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
    @Override
    public ResponseEntity<?> updateBook(Long id, BookRequest newBook) {
        boolean bookExists = bookRepository.existsById(id);
        if (!bookExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Book with the provided ID does not exist."));
        }
        boolean bookWithSameNameExists = bookRepository.existsByName(newBook.getName());
        if (bookWithSameNameExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Another book with the same name already exists."));
        }
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        book.setName(newBook.getName());
        book.setIntroduce(newBook.getIntroduce());
        book.setIbsn(newBook.getIbsn());
        book.setPublicationYear(newBook.getPublicationYear());
        book.setPublisher(newBook.getPublisher());
        book.setTotalPages(newBook.getTotalPages());
        book.setPointPrice(newBook.getPointPrice());
        book.setFileSource(newBook.getFileSource());
        book.setIsFree(newBook.getIsFree());

        Book updatedBook = bookRepository.save(book);
        BookRequest bookRequest = new BookRequest();
        modelMapper.map(updatedBook, bookRequest);
        return new ResponseEntity<>(bookRequest, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ApiResponse> deleteBook(Long bookId) {
            boolean exists = bookRepository.existsById(bookId);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Book with the provided ID does not exist."));
            }
            bookRepository.deleteBook(bookId);
            return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }
        @Override
    public List<SearchDTO> searchBook(String keyword) {
        List<Book> books = bookRepository.searchBook(keyword);
        List<SearchDTO> searchDTOs = new ArrayList<>();
        for(Book book : books){
            SearchDTO searchDTO = new SearchDTO(book.getId(), book.getName());
            searchDTOs.add(searchDTO);
        }
        return searchDTOs;
    }
    @Override
    public List<Book> getTop3BookSelling() {
        return bookRepository.getTop3BookSelling();
    }

    @Override
    public List<Book> filterBook(String typeName, String authorName, BigDecimal minPointPrice, BigDecimal maxPointPrice) {
        return  bookRepository.filterBook(typeName, authorName, minPointPrice, maxPointPrice);
    }

    @Override
    public ResponseEntity<ApiResponse> ratingBook(String userName, Long bookId, Long rating) {

        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userName);

        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
        Long userId = (long) checkExistsUser.getFirst()[0];

        Book book = bookRepository.findById(bookId).orElseThrow(() ->  new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        List<Integer> checkExistsRating = bookRepository.checkExistsRating(userId, bookId);
        if(checkExistsRating.get(0) == 1) {
            bookRepository.updateRating(userId, bookId, rating);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Update success"), HttpStatus.OK);
        }else {
            bookRepository.insertRating(userId, bookId, rating);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Insert success"), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> commentBook(String userName, Long bookId, String comment) {
        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userName);
        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
        Book book = bookRepository.findById(bookId).orElseThrow(() ->  new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        bookRepository.insertComment(userName, bookId, comment);
        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Comment success"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> reportBook(String userName, Long bookId, String reportContent) {
//        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userId);
        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userName);

        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
        Book book = bookRepository.findById(bookId).orElseThrow(() ->  new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        Long userId = (long) checkExistsUser.getFirst()[0];
        bookRepository.reportBook(userId , bookId, reportContent);
        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Report success"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getComment(Long bookId, Long page, Long limit) {
        bookRepository.findById(bookId).orElseThrow(()-> new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        if(page < 0 || limit < 0) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "The value of the parameter cannot be negative"), HttpStatus.BAD_REQUEST);
        }
        Long offset = (page - 1) * limit;
//        Long fetch = limit;
        List<Object[]> objects = bookRepository.getComment(bookId, offset, limit);
        List<CommentDTO> commentDTOs = new ArrayList<CommentDTO>();
        for(Object[] obj: objects) {
            String userName = (String) obj[0];
            String comment = (String) obj[1];
            Date date = (Date) obj[2];
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String createdAt = formatter.format(date);
            commentDTOs.add(new CommentDTO(userName, comment, createdAt));
        }
        return new ResponseEntity<List<CommentDTO>>(commentDTOs, HttpStatus.OK);
    }
    @Override
    public List<Map<String, Object>> getFeaturedBooks(int top) {
        List<Map<String, Object>> featuredBooks = bookRepository.getFeaturedBooks(top);
        return modifyBooksInfo(featuredBooks);
    }
    @Override
    public List<Map<String, Object>> getNewBooks() {
        List<Map<String, Object>> newBooks = bookRepository.getNewBooks();
        return modifyBooksInfo(newBooks);
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
    
}
