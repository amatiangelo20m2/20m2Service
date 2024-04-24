package com.ventimetriconsulting.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class NotificationEntity {

    private String title;
    private String message;
    private List<String> fmcToken;
    private RedirectPage redirectPage;


}
