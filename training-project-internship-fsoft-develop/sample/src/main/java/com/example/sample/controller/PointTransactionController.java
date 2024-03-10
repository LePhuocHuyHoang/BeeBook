package com.example.sample.controller;

import com.example.sample.exception.AccessDeniedException;
import com.example.sample.exception.ResponseEntityErrorException;
import com.example.sample.payloads.ApiResponse;
import com.example.sample.service.PointTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = { "/pointTransaction" })
public class PointTransactionController {
	private PointTransactionService pointTransactionService;

	public PointTransactionController(PointTransactionService pointTransactionService) {
		this.pointTransactionService = pointTransactionService;
	}

	@ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }
	
	@GetMapping("/all")
    public ResponseEntity<?> getAllPointTransaction(
    		@RequestParam Long userId,
    		@RequestParam(required = false) Long offset,
    		@RequestParam(required = false) Long fetch,
    		HttpServletRequest request
            ) {		
			try {
				boolean isParamValid = false;
				if (request.getParameterValues("userId") != null && request.getParameterValues("userId").length > 1) {
				    isParamValid = true;
				} else if (request.getParameterValues("offset") != null && request.getParameterValues("offset").length > 1) {
				    isParamValid = true;
				} else if (request.getParameterValues("fetch") != null && request.getParameterValues("fetch").length > 1) {
				    isParamValid = true;
				}

				if (isParamValid) {
				    throw new AccessDeniedException("Only one param value on each attribute should be provided");
				}
				if (offset == null) {
		            offset =Long.valueOf(0) ;
		        }
		        if (fetch == null) {
		            fetch = Long.valueOf(5);
		        }	
				return pointTransactionService.getAllPointTransaction(userId, offset, fetch);
			} catch (AccessDeniedException e) {
				return new ResponseEntity<>(
						new ApiResponse(Boolean.FALSE, e.getMessage()),
						HttpStatus.BAD_REQUEST);
			}
		
		}
        
    
}
