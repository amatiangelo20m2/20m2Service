package com.ventimetriconsulting.user;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@ToString
public class UserResponseEntity {

    private String name;
    private String phone;
    private String email;
    private String avatar;
    private ProfileStatus status;
    private String userCode;
    private String fcmToken;

    public enum ProfileStatus {
        ONLINE, AWAY, BUSY, INVISIBLE
    }
}
