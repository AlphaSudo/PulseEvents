package com.pro.authenticationservice.security;


import com.pro.authenticationservice.model.User;
import com.pro.authenticationservice.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;

    public UserDetailsServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user details for username: " + username);
        try {
            User u = repo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            System.out.println("User found: " + u.getUsername());
            System.out.println("User roles: " + u.getRoles());

            var authorities = u.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority(r.name()))
                    .collect(Collectors.toList());
            System.out.println("Authorities: " + authorities);

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    u.getUsername(), u.getPassword(), authorities);
            System.out.println("UserDetails created successfully");
            return userDetails;
        } catch (Exception e) {
            System.out.println("Error loading user details: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
