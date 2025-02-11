package com.asheck.smatech_store_service.helper;

import com.asheck.smatech_store_service.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RestTemplateService {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String AUTH_SERVICE_URL;

    public UserDto getAuthenticatedUser(String token) {

        String serviceUrl = AUTH_SERVICE_URL + token;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Add JWT token

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response =  restTemplate.exchange(serviceUrl, HttpMethod.GET, entity, UserDto.class);

        return response.getBody();
    }

}
