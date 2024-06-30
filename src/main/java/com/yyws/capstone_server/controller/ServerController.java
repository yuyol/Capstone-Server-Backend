package com.yyws.capstone_server.controller;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import jssc.SerialPortList;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

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


    /**
     * receive the device info from Arduino
     * @param deviceInfo
     * @return
     */
    @PostMapping("/device-info")
    public ResponseEntity<String> receiveDeviceInfo(@RequestBody String deviceInfo) {

        // 1. save the device info or update heartbeat time.
        serverService.devicePingServer(deviceInfo);

        DeviceDto deviceDto = serverService.parseInfo(deviceInfo);
        String modelName = deviceModelMap.get(String.valueOf(deviceDto.getId()));
        if (modelName != null) {
            return ResponseEntity.ok(modelName);
        }
        return ResponseEntity.ok("");
    }

    @GetMapping("/device-info")
    public ResponseEntity<Resource> receiveDeviceInfo(@RequestParam String fileName, @RequestParam String deviceInfo) {
        if (fileName == null || deviceInfo == null) throw new RuntimeException();
        try {
            String requestedModelName = null;
            DeviceDto deviceDto = serverService.parseInfo(deviceInfo);
            String deviceId = String.valueOf(deviceDto.getId());
            // 1. iterate map keys to match the device id
            for (String id : deviceModelMap.keySet()) {
                if(id.equals(deviceId)) requestedModelName = deviceModelMap.get(id);
            }
            if (requestedModelName == null) throw new RuntimeException();
            logger.info("Requested model: {}", requestedModelName);
            logger.info("Received file request for: {}", fileName);
            if (fileName.equals(requestedModelName)) {
                logger.info("Processing file: {}", fileName);

                Path filePath = Path.of("C:\\Users\\georg\\Desktop\\final_complete_server_model", fileName);
                Resource fileResource = new UrlResource(filePath.toUri());

                if (fileResource.exists() || fileResource.isReadable()) {
                    logger.info("File found: {}", filePath.toString());
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                            .body(fileResource);
                } else {
                    logger.error("File not found or not readable: {}", filePath.toString());
                    return ResponseEntity.notFound().build();
                }
            } else {
                logger.warn("No content for file: {}", fileName);
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            logger.error("Error processing file request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * receive request from frontend in time intervals
     *
     * @return
     */
    @GetMapping("/fetchConnectingDevices")
    public ResponseEntity<List<DeviceDto>> fetchConnectingDevices() {

        List<DeviceDto> liveDevices = serverService.checkDevicesHeartbeat();

        return ResponseEntity.status(HttpStatus.OK)
                .body(liveDevices);
    }

    @GetMapping("/fetchModels")
    public ResponseEntity<List<ModelDto>> fetchModels(@RequestParam String deviceId) {

        List<ModelDto> models = serverService.findAllModel();
//        if (deviceId.equals("7935192")) {
//            models = null;
//        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(models);
    }

//    @GetMapping("/fetchModels")
//    public ResponseEntity<List<ModelDto>> fetchModels(@RequestParam String deviceId) {
//        List<ModelDto> models = serverService.findAllModel();
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(models);
//    }


    // added
    private final AtomicReference<String> requestedModel = new AtomicReference<>(null);
    // store the device - model relationship
    private final Map<String, String> deviceModelMap = new HashMap<>();
    private final List<String> outputData = new CopyOnWriteArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @PostMapping("/ota-complete")
    public ResponseEntity<String> otaComplete(@RequestParam String deviceInfo) {
        DeviceDto deviceDto = serverService.parseInfo(deviceInfo);
        String deviceId = String.valueOf(deviceDto.getId());
        logger.info("OTA update completed, clearing requested model.");
        String removedModel = deviceModelMap.remove(deviceId);
        if (removedModel != null) {
            logger.info("Model has been removed successfully.");
        }
        return ResponseEntity.ok("Requested model cleared");
    }

    @GetMapping("/check-ota-status")
    public ResponseEntity<String> checkOtaStatus() {
        // Check if the deployment is complete
        boolean isOtaComplete = deviceModelMap.isEmpty(); // This logic may vary based on your actual implementation

        if (isOtaComplete) {
            return ResponseEntity.ok("OTA complete");
        } else {
            return ResponseEntity.ok("OTA in progress");
        }
    }

    @PostMapping("/send-output")
    public ResponseEntity<String> receiveOutput(@RequestParam String data) {
        synchronized (outputData) {
            outputData.add(data);
        }
        return ResponseEntity.ok("Data received");
    }

    @GetMapping("/get-output")
    public ResponseEntity<List<String>> getOutput() {
        synchronized (outputData) {
            return ResponseEntity.ok(new ArrayList<>(outputData));
        }
    }

    @PostMapping("/deploy")
    public ResponseEntity<String> deployModel(@RequestBody ModelDto modelDto, @RequestParam String deviceId) {
        if(modelDto == null || deviceId == null) throw new RuntimeException();
        System.out.println(deviceId);
        logger.info("Deploy request received for model: {}", modelDto.getName());
        requestedModel.set(modelDto.getName());

        deviceModelMap.put(deviceId, modelDto.getName());
        return ResponseEntity.ok("Deployment request recorded");
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestBody String deviceInfo) {
        logger.info("Received heartbeat with device info: {}", deviceInfo);

        try {
            serverService.devicePingServer(deviceInfo);
        } catch (Exception e) {
            logger.error("Exception while processing heartbeat: ", e);
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

}
