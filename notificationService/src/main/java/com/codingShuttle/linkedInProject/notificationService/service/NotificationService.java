package com.codingShuttle.linkedInProject.notificationService.service;

import com.codingShuttle.linkedInProject.notificationService.entity.NotificationEntity;
import com.codingShuttle.linkedInProject.notificationService.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationsRepository;
    private final JavaMailSender javaMailSender;

    @Value("{spring.mail.username}")
    private String senderEmail;

    public NotificationService(NotificationRepository notificationsRepository, JavaMailSender javaMailSender) {
        this.notificationsRepository = notificationsRepository;
        this.javaMailSender = javaMailSender;
    }

    public void postCreatedNotification(String recipient, String message, String subject, NotificationEntity notification) {
        notificationsRepository.save(notification);
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipient);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
    }

    public void sendConnectionRequestNotification(String recipient, String message, String subject, NotificationEntity notification) {
        notificationsRepository.save(notification);
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipient);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
    }

    public void sendConnectionAcceptNotification(String recipient, String message, String subject, NotificationEntity notification) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipient);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
    }

    public void postLikedNotification(String recipient, String message, String subject, NotificationEntity notification) {
        notificationsRepository.save(notification);
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipient);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);
    }
}
