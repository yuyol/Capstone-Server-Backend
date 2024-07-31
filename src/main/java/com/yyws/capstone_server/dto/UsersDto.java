package com.yyws.capstone_server.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UsersDto {

    private long userId;
    private String uniqueId;
    @NotEmpty(message = "Nick name can not be empty")
    private String username;
    private String email;
    private String mobileNumber;
    private String password;
    private String profilePictureUrl;
    private String text;
}
