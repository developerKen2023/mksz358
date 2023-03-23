package com.ken.mksz358.feignApi.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class GlobalFeignConfiguration {

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }
}
