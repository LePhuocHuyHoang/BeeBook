package com.beebook.beebookproject.service;

import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Type;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.TypeRespone;
import com.beebook.beebookproject.payloads.request.TypeRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TypeService {
    PagedResponse<TypeRespone> getAllTypes(int page, int size);
    ResponseEntity<?> getType(Long id);
    ResponseEntity<?> addType(TypeRequest typeRequest);
    ResponseEntity<?> updateType(Long id, TypeRequest newType);
    ResponseEntity<ApiResponse> deleteType(Long typeId);
    List<SearchDTO> searchType(String keyword);
    List<Type> getTop3BestTypes();
}
