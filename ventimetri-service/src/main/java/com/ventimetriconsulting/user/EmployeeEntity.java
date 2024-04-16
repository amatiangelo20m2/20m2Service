package com.ventimetriconsulting.user;

import com.ventimetriconsulting.branch.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class EmployeeEntity {

    private String name;
    private String phone;
    private String email;
    private String avatar;
    private UserResponseEntity.ProfileStatus status;
    private String userCode;
    //TODO : fcm token here is useless - remove
    private String fcmToken;
    private String branchCode;
    private Role role;
    private boolean authorized;
}
