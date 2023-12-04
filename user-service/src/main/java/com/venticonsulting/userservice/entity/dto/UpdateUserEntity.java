package com.venticonsulting.userservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserEntity {
    private long userId;
    private String name;
    private String lastname;
    private String phone;
    private String email;
}
