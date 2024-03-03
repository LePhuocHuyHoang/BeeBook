package com.example.sample.service;

import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Type;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.TypeRespone;
import com.example.sample.payloads.request.TypeRequest;
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
