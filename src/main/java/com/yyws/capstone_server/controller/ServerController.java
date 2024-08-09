package com.yyws.capstone_server.controller;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.dto.DeviceModelDto;
import com.yyws.capstone_server.dto.ModelDto;
import com.yyws.capstone_server.entity.DeployRecord;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.UserDeviceRelation;
import com.yyws.capstone_server.service.HttpService;
import com.yyws.capstone_server.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import jssc.SerialPortList;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@CrossOrigin
public class ServerController {


    ServerService serverService;

    HttpService httpService;

    public ServerController(ServerService serverService, HttpService httpService) {
        this.serverService = serverService;
        this.httpService = httpService;
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

        return ResponseEntity.status(HttpStatus.OK)
                .body(models);
    }


    /**********************       OTA Process      **********************/

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


    /**************************          Arduino Converter          ****************************/
    private final String arduinoCliPath = "C:\\Users\\georg\\AppData\\Local\\Programs\\Arduino IDE\\resources\\app\\lib\\backend\\resources\\arduino-cli.exe";

    private final String uploadDir = System.getProperty("user.home") + "/Desktop/uploads/";
    private final String outputDir = System.getProperty("user.home") + "/Desktop/output/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    @PostMapping("/convert")
    public ResponseEntity<Resource> convertSketch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            // Ensure the upload directory exists
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
                LOGGER.info("Created upload directory at {}", uploadDir);
            }

            // Save the uploaded file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.endsWith(".ino")) {
                LOGGER.error("Uploaded file is not a valid Arduino sketch");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Create a unique directory with the same name as the sketch (without the .ino extension)
            String sketchName = UUID.randomUUID().toString();
            File sketchDirectory = new File(uploadDir + sketchName);
            if (!sketchDirectory.exists()) {
                sketchDirectory.mkdirs();
            }

            // Save the file in this directory with the same name as the directory
            File uploadFile = new File(sketchDirectory, sketchName + ".ino");
            file.transferTo(uploadFile);
            LOGGER.info("Saved uploaded file to {}", uploadFile.getAbsolutePath());

            // Ensure the output directory exists
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
                LOGGER.info("Created output directory at {}", outputDir);
            }

            // Install necessary core for the ESP32-S3 board
            ProcessBuilder coreInstallPb = new ProcessBuilder(arduinoCliPath, "core", "install", "esp32:esp32");
            coreInstallPb.redirectErrorStream(true);
            Process coreInstallProcess = coreInstallPb.start();
            StringBuilder coreInstallOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(coreInstallProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    coreInstallOutput.append(line).append("\n");
                }
            }
            int coreInstallExitCode = coreInstallProcess.waitFor();
            if (coreInstallExitCode != 0) {
                LOGGER.error("Arduino CLI core install process exited with code {}", coreInstallExitCode);
                LOGGER.error("Arduino CLI core install output: \n{}", coreInstallOutput.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            LOGGER.info("Installed necessary core using Arduino CLI");

            // Compile the sketch using Arduino CLI
            ProcessBuilder pb = new ProcessBuilder(arduinoCliPath, "compile",
                    "--fqbn", "esp32:esp32:esp32s3",
                    "--output-dir", outputDir,
                    "--build-property", "build.extra_flags=-DARDUINO=10810",
                    sketchDirectory.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Capture and log the output of the Arduino CLI process
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.error("Arduino CLI process exited with code {}", exitCode);
                LOGGER.error("Arduino CLI output: \n{}", output.toString());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            LOGGER.info("Compiled sketch using Arduino CLI");

            // Retrieve the binary file
            String binFileName = sketchName + ".ino.bin";
            Path binFilePath = Paths.get(outputDir, binFileName);
            if (!Files.exists(binFilePath)) {
                LOGGER.error("Binary file {} does not exist", binFilePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            Resource resource = new FileSystemResource(binFilePath.toFile());

            // Clean up temporary files
            Files.delete(uploadFile.toPath());
            LOGGER.info("Deleted temporary file {}", uploadFile.getAbsolutePath());

            // Return the binary file
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binFileName + "\"")
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error processing file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/api/convert")
    public ResponseEntity<InputStreamResource> convertModel(@RequestParam("file") MultipartFile file) {
        String inputPath = "uploaded_model.h5";
        String outputPath = "model.tflite";
        String scriptPath = "src/main/resources/scripts/convert_model.py";
        try {
            // Save the uploaded file to disk
            File inputFile = new File(inputPath);
            file.transferTo(inputFile);

            // Execute the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, inputPath, outputPath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(500).body(null);
            }

            // Return the converted file
            File convertedFile = new File(outputPath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(convertedFile));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=model.tflite");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(convertedFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /*********************               Device as Server                 **********************/



    /**
     * Receive device info and device IP address from device
     * @param deviceInfo
     * @param IpAddress
     * @return
     */
    @GetMapping("/receiveFromDevice")
    public ResponseEntity<String> receiveFromDevice(@RequestParam String deviceInfo,@RequestParam String IpAddress) {

        httpService.receiveFromDevice(deviceInfo, IpAddress);
        System.out.println(IpAddress);

        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }

    /**
     * Receive from frontend
     * @return
     */
    @GetMapping("/receiveFromFrontend")
    public ResponseEntity<List<DeviceDto>> receiveFromFrontend() {

        List<DeviceDto> devices = httpService.findLiveDevices();

        return ResponseEntity.status(HttpStatus.OK)
                .body(devices);
    }



    /*********************               After Login                 **********************/



    @PostMapping("/registerDevice")
    public ResponseEntity<String> registerDevice(@RequestBody UserDeviceRelation relation) {

        String email = relation.getEmail();
        String deviceId = relation.getDeviceId();
        serverService.registerDevice(email, deviceId);
        System.out.println(email+deviceId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }

    @GetMapping("/searchOwnedDevices")
    public ResponseEntity<List<DeviceDto>> searchOwnedDevices(@RequestParam String email) {

        List<DeviceDto> devices = serverService.searchOwnedDevices(email);
//        List<DeviceDto> devices = null;
        System.out.println(devices);

        return ResponseEntity.status(HttpStatus.OK)
                .body(devices);
    }

    @GetMapping("/fetchConnectingDevicesLogin")
    public ResponseEntity<List<DeviceDto>> fetchConnectingDevicesLogin(@RequestParam String email) {

        List<DeviceDto> liveDevices = serverService.checkDevicesHeartbeatLogin(email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(liveDevices);
    }

    @PostMapping("/deleteDeviceFromUser")
    public ResponseEntity<String> deleteDeviceFromUser(@RequestBody UserDeviceRelation relation) {


        System.out.println(relation);
        serverService.deleteDeviceFromUser(relation);
        return ResponseEntity.status(HttpStatus.OK)
                .body("OK");
    }


    @GetMapping("/searchDeployRecord")
    public ResponseEntity<List<DeployRecord>> searchDeployRecord(@RequestParam String email) {

        List<DeployRecord> records = serverService.searchDeployRecord(email);

        System.out.println(records);
        return ResponseEntity.status(HttpStatus.OK)
                .body(records);
    }

}
