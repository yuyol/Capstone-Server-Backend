package com.yyws.capstone_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private Long userId;
    private String uniqueId;
    @NotEmpty(message = "Nick name can not be empty")
    private String username;
    @Email(message = "Please enter right email address")
    private String email;
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Please enter right phone number")
    @NotEmpty(message = "Phone number can not be empty")
    private String mobileNumber;
    @Size(min = 8,message = "Please enter at least eight passwords")
    @NotEmpty(message = "Password can not be empty")
    private String password;
}
