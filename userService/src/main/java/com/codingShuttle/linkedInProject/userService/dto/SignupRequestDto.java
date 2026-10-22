package com.codingShuttle.linkedInProject.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String firstName;
    private String familyName;
    private String email;
    private String password;
}
