package com.codingShuttle.linkedInProject.userService.event;

import lombok.Builder;
import lombok.Data;

@Data
public class ConnectionEvent {

    private Long senderUserId;
    private Long receiverUserId;
    private String senderEmail;
    private String receiverEmail;
}
