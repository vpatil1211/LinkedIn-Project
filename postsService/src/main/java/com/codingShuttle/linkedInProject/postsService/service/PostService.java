package com.codingShuttle.linkedInProject.postsService.service;

import com.codingShuttle.linkedInProject.postsService.auth.AuthContextHolder;
import com.codingShuttle.linkedInProject.postsService.client.ConnectionsServiceClient;
import com.codingShuttle.linkedInProject.postsService.client.UploaderServiceClient;
import com.codingShuttle.linkedInProject.postsService.client.UserServiceClient;
import com.codingShuttle.linkedInProject.postsService.dto.PersonDto;
import com.codingShuttle.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.codingShuttle.linkedInProject.postsService.dto.PostDto;
import com.codingShuttle.linkedInProject.postsService.entity.Post;
import com.codingShuttle.linkedInProject.postsService.event.PostCreatedEvent;
import com.codingShuttle.linkedInProject.postsService.exception.ResourceNotFoundException;
import com.codingShuttle.linkedInProject.postsService.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final UserServiceClient userServiceClient;
    private final UploaderServiceClient uploaderServiceClient;
    private final KafkaTemplate<Long, PostCreatedEvent> postCreatedEventKafkaTemplate;

    public PostService(PostRepository postRepository, ModelMapper modelMapper, ConnectionsServiceClient connectionsServiceClient, UserServiceClient userServiceClient, UploaderServiceClient uploaderServiceClient, KafkaTemplate<Long, PostCreatedEvent> postCreatedEventKafkaTemplate) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.connectionsServiceClient = connectionsServiceClient;
        this.userServiceClient = userServiceClient;
        this.uploaderServiceClient = uploaderServiceClient;
        this.postCreatedEventKafkaTemplate = postCreatedEventKafkaTemplate;
    }

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, MultipartFile file) {
        Long userId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("Creating post for user with id: {}", userId);

        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(file);


        String ownerEmail = userServiceClient.getEmailOfUser(userId);
        Post postEntity = modelMapper.map(postCreateRequestDto, Post.class);
        postEntity.setUserId(userId);
        postEntity.setEmail(ownerEmail);
        postEntity.setImageUrl(imageUrl.getBody());
       // postEntity.setContent((String)postCreateRequestDto);
        postEntity = postRepository.save(postEntity);

        /*  Get first-degree connection of current user id to send them notification that user is posted */
        List<PersonDto> firstDegreeConnectionsList = connectionsServiceClient.getFirstDegreeConnections(userId);

        //String ownerEmail = userServiceClient.getEmailOfUser(userId);
        for(PersonDto person: firstDegreeConnectionsList) { //send notification to each connection
            PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                    .postId(postEntity.getId())
                    .ownerUserId(userId)
                    .ownerUserEmail(ownerEmail)
                    .userId(person.getUserId())
                    .userEmail(person.getEmail())
                    .content(postCreateRequestDto.getContent())
                    .build();
            postCreatedEventKafkaTemplate.send("post_created_topic", postCreatedEvent);
        }

        return modelMapper.map(postEntity, PostDto.class);
    }

    public PostDto getPosts(Long postId) {
        log.info("Getting the post with post Id: {}", postId);
        Post postEntity = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not not found for given id: {}" + postId));
        return modelMapper.map(postEntity, PostDto.class);

    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all posts of User with user Id: {}", userId);
        List<Post> postList = postRepository.findByUserId(userId);
        return postList.stream()
                .map(element -> modelMapper.map(element, PostDto.class))
                .collect(Collectors.toList());
    }
}
