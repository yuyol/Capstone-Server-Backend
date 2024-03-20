package com.yyws.capstone_server.controller;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.service.ServerService;
import org.springframework.core.io.Resource;
import jssc.SerialPortList;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@CrossOrigin
public class ServerController {

    ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping("/hello")
    public void Hello() {
        System.out.println("hello");
    }

    @GetMapping("/findAllDevice")
    public ResponseEntity<List<DeviceDto>> findAllDevice() {
        List<DeviceDto> deviceDto = serverService.findAllDevice();
        return ResponseEntity.status(HttpStatus.OK)
                .body(deviceDto);
    }
    
    @GetMapping("/findAllModel")
    public ResponseEntity<List<ModelDto>> findAllModel() {
        List<ModelDto> modelDto = serverService.findAllModel();
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelDto);
    }

    @GetMapping("/findAllDeviceAndModel")
    public ResponseEntity<DeviceModelDto> findAllDeviceAndModel() {
        DeviceModelDto deviceModelDto = serverService.findAllDeviceAndModel();
        return ResponseEntity.status(HttpStatus.OK)
                .body(deviceModelDto);
    }

    @PostMapping("/modifyDeviceInfo")
    public ResponseEntity<String> modifyDeviceInfo(@RequestParam Long id,
                                                   @RequestParam String name,
                                                   @RequestParam long cpuFrequency,
                                                   @RequestParam long sram,
                                                   @RequestParam long flash,
                                                   @RequestParam String cpuArch) {

        System.out.println(cpuFrequency);
        DeviceDto deviceDto = new DeviceDto(id,name,cpuFrequency,sram,flash,cpuArch);

        serverService.modifyDeviceInfoById(deviceDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("OK");
    }

    @PostMapping("/findDeviceInfo")
    public ResponseEntity<DeviceDto> findDeviceInfo(@RequestParam Long id) {

        DeviceDto deviceDto = serverService.findDeviceInfo(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(deviceDto);
    }

    @GetMapping("/fetchConnectedDevice")
    public ResponseEntity<String> fetchConnectedDevice() {
        String[] portNames = SerialPortList.getPortNames();
        for(String portName : portNames) {
            System.out.println(portName);
            // You may want to attempt to connect here to verify if it's an Arduino.
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }

    @PostMapping("/addDevice")
    public ResponseEntity<String> addDevice(@RequestParam String name,
                                            @RequestParam long cpuFrequency,
                                            @RequestParam long sram,
                                            @RequestParam long flash,
                                            @RequestParam String cpuArch) {
        DeviceDto deviceDto = new DeviceDto(name,cpuFrequency,sram,flash,cpuArch);

        serverService.addDevice(deviceDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        System.out.println("test");

        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }


    @PostMapping("/device-info")
    public ResponseEntity receiveDeviceInfo(@RequestBody String deviceInfo) {
        // Process the device information received from the Arduino device
//        System.out.println("Received device information: " + deviceInfo);

        // You can parse the device information and store it in your database, perform some actions, etc.
        // 1. parse info
        DeviceDto deviceDto = serverService.parseInfo(deviceInfo);
        System.out.println(deviceDto.toString());

        // 2. find file path from redis


        // file test
        try {
            // Specify the file location here
            Path filePath = Path.of("D:\\programming\\project\\capstone\\files\\test.txt");
            Resource fileResource = new UrlResource(filePath.toUri());

            if (fileResource.exists() || fileResource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                        .body(fileResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        // Send a response back to the Arduino device if needed
//        return "Device information received successfully";
    }

}
