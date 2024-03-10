package com.example.sample.service.impl;

import com.example.sample.common.constant.Constants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.CommentDTO;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Book;
import com.example.sample.exception.AccessDeniedException;
import com.example.sample.exception.ResourceNotFoundException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.BookResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.request.BookRequest;
import com.example.sample.repositories.BookRepository;
import com.example.sample.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
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
    public ResponseEntity<?> getBook(Long id) {
        boolean exists = bookRepository.existsById(id);
        if (exists) {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
            return ResponseEntity.ok(book);
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
        book.setPrice(newBook.getPrice());
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
    public List<Book> filterBook(String typeName, String authorName, BigDecimal minPrice, BigDecimal maxPrice) {
        return  bookRepository.filterBook(typeName, authorName, minPrice, maxPrice);
    }

    @Override
    public ResponseEntity<ApiResponse> ratingBook(Long userId, Long bookId, Long rating) {

        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userId);
        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<ApiResponse> commentBook(Long userId, Long bookId, String comment) {
        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userId);
        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
        Book book = bookRepository.findById(bookId).orElseThrow(() ->  new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        bookRepository.insertComment(userId, bookId, comment);
        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Comment success"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> reportBook(Long userId, Long bookId, String reportContent) {
        List<Object[]> checkExistsUser = bookRepository.checkExistingUser(userId);
        if(checkExistsUser.isEmpty()) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
        }
        Book book = bookRepository.findById(bookId).orElseThrow(() ->  new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        bookRepository.reportBook(userId, bookId, reportContent);
        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Report success"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getComment(Long bookId, Long offset, Long fetch) {
        bookRepository.findById(bookId).orElseThrow(()-> new ResourceNotFoundException(BOOK_STR, Constants.ID, bookId));
        if(offset < 0 || fetch < 0) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "The value of the parameter cannot be negative"), HttpStatus.BAD_REQUEST);
        }
        List<Object[]> objects = bookRepository.getComment(bookId, offset, fetch);
        List<CommentDTO> commentDTOs = new ArrayList<CommentDTO>();
        for(Object[] obj: objects) {
            Long userId = (Long) obj[0];
            Long bookid = (Long) obj[1];
            String comment = (String) obj[2];
            Date date = (Date) obj[3];
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String createdAt = formatter.format(date);
            commentDTOs.add(new CommentDTO(userId, bookid, comment, createdAt));
        }
        return new ResponseEntity<List<CommentDTO>>(commentDTOs, HttpStatus.OK);
    }
}
