package com.ken.mksz358.gateway.filters;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HostAddrKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
//        return Mono.just(
//                exchange.getRequest()
//                        .getHeaders()
//                        .getFirst("JWT-TOKEN")
//        );
        return Mono.just("LOCK");
    }
}
