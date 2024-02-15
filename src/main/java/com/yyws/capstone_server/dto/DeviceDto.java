package com.yyws.capstone_server.dto;

import lombok.Data;

@Data
public class DeviceDto {
    private long id;
    private String name;
    private long cpuFrequency;
    private long sram;
    private long flash;
    private String cpuArch;

}
