package com.oddbnbserver.models.dto.user;


import com.oddbnbserver.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;

    private List<Long> hostedListingIds = List.of();
    private List<Long> reviewIds = List.of();
    private List<Long> favoriteIds = List.of();

}