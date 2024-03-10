package com.example.sample.payloads;

import com.example.sample.dto.RentalReceiptDTO;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RentalReceiptResponse {

    private List<RentalReceiptDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
