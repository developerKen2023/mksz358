package com.ken.mksz358.gateway.filters;

import com.alibaba.fastjson.JSON;
import com.ken.mksz358.jwt.entity.AuthJwtProperties;
import com.ken.mksz358.jwt.entity.JwtResponse;
import com.ken.mksz358.jwt.entity.ResponseCodeEnum;
import com.ken.mksz358.jwt.utils.JwtOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {

    private static final String AUTH_TOKEN_URL = "/auths/auth/login";
    private static final String REFRESH_TOKEN_URL = "/auth/token/refresh";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";

    @Resource
    private AuthJwtProperties authJwtProperties;

    @Resource
    private JwtOperator jwtOperator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest serverHttpRequest = exchange.getRequest();
        final String urlPath = serverHttpRequest.getPath().value();
        final ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();

        //if url is login url or enabled jwt is false, skip
        if (StringUtils.equals(AUTH_TOKEN_URL, urlPath) || !authJwtProperties.getEnabled())
            return chain.filter(exchange);

        String token = getToken(serverHttpRequest);
        if (StringUtils.isEmpty(token))
            return unauthorizedResponse(exchange, response, ResponseCodeEnum.TOKEN_MISSION);

        boolean isJwtNotValid = jwtOperator.isTokenExpired(token);
        if (isJwtNotValid)
            return unauthorizedResponse(exchange, response, ResponseCodeEnum.TOKEN_INVALID);

        String userId = jwtOperator.getUserIdFromToken(token);
        String username = jwtOperator.getUserNameFromToken(token);

        //check userId whether is null or not
        if (StringUtils.isEmpty(userId))
            return unauthorizedResponse(exchange, response, ResponseCodeEnum.TOKEN_INVALID);

        //add header
        addHeader(mutate, USER_ID, userId);
        addHeader(mutate, USER_NAME, username);

        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) return;
        String valueStr = value.toString();
        String valueEncode = urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    private String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(authJwtProperties.getHeader());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ServerHttpResponse serverHttpResponse, ResponseCodeEnum responseCodeEnum) {
        log.info("token exception,url is {}", exchange.getRequest().getPath());
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        JwtResponse<Object> responseResult = new JwtResponse<>(responseCodeEnum.getCode(), responseCodeEnum.getMessage());
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory()
                .wrap(JSON.toJSONStringWithDateFormat(responseResult, JSON.DEFFAULT_DATE_FORMAT)
                        .getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}