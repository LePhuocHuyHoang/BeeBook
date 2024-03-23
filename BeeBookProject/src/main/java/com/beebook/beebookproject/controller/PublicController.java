package com.beebook.beebookproject.controller;

import com.beebook.beebookproject.service.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class PublicController {

    private final KeycloakUserService keycloakUserService;


    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @PutMapping("/{username}/forgot-password")
    public void forgotPassword(@PathVariable String username) {
        //check trùng csdl

        keycloakUserService.forgotPassword(username);

    }
}
