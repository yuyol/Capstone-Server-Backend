package com.yyws.capstone_server.service;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;

import java.util.List;

public interface ServerService {
    List<DeviceDto> findAllDevice();

    List<ModelDto> findAllModel();
    DeviceModelDto findAllDeviceAndModel();

    void modifyDeviceInfoById(DeviceDto deviceDto);

    void addDevice(DeviceDto deviceDto);

    DeviceDto parseInfo(String deviceInfo);

    DeviceDto findDeviceInfo(Long id);

    void devicePingServer(String deviceInfo);

    List<DeviceDto> checkDevicesHeartbeat();

    List<DeviceDto> searchOwnedDevices(String email);

    void registerDevice(String email, String deviceId);

    List<DeviceDto> checkDevicesHeartbeatLogin(String email);
}
