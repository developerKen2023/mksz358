package com.ken.mksz358.contentcenter.controllter;

import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import com.ken.mksz358.feignApi.pojo.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tests")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final RestTemplate restTemplate;

    private final DiscoveryClient discoveryClient;

    private final UserCenterFeignClient userCenterFeignClient;

    @Value("${server.port}")
    private String port;

    @GetMapping("/testFindUser/{id}")
    public UserDto testFindUser(@PathVariable Integer id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        String uri = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("currently, no available server in nacos server"));
        log.info("uri:{}", uri);
        UserDto dto = restTemplate.getForObject(uri, UserDto.class, id);
        return dto;
    }

    @GetMapping("/customRibbonFindUser/{id}")
    public UserDto customRibbonFindUser(@PathVariable Integer id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        List<String> uris = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(uris.size());
        String uri = uris.get(index);
        log.info("uri:{}", uri);
        UserDto dto = restTemplate.getForObject(uri, UserDto.class, id);
        return dto;
    }

    @GetMapping("/getInstances")
    public List<ServiceInstance> getInstances() {
        return discoveryClient.getInstances("user-center");
    }

    @GetMapping("/testA")
    public String testA() {
        log.info("used by {}", port);
        return "AAA";
    }

    @GetMapping("/queryUser")
    public UserDto queryUser(UserDto userDto) {
        return userCenterFeignClient.queryUser(userDto);
    }
}
