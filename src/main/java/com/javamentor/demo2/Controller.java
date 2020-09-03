package com.javamentor.demo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class Controller {

    private final RestTemplate restTemplate;

    @Autowired
    public Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getUsers() {
        String url = "http://91.241.64.178:7081/api/users";

        ResponseEntity<List<User>> firstResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>(){}
        );
        HttpHeaders headers = firstResponse.getHeaders();
        List<String> sessionID = headers.get("Set-Cookie");
        System.out.println(headers);

        HttpHeaders newHeaders = new HttpHeaders();
        assert sessionID != null;
        newHeaders.addAll("Cookie", sessionID);
        newHeaders.setContentType(MediaType.APPLICATION_JSON);
        newHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        System.out.println(newHeaders);

        User newUser = new User(3L, "James", "Brown", (byte) 35);

        HttpEntity<User> postingUser = new HttpEntity<>(newUser, newHeaders);

        System.out.println(postingUser);

        ResponseEntity<String> secondResponse = restTemplate.exchange(
                url,
                HttpMethod.POST,
                postingUser,
                String.class
        );

        System.out.println(headers);

        Map<String, String> param = new HashMap<String, String>();
        param.put("id","3");

        User updatedUser = new User(Long.parseLong(param.get("id")), "Thomas", "Shelby", (byte) 35);

        HttpEntity<User> updatingUser = new HttpEntity<>(updatedUser, newHeaders);

        System.out.println(updatedUser);

        ResponseEntity<String> thirdResponse = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                updatingUser,
                String.class,
                param
        );

        url += "/3";

        HttpEntity<?> deletingUser = new HttpEntity<>(newHeaders);

        ResponseEntity<String> forthResponse = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                deletingUser,
                String.class
        );

        System.out.println(secondResponse.getBody() + thirdResponse.getBody() + forthResponse.getBody());

        return firstResponse;

    }

}
