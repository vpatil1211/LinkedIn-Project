package com.codingShuttle.linkedInProject.userService.controller;

import com.codingShuttle.linkedInProject.userService.service.UserService;
import jakarta.ws.rs.Path;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
public class UserDetailsController {
    private final UserService userService;

    public UserDetailsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email/{userId}")
    public String getEmailOfUser(@PathVariable Long userId) {
        return userService.getEmailOfUser(userId);
    }
}
