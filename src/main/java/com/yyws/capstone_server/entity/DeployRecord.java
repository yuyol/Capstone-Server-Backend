package com.yyws.capstone_server.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeployRecord {
    String email;
    String device;
    String model;
    int status;
    private LocalDateTime date;
}
