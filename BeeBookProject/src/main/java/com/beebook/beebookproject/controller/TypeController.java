package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.AppConstants;
import com.beebook.beebookproject.common.util.AppUtils;
import com.beebook.beebookproject.dto.SearchDTO;
import com.beebook.beebookproject.entities.Type;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.payloads.PagedResponse;
import com.beebook.beebookproject.payloads.TypeRespone;
import com.beebook.beebookproject.payloads.request.TypeRequest;
import com.beebook.beebookproject.service.TypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = { "/type" })
public class TypeController {
    private TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }
    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }
    @GetMapping("/all")
    public PagedResponse<TypeRespone> getAllTypes(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return typeService.getAllTypes(page, size);
    }
    @GetMapping()
    public ResponseEntity<?> getType(@RequestParam(name = "typeId") Long id) {
        return typeService.getType(id);
    }
    @PostMapping
    public ResponseEntity<?> addType(@RequestBody TypeRequest typeRequest) {
        return typeService.addType(typeRequest);
    }
    @PutMapping()
    public ResponseEntity<?> updateType(@RequestParam(name = "typeId") Long id,
                                        @RequestBody TypeRequest newType) {
        return typeService.updateType(id,newType);
    }
    @DeleteMapping()
    public ResponseEntity<ApiResponse> deleteType(@RequestParam(name = "typeId") Long typeId) {
        return typeService.deleteType(typeId);
    }
    @GetMapping("/search")
    public ResponseEntity<List<SearchDTO>> searchType(@RequestParam(name = "keyword") String keyword) {
        List<SearchDTO> types = typeService.searchType(keyword);
        return new ResponseEntity<>(types, HttpStatus.OK);
    }
    @GetMapping("/top3")
    public ResponseEntity<List<Type>> getTop3BestTypes() {
        List<Type> topTypes = typeService.getTop3BestTypes();
        return new ResponseEntity<>(topTypes, HttpStatus.OK);
    }
}
