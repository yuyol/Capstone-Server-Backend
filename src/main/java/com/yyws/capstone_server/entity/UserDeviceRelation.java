package com.yyws.capstone_server.entity;

import jakarta.persistence.Entity;
import lombok.*;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDeviceRelation {
    String email;
    String deviceId;
}
