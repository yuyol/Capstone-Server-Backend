package com.yyws.capstone_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "DEVICE")
public class Device {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private String name;
    private String ipAddr;
    // unit: MHZ
    private long cpuFrequency;
    // unit: KB
    private long sram;
    // unit: KB
    private long flash;
    private int floatingPoint;
    private String cpuArch;
    private LocalDateTime lastHeartBeat;
}
