package com.linkedInProject.connectionsService.controller;

import com.linkedInProject.connectionsService.entiity.Person;
import com.linkedInProject.connectionsService.service.ConnectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/core")
public class ConnectionController {
    private final ConnectionsService connectionsService;

    public ConnectionController(ConnectionsService connectionsService) {
        this.connectionsService = connectionsService;
    }

    @GetMapping("/first-degree/{userId}")
    public ResponseEntity<List<Person>> getFirstDegreeConnections(@PathVariable Long userId) {
        log.info("Getting first degree connections for userId: {}", userId);
        List<Person> personList = connectionsService.getFirstDegreeConnection(userId);
        return ResponseEntity.ok(personList); // ResponseEntity<>(personList, HttpStatus.FOUND);
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<Void> sendConnectionRequest(@PathVariable Long userId) {
        connectionsService.sendConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Void> acceptConnectionRequest(@PathVariable Long userId) {
        connectionsService.acceptConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long userId) {
        connectionsService.rejectConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-pending-requests/{userId}")
    public ResponseEntity<List<Person>> getAllPendingRequests(@PathVariable Long userId) {
        List<Person> pendingRequestsList = connectionsService.getAllPendingRequests(userId);
        return ResponseEntity.ok(pendingRequestsList);
    }
}
