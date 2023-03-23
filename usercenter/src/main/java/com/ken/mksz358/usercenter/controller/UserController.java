package com.ken.mksz358.usercenter.controller;

import com.ken.mksz358.usercenter.domain.entity.user.User;
import com.ken.mksz358.usercenter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserController {

    private final UserService userService;

    @Value("${server.port}")
    public String port;

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("port:{}", port);
        return userService.findById(id);
    }

    @GetMapping("/queryUser")
    public User queryUser(User user) {
        return user;
    }
}
