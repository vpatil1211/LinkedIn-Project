package com.codingShuttle.linkedInProject.postsService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-service", path = "/user", url = "${USER_SERVICE_URI:}")
@FeignClient(name = "user-service", path = "/user")
public interface UserServiceClient {

    @GetMapping("/core/email/{userId}")
    String getEmailOfUser(@PathVariable Long userId);
}
