package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.dto.PointTransactionDTO;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.dto.PointTransactionDTO;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.repositories.PointTransactionRepository;
import com.beebook.beebookproject.service.PointTransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PointTransactionServiceImpl implements PointTransactionService{

	private static final String POINT_TRANSACTION_STR = "Point Transaction";
	private PointTransactionRepository pointTransactionRepository;
	
	@Autowired
	private ModelMapper modelMapper;


	public PointTransactionServiceImpl(PointTransactionRepository pointTransactionRepository, ModelMapper modelMapper) {
		this.pointTransactionRepository = pointTransactionRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public ResponseEntity<?> getAllPointTransaction(String userName, Long offset, Long fetch) {
		List<Object[]> checkExistsUser = pointTransactionRepository.checkExistingUser(userName);
		if(checkExistsUser.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "Invalid user id value"), HttpStatus.NOT_FOUND);
		}
		if(offset < 0 || fetch < 0) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(Boolean.FALSE, "The value of the parameter cannot be negative"), HttpStatus.BAD_REQUEST);
		}
		Long userId = (long) checkExistsUser.getFirst()[0];
		List<Object[]> objects = pointTransactionRepository.getAllPointTransaction(userId, offset, fetch);
		List<PointTransactionDTO> pointTransactionDTOs = new ArrayList<PointTransactionDTO>();
		for(Object[] obj: objects) {

//			Long idTransaction = (Long) obj[0];
			Date date = (Date) obj[0];
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String transactionDate = formatter.format(date);
			Long pointsAdded = (Long) obj[1];
			String typeName = (String) obj[2];
			pointTransactionDTOs.add(new PointTransactionDTO(transactionDate, pointsAdded, typeName));
		}
		return new ResponseEntity<List<PointTransactionDTO>>(pointTransactionDTOs, HttpStatus.OK);
	}


}
