package com.codingShuttle.linkedInProject.postsService.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreatedEvent {

    private Long ownerUserId;
    private String ownerUserEmail;
    private Long postId;
    private Long userId;
    private String userEmail;
    private String content;

}
