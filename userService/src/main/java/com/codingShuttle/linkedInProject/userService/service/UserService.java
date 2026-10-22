package com.codingShuttle.linkedInProject.userService.service;

import com.codingShuttle.linkedInProject.userService.Exception.BadRequestException;
import com.codingShuttle.linkedInProject.userService.dto.LoginRequestDto;
import com.codingShuttle.linkedInProject.userService.dto.SignUpResponseDto;
import com.codingShuttle.linkedInProject.userService.dto.SignupRequestDto;
import com.codingShuttle.linkedInProject.userService.entity.UserEntity;
import com.codingShuttle.linkedInProject.userService.event.UserCreatedEvent;
import com.codingShuttle.linkedInProject.userService.repository.UserRepository;
import com.codingShuttle.linkedInProject.userService.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate ;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, JWTService jwtService, KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.userCreatedEventKafkaTemplate = userCreatedEventKafkaTemplate;
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Login request for user with email: {}", loginRequestDto.getEmail());
        UserEntity user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new BadRequestException("Incorrect Password/email"));
        boolean isPasswordMatch =  BCrypt.matchPassword(loginRequestDto.getPassword(), user.getPassword());
        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect Password/email");
        }
        String token = jwtService.generateAccessToken(user);
        Long userId = jwtService.getUserIdFromToken(token);
        return token;
    }

    public SignUpResponseDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}", signupRequestDto.getEmail());
        UserEntity user = modelMapper.map(signupRequestDto, UserEntity.class);
        boolean isUserAlreadyExisted = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(isUserAlreadyExisted) {
            throw new BadRequestException("User is already exists with given email: {}" + signupRequestDto.getEmail());
        }
        user.setPassword(BCrypt.hashPassword(signupRequestDto.getPassword()));
        user.setFirstName(signupRequestDto.getFirstName());
        user.setFamilyName(signupRequestDto.getFamilyName());
        user.setEmail(signupRequestDto.getEmail());
        user = userRepository.save(user);

        // after signup send the user to connections service
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .build();

        userCreatedEventKafkaTemplate.send("user_created_topic", userCreatedEvent);


        return modelMapper.map(user, SignUpResponseDto.class);
    }

    public String getEmailOfUser(Long userId) {
       UserEntity user = userRepository.findById(userId)
               .orElseThrow(() -> new BadRequestException("user is exists with id: {}" + userId));
       return user.getEmail();
    }
}
