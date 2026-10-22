package com.codingShuttle.linkedInProject.postsService.client;

import com.codingShuttle.linkedInProject.postsService.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(name = "connections-service", path = "/connection", url = "${CONNECTIONS_SERVICE_URI:}")
@FeignClient(name = "connections-service", path = "/connection")
public interface ConnectionsServiceClient {

    @GetMapping("/core/first-degree/{userId}")
    List<PersonDto> getFirstDegreeConnections(@PathVariable Long userId);
}
