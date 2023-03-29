package com.ken.mksz358.gateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestRateLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    CustomRequestRateLimit customRequestRateLimit;

    @Override
    public GatewayFilter apply(Object config) {
        return customRequestRateLimit;
    }
}
