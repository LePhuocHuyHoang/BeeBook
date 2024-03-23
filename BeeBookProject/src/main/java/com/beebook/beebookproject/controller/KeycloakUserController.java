package com.beebook.beebookproject.controller;


import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.dto.UserRegistrationRecord;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.service.KeycloakUserService;
import com.beebook.beebookproject.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class KeycloakUserController {

    private final KeycloakUserService keycloakUserService;

    private final UserService userService;

    @GetMapping("/")
    public String hello(){


        return "Hello";
    }

//    @PostMapping
//    public UserRegistrationRecord createUser(@RequestBody UserRegistrationDto createUser) {
//        //Lưu vào CSDL
//        userService.save(createUser);
//        UserRegistrationRecord userRegistrationRecord = new UserRegistrationRecord(createUser.getUsername(),
//                createUser.getUsername(),createUser.getFirstName(),createUser.getLastName(),createUser.getPassword(),false);
//        return keycloakUserService.createUser(userRegistrationRecord);
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakUserService.createUser(userRegistrationRecord);
    }

//    @GetMapping("/{userId}")
//    @PreAuthorize("hasAuthority('USER')")
//    public UserRepresentation getUserById(@PathVariable String userId) {
//
//        return keycloakUserService.getUserById(userId);
//    }

    @DeleteMapping("/{userName}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteUserById(@PathVariable String userName, Authentication authentication) {
        Jwt jwtToken = ((JwtAuthenticationToken) authentication).getToken();
        String jwt = jwtToken.getTokenValue();
        String userNameOfToken = Helpers.getUserByJWT(jwt);

        if(!userName.equals(userNameOfToken)){
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Incorrect account"), HttpStatus.BAD_REQUEST);
        }

        userService.deleteUserByUserName(userName);

        String userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
//                org.springframework.security.oauth2.jwt.Jwt jwtToken = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
                userId = jwtToken.getClaimAsString("sub");
            }
        }
        return keycloakUserService.deleteUserById(userId);
    }



    @PutMapping("/{userId}/send-verify-email")
    @PreAuthorize("hasAuthority('USER')")
    public void sendVerificationEmail(@PathVariable String userId) {
        //Sendmail

        keycloakUserService.emailVerification(userId);
    }

}
