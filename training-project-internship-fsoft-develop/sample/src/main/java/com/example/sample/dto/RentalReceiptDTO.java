package com.example.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalReceiptDTO {
	private Long bookId;
	private String bookName;
	private String rentalDate;
	private Long pointPrice;
}
