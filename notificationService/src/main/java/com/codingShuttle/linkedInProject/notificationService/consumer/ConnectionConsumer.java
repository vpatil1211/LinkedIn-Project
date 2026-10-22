package com.codingShuttle.linkedInProject.notificationService.consumer;

import com.codingShuttle.linkedInProject.notificationService.entity.NotificationEntity;
import com.codingShuttle.linkedInProject.notificationService.service.NotificationService;
import com.codingShuttle.linkedInProject.userService.event.ConnectionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConnectionConsumer {

    private final NotificationService notificationService;

    public ConnectionConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "connection_request_topic")
    public void sendConnectionRequest(ConnectionEvent connectionEvent) {
        log.info("connection request send for: " + connectionEvent);
        String message = String.format("Someone send you request with %d id", connectionEvent.getSenderUserId());
        String subject = "Someone just send you a request!";
        NotificationEntity notification = NotificationEntity.builder()
                .message(message)
                .userId(connectionEvent.getReceiverUserId())
                .build();
        notificationService.sendConnectionRequestNotification(connectionEvent.getReceiverEmail(), message, subject, notification);
    }

    @KafkaListener(topics = "connection_accept_topic")
    public void sendConnectionAccept(ConnectionEvent connectionEvent) {
        log.info("connection accepting for: " + connectionEvent);
        String message = String.format("Accepted your request with %d id:", connectionEvent.getReceiverUserId());
        String subject = String.format("Your Request just accepted by %d", connectionEvent.getReceiverUserId());
        NotificationEntity notification = NotificationEntity.builder()
                .message(message)
                .userId(connectionEvent.getSenderUserId())
                .build();
        notificationService.sendConnectionAcceptNotification(connectionEvent.getSenderEmail(), message, subject, notification);
    }
}
