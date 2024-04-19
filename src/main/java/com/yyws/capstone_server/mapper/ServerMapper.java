package com.yyws.capstone_server.mapper;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.Model;

public class ServerMapper {

    public static DeviceDto DeviceToDeviceDto(Device device, DeviceDto deviceDto) {
        deviceDto.setId(device.getId());
        deviceDto.setName(device.getName());
        deviceDto.setCpuFrequency(device.getCpuFrequency());
        deviceDto.setSram(device.getSram());
        deviceDto.setFlash(device.getFlash());
        deviceDto.setCpuArch(device.getCpuArch());
        deviceDto.setFloatingPoint(device.getFloatingPoint());
        deviceDto.setLastHeartBeat(device.getLastHeartBeat());
        return deviceDto;
    }
    public static Device DeviceDtoToDevice(DeviceDto deviceDto, Device device) {
        device.setId(deviceDto.getId());
        device.setName(deviceDto.getName());
        device.setCpuFrequency(deviceDto.getCpuFrequency());
        device.setSram(deviceDto.getSram());
        device.setFlash(deviceDto.getFlash());
        device.setCpuArch(deviceDto.getCpuArch());
        device.setFloatingPoint(deviceDto.getFloatingPoint());
        device.setLastHeartBeat(deviceDto.getLastHeartBeat());
        return device;
    }

    public static ModelDto ModelToModelDtos(Model model, ModelDto modelDto) {
        modelDto.setId(model.getId());
        modelDto.setName(model.getName());
        return modelDto;
    }
}
