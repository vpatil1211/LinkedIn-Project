package com.codingShuttle.linkedInProject.postsService.controller;

import com.codingShuttle.linkedInProject.postsService.auth.AuthContextHolder;
import com.codingShuttle.linkedInProject.postsService.client.ConnectionsServiceClient;
import com.codingShuttle.linkedInProject.postsService.dto.PersonDto;
import com.codingShuttle.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.codingShuttle.linkedInProject.postsService.dto.PostDto;
import com.codingShuttle.linkedInProject.postsService.entity.Post;
import com.codingShuttle.linkedInProject.postsService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/core")
@Slf4j
public class PostController {

    private final PostService postService;
    private final ConnectionsServiceClient connectionsServiceClient;

    public PostController(PostService postService, ConnectionsServiceClient connectionsServiceClient) {
        this.postService = postService;
        this.connectionsServiceClient = connectionsServiceClient;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto) {
    public ResponseEntity<PostDto> createPost(@RequestPart("post") PostCreateRequestDto postCreateRequestDto,
                                              @RequestPart("file") MultipartFile file) {

        PostDto postDto = postService.createPost(postCreateRequestDto, file);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPosts(@PathVariable Long postId) {
        Long userId = AuthContextHolder.getUserIdFromContextHolder();
       // log.info("X-User-Id: {}", userId);
//      TODO: this for example to call connections service from post service and pass userId inside headers
//        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);
        PostDto postDto = postService.getPosts(postId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/user/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId) {
        List<PostDto> postsList = postService.getAllPostsOfUser(userId);
        return ResponseEntity.ok(postsList);
    }

}
