package com.beebook.beebookproject.service;

import org.springframework.web.multipart.MultipartFile;

public interface BunnyNetService {


    //https://dash.bunny.net/welcome
    String uploadProfilePicture(MultipartFile file, String fileName, String requestKey) throws Exception;
}
