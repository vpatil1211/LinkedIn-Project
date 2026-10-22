package com.codingShuttle.linkedInProject.postsService.event;

import lombok.Data;

@Data
public class PostLikedEvent {

    private Long postId;
    private Long ownerUserId;
    private String ownerUserEmail;
    private Long likedByUserId;
    private String likedByUserEmail;
}
