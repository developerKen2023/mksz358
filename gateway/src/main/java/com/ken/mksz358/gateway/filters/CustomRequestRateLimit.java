package com.ken.mksz358.gateway.filters;

import com.alibaba.nacos.shaded.com.google.common.cache.Cache;
import com.alibaba.nacos.shaded.com.google.common.cache.CacheBuilder;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomRequestRateLimit implements GatewayFilter, Ordered {

    @Autowired
    Config config;

    private static final Cache<String, RateLimiter> RATE_LIMITER_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest serverHttpRequest = exchange.getRequest();
        final String urlPath = serverHttpRequest.getPath().value();
        final HttpHeaders headers = serverHttpRequest.getHeaders();
        final ServerHttpResponse response = exchange.getResponse();

        String group = exchange.getRequest().getHeaders().getFirst("JWT-TOKEN");
        List<String> mr = config.getGroups().stream().filter(g -> Objects.equals("MR", g)).collect(Collectors.toList());
        // decode and get payload here
        // get group from payload...
        if (Objects.equals("MR", group)) {
            //set rate limiter
            RateLimiter rateLimiter = RATE_LIMITER_CACHE.get(group, () ->
                    RateLimiter.create(Double.parseDouble(config.getPermitsPerSecond())));
            if (rateLimiter.tryAcquire()) {
                return chain.filter(exchange);
            }
        } else {
            //set rate limiter
            //don't do filter instantly
            return chain.filter(exchange);
        }
        return errorResponse(response, exchange, "too many request!");
    }

    private Mono<Void> errorResponse(ServerHttpResponse response, ServerWebExchange exchange, String message) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 200;
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "ken.gateway.api")
    public static class Config {
        private List<String> groups;
        private String permitsPerSecond;
    }
}
