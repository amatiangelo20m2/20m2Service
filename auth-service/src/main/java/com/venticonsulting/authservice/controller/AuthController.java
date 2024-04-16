package com.venticonsulting.authservice.controller;

import com.venticonsulting.authservice.entity.JwtEntity;
import com.venticonsulting.authservice.entity.dto.*;
import com.venticonsulting.authservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
//@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AuthController {

    private AuthService userService;

    @PostMapping(path = "/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseEntity save(@RequestBody Credentials credentials) {
        return userService.signUp(credentials);
    }

    @PostMapping(path = "/google/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseEntity signInWithGoogle(@RequestBody Credentials credentials) {
        return userService.signInWithGoogle(credentials);
    }

    @GetMapping(path = "/retrieve")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseEntity retrieveUserByEmail(@RequestParam String email){
        return userService.retrieveUserByEmail(email);
    }

    @GetMapping(path = "/retrievebyusercode")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseEntity retrieveUserByUserCode(@RequestParam(name="userCode") String userCode){
        return userService.retrieveUserByUserCode(userCode);
    }

    @PostMapping(path = "/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseEntity signIn(@RequestBody Credentials credentials){
        return userService.signIn(credentials);
    }

    @PostMapping(path = "/sign-in-with-token")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseEntity signInWithToken(@RequestBody JwtEntity jwtEntity){
        return userService.signInWithAccessToken(jwtEntity);
    }

    @DeleteMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserByEmail(@RequestParam String email){
        userService.deleteUserByEmail(email);
    }

    @PutMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody UpdateUserEntity userEntity){
        userService.updateUser(userEntity);
    }

    @GetMapping(path = "/getfcmtoken")
    public ResponseEntity<String> getFcmTokenByUserCode(@RequestParam String userCode){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.retrieveFcmTokenByUserCode(userCode));
    }


}
