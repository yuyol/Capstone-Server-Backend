package com.yyws.capstone_server.service;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.repository.DeviceRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HttpService {

    private final RestTemplate restTemplate;
    private WebClient webClient;

    List<DeviceDto> devices = new ArrayList<>();

    Map<String, DeviceDto> deviceMap = new HashMap<>();

    ServerService serverService;

    @Autowired
    DeviceRedisRepository deviceRedisRepository;

    public HttpService(RestTemplate restTemplate, ServerService serverService, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.serverService = serverService;
        this.webClient = webClientBuilder.baseUrl("http://" + "192.168.1.106").build();
    }

    /**
     *
     */
    public Mono<String> sendRequestToEdgeDevice(){

        for (Map.Entry<String, DeviceDto> stringDeviceDtoEntry : deviceMap.entrySet()) {
            DeviceDto deviceDto = stringDeviceDtoEntry.getValue();
            String ipAddr = deviceDto.getIpAddr();
            // send request to ipAddr
            webClient.mutate().baseUrl("http://" + ipAddr).build();
            if (webClient == null) return null;
            return webClient.get()
                    .uri("/deviceInfo")
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> handleResponse(deviceDto, response))
                    .doOnError(e -> handleErrorFromDevice(deviceDto, e.getMessage()));
        }

        return null;

    }

    /**
     * Handle the response from device
     * update timestamp
     * @param deviceDto
     */
    public void handleResponse(DeviceDto deviceDto, String response) {
        System.out.println("receive from device:" + response);
        LocalDateTime now = LocalDateTime.now();

        // update heartbeat time
        deviceDto.setLastHeartBeat(now);
        deviceMap.put(String.valueOf(deviceDto.getId()), deviceDto);


    }

    /**
     * check if the last heartbeat time within the time limit
     * @param deviceDto
     * @param eMessage
     */
    public void handleErrorFromDevice(DeviceDto deviceDto, String eMessage) {
        System.err.println("Error while sending request to Arduino: " + eMessage);

        LocalDateTime now = LocalDateTime.now();
        // 2 minutes timeout
        Duration timeout = Duration.ofMinutes(2);

        if (deviceDto.getLastHeartBeat() != null) {
            Duration durationSinceLastHeartbeat = Duration.between(deviceDto.getLastHeartBeat(), now);
            // if exceed the time limit, remove it from the map
            if (durationSinceLastHeartbeat.compareTo(timeout) > 0) {
                deviceMap.remove(String.valueOf(deviceDto.getId()));
            }
        }
    }

    public void receiveFromDevice(String deviceInfo, String ipAddress) {
        DeviceDto deviceDto = serverService.parseInfo(deviceInfo);
        deviceDto.setIpAddr(ipAddress);

        // setup heartbeat
        deviceDto.setLastHeartBeat(LocalDateTime.now());

        // Once a device connected, add to the map
        deviceMap.put(String.valueOf(deviceDto.getId()), deviceDto);
        System.out.println(deviceMap);
        deviceRedisRepository.saveDeviceAsServer(deviceDto);
    }
}
