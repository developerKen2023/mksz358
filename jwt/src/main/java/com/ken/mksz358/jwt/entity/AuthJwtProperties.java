package com.ken.mksz358.jwt.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "auth.jwt")
@Component
public class AuthJwtProperties {

    //是否开启JWT，即注入相关的类对象
    private Boolean enabled = true;

    //JWT 密钥
    private String secret;

    //accessToken 有效时间
    private Long expiration;

    //header名称
    private String header;

    //跳过认证的路由
    private String skipValidUrl;

    /**
     * 用户登录-用户名参数名称
     */
    private String userParamName = "userId";
    /**
     * 用户登录-密码参数名称
     */
    private String pwdParamName = "password";
}
