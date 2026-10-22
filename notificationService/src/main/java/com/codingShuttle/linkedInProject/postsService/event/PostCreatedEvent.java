package com.codingShuttle.linkedInProject.postsService.event;

import lombok.Data;

@Data
public class PostCreatedEvent {

    private Long ownerUserId;
    private String ownerUserEmail;
    private Long postId;
    private Long userId;
    private String userEmail;
    private String content;

}
