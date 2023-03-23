package com.ken.mksz358.feignApi.clients;

import com.ken.mksz358.feignApi.configuration.GlobalFeignConfiguration;
import com.ken.mksz358.feignApi.fallbackFactory.UserCenterFallbackFactory;
import com.ken.mksz358.feignApi.pojo.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-center", configuration = GlobalFeignConfiguration.class, fallbackFactory = UserCenterFallbackFactory.class)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDto findById(@PathVariable("id") Integer id);

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
