package com.ken.mksz358.usercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.ken")
@SpringBootApplication(scanBasePackages = {"com.ken.mksz358.usercenter","com.ken.mksz358.feignApi"})
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }
}