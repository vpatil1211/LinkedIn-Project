package com.linkedInProject.apiGateway.filter;

import com.linkedInProject.apiGateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        /* this is used to get userId from token and pass to downStream services */
        return (exchange, chain) -> {

            log.info("Auth request: {}", exchange.getRequest().getURI());
            final String tokenHeaders = exchange.getRequest().getHeaders().getFirst("Authorization");
            if(tokenHeaders == null || !tokenHeaders.startsWith("Bearer")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            final String token = tokenHeaders.split("Bearer ")[1];
            try {
                String userId = jwtService.getUserIdFromToken(token);
                ServerWebExchange mutateExchange = exchange.mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();

                return chain.filter(mutateExchange);
            } catch(JwtException ex) {
                log.error("JWT Exception Occured: {}", ex.getLocalizedMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    static class Config {

    }
}
