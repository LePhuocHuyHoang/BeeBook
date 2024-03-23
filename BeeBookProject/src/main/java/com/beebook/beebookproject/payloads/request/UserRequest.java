package com.beebook.beebookproject.payloads.request;

import com.beebook.beebookproject.entities.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
@Getter
@Setter
public class UserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String username;
    private String password;
    private Long point;
    private String gender;
    private Set<Role> roles = new HashSet<>();
}
