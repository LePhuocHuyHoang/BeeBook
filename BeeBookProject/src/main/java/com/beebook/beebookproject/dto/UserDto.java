package com.beebook.beebookproject.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Data
public class UserDto {
    //    private String id;
    private String userName;
    private String firstName;
    private String lastName;
    private Date dob;
    private Long point;
    //    private String countryIso2;
//    private String type;
//    private String storageId;
//    private StorageProvider storageProvider;
//    private String language;
    private String gender;
    private String email;
}
