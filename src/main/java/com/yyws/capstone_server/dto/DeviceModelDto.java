package com.yyws.capstone_server.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceModelDto {
    private List<DeviceDto> deviceDtoList;
    private List<ModelDto> modelDtoList;
}
