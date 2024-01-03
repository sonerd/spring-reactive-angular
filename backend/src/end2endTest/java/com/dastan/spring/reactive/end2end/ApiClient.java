package com.dastan.spring.reactive.end2end;

import com.dastan.spring.reactive.common.LoginRequest;
import com.dastan.spring.reactive.common.PaginatedResult;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ApiClient {
    private final String SERVER_URL = "http://localhost";
    private final RestTemplate restTemplate = new RestTemplate();
    @LocalServerPort
    private int port;

    public ResponseEntity<Void> login(final LoginRequest request) {
        final String url = SERVER_URL + ":" + port + "/login";
        return restTemplate.postForEntity(url, request, Void.class);
    }

    public ResponseEntity<PaginatedResult> queryPosts(final String query, int offset, int limit) {
        final String url = SERVER_URL + ":" + port + "/posts?q=" + query + "&offset=" + offset + "&limit=";
        return restTemplate.getForEntity(url, PaginatedResult.class);
    }
}
