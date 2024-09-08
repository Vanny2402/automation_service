package com.vanny.Automateapi.model.jwtmodel;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String password;
    private String authorities;
    
    
}
