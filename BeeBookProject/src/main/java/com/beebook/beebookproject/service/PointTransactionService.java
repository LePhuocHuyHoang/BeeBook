package com.beebook.beebookproject.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionService {
	ResponseEntity<?> getAllPointTransaction(String userName, Long offset, Long fetch);
}
