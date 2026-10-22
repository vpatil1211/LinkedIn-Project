package com.codingShuttle.linkedInProject.postsService.service;

import com.codingShuttle.linkedInProject.postsService.auth.AuthContextHolder;
import com.codingShuttle.linkedInProject.postsService.client.UserServiceClient;
import com.codingShuttle.linkedInProject.postsService.entity.Post;
import com.codingShuttle.linkedInProject.postsService.entity.PostLike;
import com.codingShuttle.linkedInProject.postsService.event.PostLikedEvent;
import com.codingShuttle.linkedInProject.postsService.exception.BadRequestException;
import com.codingShuttle.linkedInProject.postsService.exception.ResourceNotFoundException;
import com.codingShuttle.linkedInProject.postsService.repository.PostLikeRepository;
import com.codingShuttle.linkedInProject.postsService.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostLikedEvent> postLikedEventKafkaTemplate;

    public PostLikeService(PostLikeRepository postLikeRepository, PostRepository postRepository, UserServiceClient userServiceClient, ModelMapper modelMapper, KafkaTemplate<Long, PostLikedEvent> postLikedEventKafkaTemplate) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userServiceClient = userServiceClient;
        this.modelMapper = modelMapper;
        this.postLikedEventKafkaTemplate = postLikedEventKafkaTemplate;
    }

    @Transactional
    public void likePost(Long postId) {
        Long userId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("User with Id: {} Liking the post with Id: {}", userId, postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("No any Post found with this Id: {}" + postId));
        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(hasAlreadyLiked) {
            throw new BadRequestException("You cannot like the post again!");
        }
        String email = userServiceClient.getEmailOfUser(userId);
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        //send notification to owner of the post
        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                .likedByUserId(userId)
                .postId(postId)
                .likedByUserEmail(email)
                .ownerUserId(post.getUserId())
                .ownerUserEmail(post.getEmail())
                .build();
        postLikedEventKafkaTemplate.send("post_liked_topic", postLikedEvent);
    }

    @Transactional
    public void unlikePost(Long postId) {
        log.info("Unliking the post with Id: {}", postId);
        Long userId = AuthContextHolder.getUserIdFromContextHolder();
        log.info("User with Id: {} Unliking the post with Id: {}", userId, postId);
        postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("No any Post found with this Id: {}" + postId));
        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(!hasAlreadyLiked) {
            throw new BadRequestException("You cannot Unlike the post again!");
        }
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    }
}
