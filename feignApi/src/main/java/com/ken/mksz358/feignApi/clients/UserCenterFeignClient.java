package com.ken.mksz358.feignApi.clients;

import com.ken.mksz358.feignApi.configuration.GlobalFeignConfiguration;
import com.ken.mksz358.feignApi.fallbackFactory.UserCenterFallbackFactory;
import com.ken.mksz358.feignApi.pojo.UserDto;
import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-center", configuration = GlobalFeignConfiguration.class, fallbackFactory = UserCenterFallbackFactory.class)
public interface UserCenterFeignClient {

    @PostMapping("/users/{id}")
    UserDto findById(@PathVariable("id") Integer id);

    @PostMapping("/users/addUser")
    UserDto addUser(@RequestBody UserLoginDto userLoginDto);

    /**
     * 支持多参数访问 get请求可以这样玩
     * 如果是post请求 直接@RequestBody
     *
     * @param userDto
     * @return
     */
    @GetMapping("/users/queryUser")
    UserDto queryUser(@SpringQueryMap UserDto userDto);
}
