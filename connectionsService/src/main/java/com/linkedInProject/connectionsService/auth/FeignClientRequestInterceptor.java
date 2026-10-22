package com.linkedInProject.connectionsService.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Long userId = AuthContextHolder.getUserIdFromContextHolder();
        if(userId != null) {
            template.header("X-User-Id", String.valueOf(userId));
        }
    }
}
