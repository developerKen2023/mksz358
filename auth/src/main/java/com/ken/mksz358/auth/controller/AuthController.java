package com.ken.mksz358.auth.controller;

import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import com.ken.mksz358.feignApi.pojo.UserDto;
import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import com.ken.mksz358.jwt.entity.AuthJwtProperties;
import com.ken.mksz358.jwt.utils.JwtOperator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private JwtOperator jwtOperator;

    @Resource
    private UserCenterFeignClient userCenterFeignClient;

    @Resource
    private AuthJwtProperties authJwtProperties;

    @PostMapping("/login")
    public Map<String, Object> authLogin(@RequestBody UserLoginDto loginDto) {
        UserDto userDto = userCenterFeignClient.addUser(loginDto);
        return jwtOperator.generateTokenAndRefreshToken(loginDto.getWxId(), userDto.getWxNickname());
    }

    @GetMapping("/validateToken")
    public Boolean validateToken(@RequestParam("token") String token,
                                 @RequestParam("userId") String userId) {
        return jwtOperator.validateToken(token, userId);
    }

    @GetMapping("/getExpirationDate")
    public String getExpirationDateFromToken(@RequestParam("token") String token) {
        return jwtOperator.getExpirationDateFromToken(token);
    }

    @GetMapping("/getUserIdFromRequest")
    public String getHeaders() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        return jwtOperator.getUserIdFromRequest(request);
    }
}
