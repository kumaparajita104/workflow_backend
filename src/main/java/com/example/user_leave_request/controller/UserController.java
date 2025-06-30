package com.example.user_leave_request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", jwt.getClaimAsString("preferred_username")); // or "email", "name", etc.
        System.out.println(jwt.getClaimAsString("preferred_username"));
        List<String> roles = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
            roles.addAll(((List<?>) realmAccess.get("roles")).stream()
                    .filter(role -> role instanceof String)
                    .map(Object::toString)
                    .toList());
        }

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("user_leave_api")) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get("user_leave_api");
            if (clientRoles.get("roles") instanceof List<?>) {
                roles.addAll(((List<?>) clientRoles.get("roles")).stream()
                        .filter(role -> role instanceof String)
                        .map(Object::toString)
                        .toList());
            }
        }

        userInfo.put("roles", roles);
        return ResponseEntity.ok(userInfo);
    }
}
