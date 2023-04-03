package com.ken.mksz358.auth.controller;

import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import com.ken.mksz358.feignApi.pojo.UserDto;
import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import com.ken.mksz358.jwt.utils.JwtOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private JwtOperator jwtOperator;

    @Resource
    private UserCenterFeignClient userCenterFeignClient;

    @PostMapping("/login")
    public Map<String, Object> authLogin(@RequestBody UserLoginDto loginDto) {
        UserDto userDto = userCenterFeignClient.addUser(loginDto);
        return jwtOperator.generateTokenAndRefreshToken(loginDto.getWxId(), userDto.getWxNickname());
    }
}
