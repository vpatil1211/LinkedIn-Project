package com.codingShuttle.linkedInProject.notificationService.consumer;

import com.codingShuttle.linkedInProject.notificationService.entity.NotificationEntity;
import com.codingShuttle.linkedInProject.postsService.event.PostCreatedEvent;
import com.codingShuttle.linkedInProject.postsService.event.PostLikedEvent;
import com.codingShuttle.linkedInProject.notificationService.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostConsumer {

    private final NotificationService notificationService;

    public PostConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "post_created_topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent) {
        log.info("notification received: {}", postCreatedEvent);
        String message = String.format("Your connection with id %d has created this post %s", postCreatedEvent.getOwnerUserId(), postCreatedEvent.getContent());
        String subject = "Your Connection just posted a post!";
        NotificationEntity notifications = NotificationEntity.builder()
                .message(message)
                .userId(postCreatedEvent.getUserId())
                .build();
        // notificationService.postCreatedNotification(notifications);
        notificationService.postCreatedNotification(postCreatedEvent.getUserEmail(), message, subject, notifications);
    }

    @KafkaListener(topics = "post_liked_topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent) {
        log.info("notification received: {}", postLikedEvent);
        String message = String.format("Your connection with id %d has liked your post with post id %d", postLikedEvent.getLikedByUserId(), postLikedEvent.getPostId());
        String subject = "Your connection just liked your post!";
        NotificationEntity notifications = NotificationEntity.builder()
                .message(message)
                .userId(postLikedEvent.getOwnerUserId())
                .build();
        notificationService.postLikedNotification(postLikedEvent.getOwnerUserEmail(), message, subject, notifications);
    }
}
