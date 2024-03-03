package com.example.sample.controller;

import com.example.sample.common.util.AppConstants;
import com.example.sample.common.util.AppUtils;
import com.example.sample.dto.SearchDTO;
import com.example.sample.entities.Type;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.payloads.PagedResponse;
import com.example.sample.payloads.TypeRespone;
import com.example.sample.payloads.request.TypeRequest;
import com.example.sample.service.TypeService;
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getType(@PathVariable(name = "id") Long id) {
        return typeService.getType(id);
    }
    @PostMapping
    public ResponseEntity<?> addType(@RequestBody TypeRequest typeRequest) {
        return typeService.addType(typeRequest);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateType(@PathVariable(name = "id") Long id,
                                        @RequestBody TypeRequest newType) {
        return typeService.updateType(id,newType);
    }
    @DeleteMapping("/{typeId}")
    public ResponseEntity<ApiResponse> deleteType(@PathVariable Long typeId) {
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
