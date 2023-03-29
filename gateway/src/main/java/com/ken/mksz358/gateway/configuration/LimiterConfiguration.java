package com.ken.mksz358.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LimiterConfiguration {
    /**
     * 按照Path限流
     *
     * @return key
     */
//    @Bean
//    public KeyResolver pathKeyResolver() {
//        return exchange -> Mono.just(
//                exchange.getRequest()
//                        .getPath()
//                        .toString()
//        );
//    }

    /**
     * 按照用户
     *
     * @return
     */
//    @Bean
//    public KeyResolver userKeyResolver() {
//        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
//    }
    
    @Bean
    public KeyResolver ipKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
//                return Mono.just(
//                        exchange.getRequest()
//                                .getHeaders()
//                                .getFirst("X-Forwarded-For")
//                );
                return Mono.just("666");
            }
        };
    }
}
