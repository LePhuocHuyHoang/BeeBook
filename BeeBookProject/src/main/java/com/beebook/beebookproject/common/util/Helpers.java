package com.beebook.beebookproject.common.util;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Helpers {
    @Value("${secret.key}")
    public static String secretKey;

    public static String regexValidatorPassword(String password){
        if(password.length() < 8){
            return "Password must not be less than 8 characters";
        }

        String regexSpecial = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";
        if(!password.matches(regexSpecial)){
            return "Password has at least 1 special character";
        }
        String regexUpperCase = ".*[A-Z].*";
        if(!password.matches(regexUpperCase)){
            return "Password has at least 1 uppercase character";
        }
        String regexLowerCase = ".*[a-z].*";
        if(!password.matches(regexLowerCase)){
            return "Password has at least 1 lowercase character";
        }
        String regexDigit = ".*[0-9].*";
        if(!password.matches(regexDigit)){
            return "Password has at least 1 digit character";
        }
        return "";
    }

    public static String encrytePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password+secretKey);
    }


    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword+ secretKey, encodedPassword);
    }

    public static String getUserByJWT(String jwtTokenString) {
        try {
            // Parse the JWT token string
            JWT jwtToken = JWTParser.parse(jwtTokenString);

            // Extract the JWT claims
            JWTClaimsSet claims = jwtToken.getJWTClaimsSet();

            // Extract the value of "preferred_username" claim
            String preferredUsername = (String) claims.getClaim("preferred_username");
            System.out.print("User name: " + preferredUsername);
            return preferredUsername;
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null; // Return null in case of error
        }
    }
}
