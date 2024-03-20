package com.yyws.capstone_server.service.Impl;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.Model;
import com.yyws.capstone_server.mapper.ServerMapper;
import com.yyws.capstone_server.repository.DeviceRedisRepository;
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
    DeviceRedisRepository deviceRedisRepository;

    @Autowired
    ModelRepository modelRepository;

    @Override
    public List<DeviceDto> findAllDevice() {
//        List<Device> devices = deviceRepository.findAll();
        List<Device> devices = deviceRedisRepository.findAll();
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

    @Override
    public void modifyDeviceInfoById(DeviceDto deviceDto) {
        Device referenceById = deviceRepository.getReferenceById(deviceDto.getId());
        referenceById.setName(deviceDto.getName());
        referenceById.setCpuFrequency(deviceDto.getCpuFrequency());
        referenceById.setSram(deviceDto.getSram());
        referenceById.setFlash(deviceDto.getFlash());
        referenceById.setCpuArch(deviceDto.getCpuArch());
        deviceRepository.save(referenceById);
    }

    @Override
    public void addDevice(DeviceDto deviceDto) {
//        Integer count = deviceRepository.countDevice();

        Device device = ServerMapper.DeviceDtoToDevice(deviceDto, new Device());
//        device.setId((long) (count+1));
        deviceRepository.save(device);
    }

    @Override
    public DeviceDto parseInfo(String deviceInfo) {
        char[] chars = deviceInfo.toCharArray();
        DeviceDto deviceDto = new DeviceDto();
        int count = 0;
        for (int i=0;i<chars.length;i++) {
            if(Character.isDigit(chars[i])) {
                int j = i;
                StringBuilder sb = new StringBuilder();
                while(j< chars.length && Character.isDigit(chars[j])) {
                    sb.append(chars[j]);
                    j++;
                }
                i = j;
                String s = sb.toString();
                switch (count){
                    case 0:
                        deviceDto.setId(Long.parseLong(s));
                        break;
                    case 1:
                        deviceDto.setCpuFrequency(Long.parseLong(s));
                        break;
                    case 2:
                        deviceDto.setSram(Long.parseLong(s));
                        break;
                    case 3:
                        deviceDto.setFlash(Long.parseLong(s));
                        break;
                    case 4:
                        deviceDto.setFloatingPoint(Integer.parseInt(s));
                        break;
                }
                count++;
            }
        }
        return deviceDto;
    }

    @Override
    public DeviceDto findDeviceInfo(Long id) {

        DeviceDto deviceDto = deviceRedisRepository.findDeviceInfo(id);
        return deviceDto;
    }
}
