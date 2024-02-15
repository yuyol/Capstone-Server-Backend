package com.yyws.capstone_server.service.Impl;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.Model;
import com.yyws.capstone_server.mapper.ServerMapper;
import com.yyws.capstone_server.repository.DeviceRepository;
import com.yyws.capstone_server.repository.ModelRepository;
import com.yyws.capstone_server.service.ServerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ServerServiceImpl implements ServerService {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    ModelRepository modelRepository;

    @Override
    public List<DeviceDto> findAllDevice() {
        List<Device> devices = deviceRepository.findAll();
        List<DeviceDto> deviceDtos = new ArrayList<>();
        for (Device device :
                devices) {
            deviceDtos.add(ServerMapper.DeviceToDeviceDto(device, new DeviceDto()));
        }
        return deviceDtos;
    }

    @Override
    public List<ModelDto> findAllModel() {
        List<Model> models = modelRepository.findAll();
        List<ModelDto> modelDtos = new ArrayList<>();
        for (Model model :
                models) {
            modelDtos.add(ServerMapper.ModelToModelDtos(model, new ModelDto()));
        }
        return modelDtos;
    }

    @Override
    public DeviceModelDto findAllDeviceAndModel() {
        List<DeviceDto> allDevice = findAllDevice();
        List<ModelDto> allModel = findAllModel();
        DeviceModelDto deviceModelDto = new DeviceModelDto();
        deviceModelDto.setDeviceDtoList(allDevice);
        deviceModelDto.setModelDtoList(allModel);
        return deviceModelDto;
    }
}
