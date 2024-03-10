package com.example.sample.dto;

import com.example.sample.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String username;
    private String password;
    private Long point;
    private String gender;
    private List<Role> roles = new ArrayList<>();
}
