package com.beebook.beebookproject.service;

import com.beebook.beebookproject.dto.UserRegistrationRecord;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;

public interface KeycloakUserService {

    ResponseEntity<?> createUser(UserRegistrationRecord userRegistrationRecord);
    UserRepresentation getUserById(String userId);
    ResponseEntity<?> deleteUserById(String userId);
    void emailVerification(String userId);
    void forgotPassword(String username);
}
