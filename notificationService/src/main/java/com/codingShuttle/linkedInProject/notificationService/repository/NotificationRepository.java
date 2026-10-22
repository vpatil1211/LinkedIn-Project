package com.codingShuttle.linkedInProject.notificationService.repository;

import com.codingShuttle.linkedInProject.notificationService.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

}
