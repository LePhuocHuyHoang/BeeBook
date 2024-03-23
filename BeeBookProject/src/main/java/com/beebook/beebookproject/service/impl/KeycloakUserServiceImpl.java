package com.beebook.beebookproject.service.impl;

import com.beebook.beebookproject.dto.UserRegistrationRecord;
import com.beebook.beebookproject.entities.User;
import com.beebook.beebookproject.exception.AccessDeniedException;
import com.beebook.beebookproject.common.util.Helpers;
import com.beebook.beebookproject.payloads.ApiResponse;
import com.beebook.beebookproject.repositories.UserRepository;
import com.beebook.beebookproject.service.KeycloakUserService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";

    private String realm = "beebook";
    private Keycloak keycloak;
    private UserRepository userRepository;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public ResponseEntity<?> createUser(UserRegistrationRecord userRegistrationRecord) {
        try {
            //Check input nguoi dung co dung khong HTTP
            boolean isAccountExists = Optional.ofNullable(userRepository.findByUsername(userRegistrationRecord.username())).isPresent();
            if(isAccountExists){
                throw new AccessDeniedException("Username already Exists");
            }
            isAccountExists = Optional.ofNullable(userRepository.findByEmail(userRegistrationRecord.email())).isPresent();
            if(isAccountExists){
                throw new AccessDeniedException("Email already Exists");
            }
            String regexValidatorPassword = Helpers.regexValidatorPassword(userRegistrationRecord.password());

            if (!regexValidatorPassword.isEmpty()){
                throw new AccessDeniedException(regexValidatorPassword);
            }

            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(userRegistrationRecord.username());
            user.setEmail(userRegistrationRecord.email());
            user.setFirstName(userRegistrationRecord.firstName());
            user.setLastName(userRegistrationRecord.lastName());
            user.setEmailVerified(false);


            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setValue(userRegistrationRecord.password());
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

            List<CredentialRepresentation> list = new ArrayList<>();
            list.add(credentialRepresentation);
            user.setCredentials(list);

            UsersResource usersResource = getUsersResource();

//            assignRoleToUser(user.getId(),Role.USER);
            // Luu cho khac
            Response response = usersResource.create(user);

            List<UserRepresentation> representationList = usersResource.searchByUsername(user.getUsername(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation1 != null;
                emailVerification(userRepresentation1.getId());
            }

            //Luu vao local db
            User userEnity = new User();
            userEnity.setUsername(userRegistrationRecord.username());
            userEnity.setFirstName(userRegistrationRecord.firstName());
            userEnity.setLastName(userRegistrationRecord.lastName());
            userEnity.setPassword(Helpers.encrytePassword(userRegistrationRecord.password()));
            userEnity.setEmail(userRegistrationRecord.email());

            userRepository.save(userEnity);


            return new ResponseEntity<>(userRegistrationRecord, HttpStatus.OK);
        }catch (AccessDeniedException e){
            ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }


    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    @Override
    public UserRepresentation getUserById(String userId) {


        return getUsersResource().get(userId).toRepresentation();
    }

    @Override
    public ResponseEntity<?> deleteUserById(String userId) {
        getUsersResource().delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }


    @Override
    public void emailVerification(String userId) {

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    @Override
    public void forgotPassword(String username) {

        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> representationList = usersResource.searchByUsername(username, true);
        // Hoặc kiểm tra Email để tìm lại mật khẩu tự code ...
        UserRepresentation userRepresentation = representationList.stream().findFirst().orElse(null);

        if (userRepresentation != null) {
            UserResource userResource = usersResource.get(userRepresentation.getId());
            List<String> actions = new ArrayList<>();
            // Mail với action UPDATE_PASSWORD mở vào để check các hàm mail nếu cần tạo thêm
            actions.add(UPDATE_PASSWORD);
            userResource.executeActionsEmail(actions);
            return;
        }
        throw new RuntimeException("Username not found");
    }

    private RoleRepresentation getRole(String role) {
        RolesResource rolesResource = getRolesResource();
        return rolesResource.get(role).toRepresentation();
    }



    private void assignRoleToUser(String userId, String role) {
        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(userId);
        RoleRepresentation roleRepresentation = getRole(role);
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    private RolesResource getRolesResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.roles();
    }
}
