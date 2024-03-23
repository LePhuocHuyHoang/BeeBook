package com.beebook.beebookproject.dto;

import com.beebook.beebookproject.entities.Gender;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationDto {

    private Long id;

    private String firstName;

    private String lastName;

    private Date dob;

    private String username;

    private String password;

    private Long point;

    private Gender gender;

    private String avatar;

    private MultipartFile[] image;
}
