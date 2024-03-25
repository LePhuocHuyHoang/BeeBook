package com.beebook.beebookproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointTransactionDTO {
//	private Long idTransaction;
	private String transactionDate;
	private Long pointsAdded;
	private String typeName;
}
