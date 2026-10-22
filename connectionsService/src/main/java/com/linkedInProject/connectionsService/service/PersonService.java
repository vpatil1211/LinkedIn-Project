package com.linkedInProject.connectionsService.service;

import com.linkedInProject.connectionsService.entiity.Person;
import com.linkedInProject.connectionsService.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void createPerson(Long userId, String firstName, String userEmail) {
        log.info("Creating new person with userId %d and name %s",  userId, firstName);
        Person person = Person.builder()
                .userId(userId)
                .firstName(firstName)
                .email(userEmail)
                .build();
        personRepository.save(person);
    }
}
