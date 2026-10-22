package com.codingShuttle.linkedInProject.postsService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {

    private Long id;
    private Long userId;
    private String firstName;
    private String email;
}
