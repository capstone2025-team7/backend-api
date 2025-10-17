package com.capstone2025.team7.backend.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminEmail;

    public List<String> createRoles(String email) {
        List<String> roles = new ArrayList<>();

        if (adminEmail != null && !adminEmail.isEmpty() && email.equals(adminEmail)) {
            roles.add("ADMIN");
        }

        roles.add("USER");

        return roles;
    }

    public Collection<GrantedAuthority> createAuthorities(List<String> roles){
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return authorities;
    }
}
