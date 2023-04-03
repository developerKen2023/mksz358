package com.ken.mksz358.feignApi.fallbackFactory;

import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import com.ken.mksz358.feignApi.pojo.UserDto;
import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCenterFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDto findById(Integer id) {
                log.warn("远程调用被限流/降级了", throwable);
                return new UserDto();
            }

            @Override
            public UserDto addUser(UserLoginDto userLoginDto) {
                log.warn("远程调用被限流/降级了", throwable);
                return new UserDto();
            }

            @Override
            public UserDto queryUser(UserDto userDto) {
                return userDto;
            }
        };
    }
}
