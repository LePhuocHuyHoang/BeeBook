package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.exception.ResponseEntityErrorException;
import com.beebook.beebookproject.service.PointTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

	@GetMapping("/getAllPointTransaction")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<?> getAllPointTransaction(
			@RequestParam(required = false) Long page,
			@RequestParam(required = false) Long limit,
			HttpServletRequest request
	) {
		try {
			boolean isParamValid = false;
			if (request.getParameterValues("page") != null && request.getParameterValues("page").length > 1) {
				isParamValid = true;
			} else if (request.getParameterValues("limit") != null && request.getParameterValues("limit").length > 1) {
				isParamValid = true;
			}

			if (isParamValid) {
				throw new AccessDeniedException("Only one param value on each attribute should be provided");
			}
			if (page == null) {
				page =Long.valueOf(1) ;
			}
			if (limit == null) {
				limit = Long.valueOf(5);
			}
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
			String jwt = jwtToken.getTokenValue();
			String username = Helpers.getUserByJWT(jwt);
			Long offset = (page - 1 ) *limit;
			return pointTransactionService.getAllPointTransaction(username, offset, limit);
		} catch (AccessDeniedException e) {
			return new ResponseEntity<>(
					new ApiResponse(Boolean.FALSE, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}

	}
        
    
}
