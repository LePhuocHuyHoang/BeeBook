package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.payloads.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class UserApi {

    @Autowired
    private final Keycloak keycloak;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            AccessTokenResponse accessTokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.ok(accessTokenResponse.getToken());
        } catch (Exception e) {
            // Log the exception for troubleshooting
            e.printStackTrace();
            return ResponseEntity.status(401).body("Failed to obtain access token: " + e.getMessage());
        }
    }
}
