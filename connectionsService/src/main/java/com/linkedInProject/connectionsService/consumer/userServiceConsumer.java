package com.linkedInProject.connectionsService.consumer;

import com.linkedInProject.connectionsService.service.PersonService;
import com.codingShuttle.linkedInProject.userService.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class userServiceConsumer {
    private final PersonService personService;

    public userServiceConsumer(PersonService personService) {
        this.personService = personService;
    }

    @KafkaListener(topics = "user_created_topic")
    public void handleUserCreated(UserCreatedEvent userCreatedEvent) {
        log.info("Creating user with: {}", userCreatedEvent);
       personService.createPerson(userCreatedEvent.getUserId(), userCreatedEvent.getFirstName(), userCreatedEvent.getEmail());
    }
}
