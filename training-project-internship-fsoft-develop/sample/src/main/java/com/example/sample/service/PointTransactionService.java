package com.example.sample.service;

import org.springframework.http.ResponseEntity;

public interface PointTransactionService {
	ResponseEntity<?> getAllPointTransaction(Long userId, Long offset, Long fetch);
}
