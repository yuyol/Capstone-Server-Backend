package com.yyws.capstone_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "DEVICE")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private long id;
    private String name;
    // unit: MHZ
    private long cpuFrequency;
    // unit: KB
    private long sram;
    // unit: KB
    private long flash;
    private String cpuArch;
}
