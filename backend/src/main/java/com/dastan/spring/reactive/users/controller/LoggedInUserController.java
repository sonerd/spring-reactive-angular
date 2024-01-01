package com.dastan.spring.reactive.users.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/me")
public class LoggedInUserController {

    @GetMapping("")
    public Mono<Map<String, Object>> current(@AuthenticationPrincipal Mono<Principal> principal) {
        return principal.map(user -> Map.of("name", user.getName(), "roles",
                                            AuthorityUtils.authorityListToSet(((Authentication) user).getAuthorities())));
    }
}
