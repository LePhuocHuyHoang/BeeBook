package com.beebook.beebookproject.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Data
public class UserDto {
    private String id;
    private LocalDate dob;
    private String countryIso2;
    private String type;
    private String storageId;
//    private StorageProvider storageProvider;
    private String language;
    private String gender;
}
