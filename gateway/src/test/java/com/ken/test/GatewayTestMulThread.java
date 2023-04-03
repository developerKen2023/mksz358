package com.ken.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GatewayTestMulThread {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8040/user/users/1";
        HttpHeaders headers = new HttpHeaders();
        headers.add("JWT-TOKEN", "MRPlus");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        final int poolSize = 11;

        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < poolSize; i++) {
            threadPool.submit(()->{
                restTemplate.postForObject(url, httpEntity, String.class);
            });
        }

        threadPool.shutdown();
    }
}
