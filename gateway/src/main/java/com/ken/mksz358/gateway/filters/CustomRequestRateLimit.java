package com.ken.mksz358.gateway.filters;

import com.alibaba.nacos.shaded.com.google.common.cache.Cache;
import com.alibaba.nacos.shaded.com.google.common.cache.CacheBuilder;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.PassiveExpiringMap;
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

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CustomRequestRateLimit implements GatewayFilter, Ordered {

    @Autowired
    Config config;

    Set<String> keySets = Collections.emptySet();

    //1 hour for group count 60*60*1000 = 3600_000
    final Map<String, Integer> groupCountCache = Collections.synchronizedMap(new PassiveExpiringMap<>(3600_000));

    private static final Cache<String, RateLimiter> RATE_LIMITER_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    @PostConstruct
    public void doInit() {
        Objects.requireNonNull(config.getGroupsMap());
        keySets = config.getGroupsMap().keySet();
    }

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest serverHttpRequest = exchange.getRequest();
        final String urlPath = serverHttpRequest.getPath().value();
        final HttpHeaders headers = serverHttpRequest.getHeaders();
        final ServerHttpResponse response = exchange.getResponse();

        // actually, when we get JWT token, we need to decode token and get payload
        // assumption: skip get group process, regard JWT token as group
        String group = headers.getFirst(config.getTokenName());

        int totalGroupCount = 0;
        int otherGroupCount = 0;
        // check groups
        // get other keys
        /**
         *  逻辑：
         *  1.先规定每一个group的最低比例
         *  2.根据#groupCountCache#里面的实际访问数量,计算出每一个group的比例
         *  3.最低比例为按照事先#groupsMap#里定义好的比例
         *  4.根据总体比例调整
         */

        groupCountCache.put(group, groupCountCache.getOrDefault(group, 0) + 1);

        // get total count in groupCountCache
        for (String key : keySets) {
            totalGroupCount += groupCountCache.getOrDefault(key, 0);
            if (!Objects.equals(group, key)) otherGroupCount += groupCountCache.getOrDefault(key, 0);
        }

        double currentRate;

        if (otherGroupCount <= 0) {
            currentRate = 1;
        } else {
            Double countCacheRate = Math.round(groupCountCache.get(group) * 1000 / totalGroupCount) / 1000.0;
            currentRate = countCacheRate > config.getGroupsMap().get(group) ? countCacheRate : config.getGroupsMap().get(group);
        }

        log.info("group:{},currentRate:{},after calculating rate:{}", group, currentRate, currentRate * config.getTotalPermitsPerSecond());

        RateLimiter rateLimiter = RATE_LIMITER_CACHE.get(group, () ->
                RateLimiter.create(currentRate * config.getTotalPermitsPerSecond()));

        if (rateLimiter.tryAcquire()) {
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
        private Map<String, Double> groupsMap;
        private int totalPermitsPerSecond;
        private String tokenName;
    }
}
