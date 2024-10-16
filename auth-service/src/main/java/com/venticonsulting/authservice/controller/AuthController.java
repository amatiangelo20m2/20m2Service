package com.venticonsulting.authservice.controller;

import com.venticonsulting.authservice.entity.JwtEntity;
import com.venticonsulting.authservice.entity.UserCodeCredential;
import com.venticonsulting.authservice.entity.dto.*;
import com.venticonsulting.authservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService userService;

    @PostMapping(path = "/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseEntity signUp(@RequestBody Credentials credentials) {
        return userService.signUp(credentials);

    }

    @PostMapping(path = "/google/sign-in")
    public AuthResponseEntity signInWithGoogle(@RequestBody Credentials credentials) {
        return userService.signInWithGoogle(credentials);
    }

    @GetMapping(path = "/retrieve")
    public UserResponseEntity retrieveUserByEmail(@RequestParam String email){
        return userService.retrieveUserByEmail(email);
    }

    @GetMapping(path = "/retrievebyusercode")
    public UserResponseEntity retrieveUserByUserCode(@RequestParam(name="userCode") String userCode){
        return userService.retrieveUserByUserCode(userCode);
    }

    @PostMapping(path = "/sign-in")
    public AuthResponseEntity signIn(@RequestBody Credentials credentials){
        return userService.signIn(credentials);
    }

    @PostMapping(path = "/sign-in-with-user-code")
    public ResponseEntity<AuthResponseEntity> signInWithUserCode(@RequestBody UserCodeCredential userCodeCredential){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.signInWithUserCode(userCodeCredential));
    }

    @PostMapping(path = "/sign-in-with-token")
    public AuthResponseEntity signInWithToken(@RequestBody JwtEntity jwtEntity){
        return userService.signInWithAccessToken(jwtEntity);
    }

    @DeleteMapping(path = "/delete")
    public void deleteUserByEmail(@RequestParam String email){
        userService.deleteUserByEmail(email);
    }

    @PutMapping(path = "/update")
    public void updateUser(@RequestBody UpdateUserEntity userEntity){
        userService.updateUser(userEntity);
    }

    @GetMapping(path = "/getfcmtoken")
    public ResponseEntity<String> getFcmTokenByUserCode(@RequestParam String userCode){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.retrieveFcmTokenByUserCode(userCode));
    }

    @PutMapping(path = "/reset/password")
    public ResponseEntity<Void> resetPassword(@RequestParam String userCode,
                                               @RequestParam String password){

        userService.resetPassword(userCode, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
