package com.yyws.capstone_server.service;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;

import java.util.List;

public interface ServerService {
    List<DeviceDto> findAllDevice();

    List<ModelDto> findAllModel();
    DeviceModelDto findAllDeviceAndModel();

}
