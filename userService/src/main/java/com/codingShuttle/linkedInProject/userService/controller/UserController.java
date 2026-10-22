package com.codingShuttle.linkedInProject.userService.controller;


import com.codingShuttle.linkedInProject.userService.dto.LoginRequestDto;
import com.codingShuttle.linkedInProject.userService.dto.LoginResponseDto;
import com.codingShuttle.linkedInProject.userService.dto.SignUpResponseDto;
import com.codingShuttle.linkedInProject.userService.dto.SignupRequestDto;
import com.codingShuttle.linkedInProject.userService.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignupRequestDto signupRequestDto) {
        SignUpResponseDto signUpResponseDto = userService.signUp(signupRequestDto);
        return new ResponseEntity(signUpResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto);
    }
}
