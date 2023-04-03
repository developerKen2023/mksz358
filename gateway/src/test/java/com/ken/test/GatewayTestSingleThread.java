package com.ken.test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


public class GatewayTestSingleThread {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8040/user/users/1";
        HttpHeaders headers = new HttpHeaders();
        headers.add("JWT-TOKEN", "MRPlus");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        for (int i = 0; i < 5; i++) {
            restTemplate.postForObject(url, httpEntity, String.class);
        }

        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("JWT-TOKEN", "SA");
        HttpEntity<String> httpEntity1 = new HttpEntity<>(headers1);
        for (int i = 0; i < 6; i++) {
            restTemplate.postForObject(url, httpEntity1, String.class);
        }
    }
}
