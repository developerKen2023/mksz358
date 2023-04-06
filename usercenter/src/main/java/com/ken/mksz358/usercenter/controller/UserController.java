package com.ken.mksz358.usercenter.controller;

import com.ken.mksz358.feignApi.auth.CheckAuthorization;
import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import com.ken.mksz358.usercenter.domain.entity.user.User;
import com.ken.mksz358.usercenter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserController {

    private final UserService userService;

    @Value("${server.port}")
    public String port;

    @CheckAuthorization("admin")
    @PostMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("port:{}", port);
        return userService.findById(id);
    }

    @PostMapping("/addUser")
    public User login(@RequestBody UserLoginDto userLoginDto) {
        return userService.addUser(userLoginDto);
    }

    @GetMapping("/queryUser")
    public User queryUser(User user) {
        return user;
    }
}
