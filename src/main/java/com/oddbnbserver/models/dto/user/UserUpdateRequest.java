package com.oddbnbserver.models.dto.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;

}