package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Type;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResourceNotFoundException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.TypeRespone;
import com.beebook.beebookproject.payloads.request.TypeRequest;
import com.beebook.beebookproject.repositories.TypeRepository;
import com.beebook.beebookproject.service.TypeService;
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
public class TypeServiceImpl implements TypeService {
    private TypeRepository typeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public TypeServiceImpl(TypeRepository typeRepository, ModelMapper modelMapper) {
        this.typeRepository = typeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PagedResponse<TypeRespone> getAllTypes(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Sort sortInfo = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sortInfo);

        Page<Type> typesPage = typeRepository.findAll(pageable);

        if (typesPage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), typesPage.getNumber(), typesPage.getSize(), typesPage.getTotalElements(),
                    typesPage.getTotalPages(), typesPage.isLast());
        }

        List<TypeRespone> typeResponses = new ArrayList<>();
            TypeRespone typeResponse = modelMapper.map(typesPage, TypeRespone.class);
            typeResponse.setContent(typesPage.getContent());
            typeResponse.setPageNumber(typesPage.getNumber());
            typeResponse.setPageSize(typesPage.getSize());
            typeResponse.setTotalElements(typesPage.getTotalElements());
            typeResponse.setTotalPages(typesPage.getTotalPages());
            typeResponse.setLastPage(typesPage.isLast());
            typeResponses.add(typeResponse);
        return new PagedResponse<>(typeResponses, typesPage.getNumber(), typesPage.getSize(), typesPage.getTotalElements(), typesPage.getTotalPages(),
                typesPage.isLast());
    }
    @Override
    public ResponseEntity<?> getType(Long id) {
        boolean exists = typeRepository.existsById(id);
        if (exists) {
            Type type = typeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Type", "id", id));
            return ResponseEntity.ok(type);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Type with the provided ID does not exist."));
        }
    }
    @Override
    public ResponseEntity<?> addType(TypeRequest typeRequest) {
        try {
            if (typeRepository.existsByName(typeRequest.getName())) {
                throw new AccessDeniedException("Type with the same name already exists");
            }
            Type type = new Type();
            modelMapper.map(typeRequest, type);
            Type newType = typeRepository.save(type);
            return ResponseEntity.status(HttpStatus.CREATED).body(newType);
        } catch (AccessDeniedException e) {
            ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
    @Override
    public ResponseEntity<?> updateType(Long id, TypeRequest newType) {
        boolean typeExists = typeRepository.existsById(id);
        if (!typeExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Type with the provided ID does not exist."));
        }
        boolean typeWithSameNameExists = typeRepository.existsByName(newType.getName());
        if (typeWithSameNameExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Another type with the same name already exists."));
        }
        Type type = typeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type", "id", id));

        type.setName(newType.getName());
        type.setDescription(newType.getDescription());

        Type updatedType = typeRepository.save(type);
        TypeRequest typeRequest = new TypeRequest();
        modelMapper.map(updatedType, typeRequest);
        return new ResponseEntity<>(typeRequest, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ApiResponse> deleteType(Long typeId) {
        boolean exists = typeRepository.existsById(typeId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Type with the provided ID does not exist."));
        }
        typeRepository.deleteType(typeId);
        return ResponseEntity.ok(new ApiResponse(true, "Deleted successfully."));
    }
    @Override
    public List<SearchDTO> searchType(String keyword) {
        List<Type> types = typeRepository.searchType(keyword);
        List<SearchDTO> searchDTOs = new ArrayList<>();
        for(Type type : types){
            SearchDTO searchDTO = new SearchDTO(type.getId(), type.getName());
            searchDTOs.add(searchDTO);
        }
        return searchDTOs;
    }
    @Override
    public List<Type> getTop3BestTypes() {
        return typeRepository.getTop3BestTypes();
    }
}
