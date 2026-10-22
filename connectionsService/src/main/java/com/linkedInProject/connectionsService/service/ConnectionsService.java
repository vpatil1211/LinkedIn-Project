package com.linkedInProject.connectionsService.service;

import com.codingShuttle.linkedInProject.userService.event.ConnectionEvent;
import com.linkedInProject.connectionsService.Exception.BadRequestException;
import com.linkedInProject.connectionsService.auth.AuthContextHolder;
import com.linkedInProject.connectionsService.client.UserServiceClient;
import com.linkedInProject.connectionsService.entiity.Person;
import com.linkedInProject.connectionsService.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ConnectionsService {

    private final PersonRepository personRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<Long, ConnectionEvent> connectionEventKafkaTemplate;

    public ConnectionsService(PersonRepository personRepository, UserServiceClient userServiceClient, KafkaTemplate<Long, ConnectionEvent> connectionEventKafkaTemplate) {
        this.personRepository = personRepository;
        this.userServiceClient = userServiceClient;
        this.connectionEventKafkaTemplate = connectionEventKafkaTemplate;
    }

    public List<Person> getFirstDegreeConnection(Long userId) {
        log.info("Getting first degree connection of user id: {}", userId);
        List<Person> personList = personRepository.getFirstDegreeConnections(userId);
        return personList;
    }

    public void sendConnectionRequest(Long receiverUserId) {
        Long senderUserId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("Sending connection with senderId: {} to receiverId: {}", senderUserId, receiverUserId);
        if(senderUserId.equals(receiverUserId)) {
           throw new BadRequestException("Both sender and receiver are same");
        }
        boolean isConnectionRequestSentAlready = personRepository.connectionRequestExists(senderUserId, receiverUserId);
        if(isConnectionRequestSentAlready) {
            throw new BadRequestException("You have already sent a connection request to this id: {}" + receiverUserId);
        }

        boolean isAlreadyConnected = personRepository.isAlreadyConnected(senderUserId, receiverUserId);
        if(isAlreadyConnected) {
            throw new BadRequestException("You have already connection with {} " + receiverUserId);
        }
        personRepository.addConnection(senderUserId, receiverUserId);
        log.info("Successfully send connection request.");

        String senderEmail = userServiceClient.getEmailOfUser(senderUserId);
        String receiverEmail = this.getEmailOfUser(receiverUserId);

        ConnectionEvent connectionEvent = ConnectionEvent.builder()
                .senderUserId(senderUserId)
                .receiverUserId(receiverUserId)
                .senderEmail(senderEmail)
                .receiverEmail(receiverEmail)
                .build();
        connectionEventKafkaTemplate.send("connection_request_topic", connectionEvent);
    }

    public void acceptConnectionRequest(Long senderUserId) {
        Long receiverUserId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("Accepting request with receiverUserId: {} for senderUserId: {}", receiverUserId, senderUserId);
        if(senderUserId.equals(receiverUserId)) {
            throw new BadRequestException("Both sender and receiver are same");
        }

        boolean isAlreadyConnected = personRepository.isAlreadyConnected(senderUserId, receiverUserId);
        if(isAlreadyConnected) {
            throw new BadRequestException("You have already connection with {} " + senderUserId + ". Cannot accept the request again.");
        }

        boolean isConnectionRequestSentAlready = personRepository.connectionRequestExists(senderUserId, receiverUserId);
        if(!isConnectionRequestSentAlready) {
            throw new BadRequestException("No Connection request exists to this id: {}" + senderUserId);
        }
        personRepository.acceptConnectionRequest(senderUserId, receiverUserId);
        log.info("Successfully accept request with {}", senderUserId);

        String receiverEmail = userServiceClient.getEmailOfUser(receiverUserId);
        String senderEmail = this.getEmailOfUser(senderUserId);

        ConnectionEvent connectionEvent = ConnectionEvent.builder()
                .senderUserId(senderUserId)
                .receiverUserId(receiverUserId)
                .receiverEmail(receiverEmail)
                .senderEmail(senderEmail)
                .build();
        connectionEventKafkaTemplate.send("connection_accept_topic", connectionEvent);
    }

    public void rejectConnectionRequest(Long senderUserId) {
        Long receiverUserId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("Rejecting connection request with receiverId: {} for senderUserId: {}", receiverUserId, senderUserId);

        if(senderUserId.equals(receiverUserId)) {
            throw new BadRequestException("Both sender and receiver are same");
        }

        boolean isConnectionRequestSentAlready = personRepository.connectionRequestExists(senderUserId, receiverUserId);
        if(!isConnectionRequestSentAlready) {
            throw new BadRequestException("No Connection request exists to this id: {}" + senderUserId);
        }

        personRepository.rejectConnectionRequest(senderUserId, receiverUserId);
        log.info("Successfully rejected the request.");
    }

    public List<Person> getAllPendingRequests(Long ownerUserId) {
        return personRepository.getAllPendingRequests(ownerUserId);
    }

    public String getEmailOfUser(Long userId) {
        Person person = personRepository.findByUserId(userId).orElseThrow(() -> new BadRequestException("user is not exist with id: " + userId));
        return person.getEmail();
    }
}
