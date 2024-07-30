package com.yyws.capstone_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {
    public DeviceDto(String name,long cpuFrequency,long sram,long flash,String cpuArch) {
        this.name = name;
        this.cpuFrequency = cpuFrequency;
        this.sram = sram;
        this.flash = flash;
        this.cpuArch = cpuArch;
    }
    public DeviceDto(long id,String name,long cpuFrequency,long sram,long flash,String cpuArch) {
        this.id = id;
        this.name = name;
        this.cpuFrequency = cpuFrequency;
        this.sram = sram;
        this.flash = flash;
        this.cpuArch = cpuArch;
    }
    private long id;
    private String ipAddr;
    private String urlString;
    private String name;
    private long cpuFrequency;
    private long sram;
    private long flash;
    private int floatingPoint;
    private String cpuArch;

    private LocalDateTime lastHeartBeat;
}
