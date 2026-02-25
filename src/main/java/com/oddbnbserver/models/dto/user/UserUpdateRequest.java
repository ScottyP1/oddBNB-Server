package com.oddbnbserver.models.dto.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserUpdateRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String password;

    private String role;

    private List<Long> hostedListingIds = List.of();
    private List<Long> reviewIds = List.of();
    private List<Long> favoriteIds = List.of();

}