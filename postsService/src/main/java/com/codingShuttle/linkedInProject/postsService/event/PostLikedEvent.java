package com.codingShuttle.linkedInProject.postsService.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLikedEvent {

    private Long postId;
    private Long ownerUserId;
    private String ownerUserEmail;
    private Long likedByUserId;
    private String likedByUserEmail;
}
