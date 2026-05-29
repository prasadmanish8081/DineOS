package com.dineos.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/access")
public class AccessController {

    @GetMapping("/customer")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'KITCHEN', 'OWNER', 'ADMIN')")
    public Map<String, String> customer(Authentication authentication) {
        return Map.of("message", "Accessible by CUSTOMER and higher", "user", authentication.getName());
    }

    @GetMapping("/kitchen")
    @PreAuthorize("hasAnyRole('KITCHEN', 'OWNER', 'ADMIN')")
    public Map<String, String> kitchen(Authentication authentication) {
        return Map.of("message", "Accessible by KITCHEN and higher", "user", authentication.getName());
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public Map<String, String> owner(Authentication authentication) {
        return Map.of("message", "Accessible by OWNER and ADMIN", "user", authentication.getName());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> admin(Authentication authentication) {
        return Map.of("message", "Accessible by ADMIN only", "user", authentication.getName());
    }
}
