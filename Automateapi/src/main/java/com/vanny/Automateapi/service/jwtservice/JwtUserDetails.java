package com.vanny.Automateapi.service.jwtservice;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {
    
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    // Constructor that accepts a list of comma-separated authorities
    public JwtUserDetails(String username, String password, String authorities) {
        this.username = username;
        this.password = password;
        this.authorities = Arrays.stream(authorities.split(","))
                                 .map(SimpleGrantedAuthority::new)
                                 .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // Return the list of GrantedAuthorities
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Set to true by default, customize if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Set to true by default, customize if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Set to true by default, customize if needed
    }

    @Override
    public boolean isEnabled() {
        return true;  // Set to true by default, customize if needed
    }
}
