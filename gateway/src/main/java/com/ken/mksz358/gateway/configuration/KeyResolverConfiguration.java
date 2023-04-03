package com.ken.mksz358.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfiguration {

    public static final String LOCK = "LOCK";

    /**
     * 锁的是同一个资源
     * @return
     */
    @Bean
    public KeyResolver lockKeyResolver() {
        return exchange -> Mono.just(LOCK);
    }
}
