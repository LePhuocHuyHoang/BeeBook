package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Author;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResourceNotFoundException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.AuthorRespone;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.request.AuthorRequest;
import com.beebook.beebookproject.repositories.AuthorRepository;
import com.beebook.beebookproject.service.AuthorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class AuthorServiceImpl implements AuthorService {
    private AuthorRepository authorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public PagedResponse<AuthorRespone> getAllAuthors(int page, int size) {
        // Kiểm tra và xác thực số trang và kích thước trang.
        AppUtils.validatePageNumberAndSize(page, size);
        Sort sortInfo = Sort.by(Sort.Direction.DESC, "id");
        //Tạo đối tượng pageable để phân trang.
        Pageable pageable = PageRequest.of(page, size, sortInfo);
        Page<Author> authorsPage = authorRepository.findAll(pageable);
        System.out.println(authorsPage.getContent());
        //Kiểm tra không có phần tử thì trả về mảng rỗng.
        if (authorsPage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), authorsPage.getNumber(), authorsPage.getSize(), authorsPage.getTotalElements(),
                    authorsPage.getTotalPages(), authorsPage.isLast());
        }
        //Chuyển đổi Author sang AuthorRespone bằng modelMapper.
        List<AuthorRespone> authorResponses = new ArrayList<>();
            AuthorRespone authorResponse = modelMapper.map(authorsPage, AuthorRespone.class);
            authorResponse.setContent(authorsPage.getContent());
            authorResponse.setPageNumber(authorsPage.getNumber());
            authorResponse.setPageSize(authorsPage.getSize());
            authorResponse.setTotalElements(authorsPage.getTotalElements());
            authorResponse.setTotalPages(authorsPage.getTotalPages());
            authorResponse.setLastPage(authorsPage.isLast());
            authorResponses.add(authorResponse);
        return new PagedResponse<>(authorResponses, authorsPage.getNumber(), authorsPage.getSize(), authorsPage.getTotalElements(), authorsPage.getTotalPages(),
                authorsPage.isLast());
    }

    @Override
    public ResponseEntity<?> getAuthor(Long id) {
        boolean exists = authorRepository.existsById(id);
        if (exists) {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
            return ResponseEntity.ok(author);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Author with the provided ID does not exist."));
        }
    }
    @Override
    public ResponseEntity<?> addAuthor(AuthorRequest authorRequest) {
        try {
            if (authorRepository.existsByName(authorRequest.getName())) {
                throw new AccessDeniedException("Author with the same name already exists");
            }
            Author author = new Author();
            modelMapper.map(authorRequest, author);
            Author newAuthor = authorRepository.save(author);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAuthor);
        } catch (AccessDeniedException e) {
            ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateAuthor(Long id, AuthorRequest newAuthor) {
        boolean authorExists = authorRepository.existsById(id);
        if (!authorExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Author with the provided ID does not exist."));
        }
        boolean authorWithSameNameExists = authorRepository.existsByName(newAuthor.getName());
        if (authorWithSameNameExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Another author with the same name already exists."));
        }
        Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        author.setName(newAuthor.getName());
        author.setDob(newAuthor.getDob());
        author.setBio(newAuthor.getBio());
        Author updateAuthor = authorRepository.save(author);
        AuthorRequest authorRequest = new AuthorRequest();
        modelMapper.map(updateAuthor, authorRequest);
        return new ResponseEntity<>(authorRequest, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteAuthor(Long authorId) {
        boolean exists = authorRepository.existsById(authorId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Author with the provided ID does not exist."));
        }
        authorRepository.deleteAuthor(authorId);
        return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }

    @Override
    public List<SearchDTO> searchAuthor(String keyword) {
        List<Author> authors = authorRepository.searchAuthor(keyword);
        List<SearchDTO> searchDTOs = new ArrayList<>();
        for(Author author : authors){
            SearchDTO searchDTO = new SearchDTO(author.getId(), author.getName());
            searchDTOs.add(searchDTO);
        }
        return searchDTOs;
    }
    @Override
    public List<Author> getTop3BestAuthors() {
        return authorRepository.getTop3BestAuthors();
    }
    @Override
    public List<Author> filterAuthor(Long birthYear, String typeName) {
        return  authorRepository.filterAuthor(birthYear, typeName);
    }
}
