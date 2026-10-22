package com.codingShuttle.linkedInProject.userService.event;

import lombok.Builder;
import lombok.Data;

@Data
public class UserCreatedEvent {

    private Long userId;
    private String firstName;
    private String email;
}
