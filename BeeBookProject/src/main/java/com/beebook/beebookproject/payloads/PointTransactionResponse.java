package com.beebook.beebookproject.payloads;

import com.beebook.beebookproject.entities.PointTransaction;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PointTransactionResponse {
	 private List<PointTransaction> content;
	    private Integer pageNumber;
	    private Integer pageSize;
	    private Long totalElements;
	    private Integer totalPages;
	    private boolean lastPage;
}
