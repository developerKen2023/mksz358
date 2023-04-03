package com.ken.mksz358.auth;

import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.ken.mksz358.auth", "com.ken.mksz358.feignApi","com.ken.mksz358.jwt"})
@EnableFeignClients(clients = {UserCenterFeignClient.class})
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}