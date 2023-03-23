package sentinel.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class sentinelTest {

    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String[] uris = new String[]{
                "http://localhost:8020/tests/testA",
                "http://localhost:8021/tests/testA",
                "http://localhost:8022/tests/testA"
        };
        for (int i = 0; i < 3; i++) {
            if (i==0){
                restTemplate.getForObject(uris[i],String.class);
            }
            if (i==1){
                restTemplate.getForObject(uris[i],String.class);
            }
            if (i==2){
                restTemplate.getForObject(uris[i],String.class);
            }
        }
    }
}
