package com.yyws.capstone_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsersDto {

    private long userId;
    private String uniqueId;
    @NotEmpty(message = "Nick name can not be empty")
    private String username;
    @Email(message = "Please enter right email address")
    @NotEmpty(message = "Email can not be empty")
    private String email;
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Please enter right phone number")
    private String mobileNumber;
    @Size(min = 8,message = "Please enter at least eight passwords")
    @NotEmpty(message = "Password can not be empty")
    private String password;
}
