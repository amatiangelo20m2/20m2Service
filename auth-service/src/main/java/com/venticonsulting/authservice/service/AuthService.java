package com.venticonsulting.authservice.service;

import com.venticonsulting.authservice.entity.*;
import com.venticonsulting.authservice.entity.dto.*;
import com.venticonsulting.authservice.exception.customexceptions.BadCredentialsException;
import com.venticonsulting.authservice.exception.customexceptions.UserAlreadyExistException;
import com.venticonsulting.authservice.exception.customexceptions.UserNotFoundException;
import com.venticonsulting.authservice.repository.AuthRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    public AuthResponseEntity signIn(Credentials credentials) {

        log.info("Sign in user with email : " + credentials.getEmail());

        UserEntity userEntity
                = authRepository.findByEmail(credentials.getEmail()).orElseThrow(()
                -> new UserNotFoundException("Utente non trovato con la seguente mail [" + credentials.getEmail() + "]"));

            if (passwordEncoder.matches(credentials.getPassword(), userEntity.getPassword())) {
                return AuthResponseEntity.builder()
                        .user(UserResponseEntity
                                .builder()
                                .email(userEntity.getEmail())
                                .name(userEntity.getName())
                                .phone(userEntity.getPhone())
                                .avatar(userEntity.getAvatar())
                                .status(userEntity.getProfileStatus())
                                .userCode(userEntity.getUserCode())
                                .build())
                        .accessToken(jwtService.generateToken(credentials.getEmail(), userEntity.getUserCode()))
                        .build();

            }else{
                log.error("Password errata per mail [" + credentials.getEmail() + "]");
                throw new BadCredentialsException("Password errata per mail [" + credentials.getEmail() + "]");
            }
    }

    @Transactional
    public AuthResponseEntity signUp(Credentials credentials) {

        log.info("Save user: " + credentials);

        if(authRepository.findByEmail(credentials.getEmail()).isPresent()){
            log.error("Exception saving user. Mail " + credentials.getEmail() + " is already been used.");
            throw new UserAlreadyExistException("Mail " + credentials.getEmail() + " is already been used");
        }
        UserEntity userEntityBuild = UserEntity
                .builder()
                .name(credentials.getName())
                .phone(credentials.getPhone())
                .password(passwordEncoder.encode(credentials.getPassword()))
                .email(credentials.getEmail())
                .fmcToken(credentials.getFmcToken())
                .profileStatus(ProfileStatus.ONLINE)
                .signInMethod(SignInMethod.PASSWORD)
                .avatar(credentials.getAvatar())
                .id(0)
                .build();

        UserEntity userEntity = authRepository.save(userEntityBuild);

        return AuthResponseEntity.builder()
                .user(UserResponseEntity
                        .builder()
                        .email(userEntity.getEmail())
                        .name(userEntity.getName())
                        .phone(userEntity.getPhone())
                        .avatar(userEntity.getAvatar())
                        .userCode(userEntity.getUserCode())
                        .status(userEntity.getProfileStatus())
                        .build())

                .accessToken(jwtService.generateToken(credentials.getEmail(), userEntity.getUserCode()))
                .build();
    }

    @Transactional
    public AuthResponseEntity signInWithUserCode(UserCodeCredential userCodeCredential) {
        log.info("Sign in user by user code: " + userCodeCredential.getUserCode());

        UserEntity userEntity = authRepository.findByUserCode(userCodeCredential.getUserCode())
                .orElseThrow(() -> new UserNotFoundException("User not found for code " + userCodeCredential.getUserCode()));

        if (passwordEncoder.matches(userCodeCredential.getPassword(), userEntity.getPassword())) {
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(userEntity.getEmail())
                            .name(userEntity.getName())
                            .phone(userEntity.getPhone())
                            .avatar(userEntity.getAvatar())
                            .status(userEntity.getProfileStatus())
                            .userCode(userEntity.getUserCode())
                            .build())
                    .accessToken(jwtService.generateToken(userEntity.getEmail(), userEntity.getUserCode()))
                    .build();

        }else{
            log.error("Password errata per utente con codice [" + userCodeCredential.getUserCode() + "]");
            throw new BadCredentialsException("Password errata per utente con codice [" + userCodeCredential.getUserCode() + "]");
        }
    }

    @Transactional
    public AuthResponseEntity signInWithGoogle(Credentials credentials) {
        log.info("Sign in user by google: " + credentials.getEmail());
        Optional<UserEntity> userByEmail = authRepository.findByEmail(credentials.getEmail());
        if(userByEmail.isPresent()) {
            log.info("Refresh token for user: {}. New Token [{}]", userByEmail.get(), credentials.getFmcToken());
            userByEmail.get().setFmcToken(credentials.getFmcToken());

            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(userByEmail.get().getEmail())
                            .name(userByEmail.get().getName())
                            .phone(userByEmail.get().getPhone())
                            .avatar(userByEmail.get().getAvatar())
                            .userCode(userByEmail.get().getUserCode())
                            .status(userByEmail.get().getProfileStatus())
                            .fcmToken(credentials.getFmcToken())
                            .build())
                    .accessToken(jwtService.generateToken(credentials.getEmail(), userByEmail.get().getUserCode()))
                    .build();
        }else{
            UserEntity userEntityBuild = UserEntity
                    .builder()
                    .name(credentials.getName())
                    .phone(credentials.getPhone())
                    .password(passwordEncoder.encode(credentials.getPassword()))
                    .email(credentials.getEmail())
                    .profileStatus(ProfileStatus.ONLINE)
                    .signInMethod(SignInMethod.GOOGLE)
                    .avatar(credentials.getAvatar())
                    .fmcToken(credentials.getFmcToken())
                    .id(0)
                    .build();

            UserEntity userEntity = authRepository.save(userEntityBuild);
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(userEntity.getEmail())
                            .name(userEntity.getName())
                            .phone(userEntity.getPhone())
                            .avatar(userEntity.getAvatar())
                            .userCode(userEntity.getUserCode())
                            .status(userEntity.getProfileStatus())
                            .fcmToken(userEntity.getFmcToken())
                            .build())

                    .accessToken(jwtService.generateToken(credentials.getEmail(),
                            userEntity.getUserCode()))
                    .build();
        }





    }

    @Transactional
    public void updateUser(UpdateUserEntity updateUserEntity) {
        log.info("Update user with id {} : {}", updateUserEntity.getUserId(), updateUserEntity);

        Optional<UserEntity> existingUserOpt = authRepository.findById(updateUserEntity.getUserId());

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = getUserEntity(updateUserEntity, existingUserOpt);
            authRepository.save(existingUser);
        } else {
            throw new UserNotFoundException("User not found with the following id: " + updateUserEntity.getUserId());
        }
    }

    private static UserEntity getUserEntity(UpdateUserEntity updateUserEntity,
                                            Optional<UserEntity> existingUserOpt) {
        UserEntity existingUser = existingUserOpt.get();

        if (updateUserEntity.getName() != null) {
            existingUser.setName(updateUserEntity.getName());
        }

        if (updateUserEntity.getPhone() != null) {
            existingUser.setPhone(updateUserEntity.getPhone());
        }

        if (updateUserEntity.getEmail() != null) {
            existingUser.setEmail(updateUserEntity.getEmail());
        }
        return existingUser;
    }

    public UserResponseEntity retrieveUserByEmail(String email) {
        log.info("Retrieve user by id : {}", email);

        Optional<UserEntity> userOpt = authRepository.findByEmail(email);
        if(userOpt.isPresent()){
            return UserResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .status(userOpt.get().getProfileStatus())
                    .avatar(userOpt.get().getAvatar())
                    .userCode(userOpt.get().getUserCode())
                    .build();
        }else{
            log.error("User not found with the following email [{}] ", email);
            throw new UserNotFoundException("User not found with the following email: " + email);
        }
    }

    public UserResponseEntity retrieveUserByUserCode(String userCode) {
        log.info("Retrieve user by code : {}", userCode);

        Optional<UserEntity> userOpt = authRepository.findByUserCode(userCode);
        if(userOpt.isPresent()){
            return UserResponseEntity
                    .builder()
                    .email(userOpt.get().getEmail())
                    .name(userOpt.get().getName())
                    .phone(userOpt.get().getPhone())
                    .status(userOpt.get().getProfileStatus())
                    .avatar(userOpt.get().getAvatar())
                    .userCode(userOpt.get().getUserCode())
                    .build();
        }else{
            log.error("User not found with the following code [{}] ", userCode);
            throw new UserNotFoundException("User not found with the following userCode: " + userCode);
        }
    }
    public void deleteUserByEmail(String email) {
        log.info("Delete user by email : {}", email);
        if(authRepository.findByEmail(email).isPresent()){
            authRepository.deleteByEmail(email);
        }else{
            log.error("User not found with the following email [{}] ", email);
            throw new UserNotFoundException("User not found with the following email: " + email);
        }
    }




    public AuthResponseEntity signInWithAccessToken(JwtEntity accessToken) {
        log.info("Token: " + accessToken.getAccessToken());

        Optional<UserEntity> existingUserOpt = authRepository.findByEmail(jwtService.extractUsername(accessToken.getAccessToken()));

        if(existingUserOpt.isPresent()){
            return AuthResponseEntity.builder()
                    .user(UserResponseEntity
                            .builder()
                            .email(existingUserOpt.get().getEmail())
                            .name(existingUserOpt.get().getName())
                            .phone(existingUserOpt.get().getPhone())
                            .avatar(existingUserOpt.get().getAvatar())
                            .status(existingUserOpt.get().getProfileStatus())
                            .userCode(existingUserOpt.get().getUserCode())
                            .build())
                    .accessToken(jwtService.generateToken(existingUserOpt.get().getEmail(), existingUserOpt.get().getUserCode()))
                    .build();
        }else{
            log.error("Utente non trovato con la seguente mail [" + jwtService.extractUsername(accessToken.getAccessToken()) + "] dopo autenticatione con jwt");
            throw new UserNotFoundException("Utente non trovato con la seguente mail [" + jwtService.extractUsername(accessToken.getAccessToken()) + "] dopo autenticatione con jwt");
        }

    }

    public String retrieveFcmTokenByUserCode(String email) {
        log.info("Retrieve FCM Token by user with email: " + email);

        UserEntity userEntity = authRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException("Utente non trovato con la seguente mail [" + email + "]"));

        return userEntity.getFmcToken();
    }

    @Transactional
    @Modifying
    public void resetPassword(String userCode, String password) {

        log.info("Retrieve password for user with code: "
                + userCode);

        UserEntity userEntity = authRepository.findByUserCode(userCode).orElseThrow(()
                -> new UserNotFoundException("Utente non trovato con il seguente codice [" + userCode + "]"));

        userEntity.setPassword(passwordEncoder.encode(password));
    }




}
