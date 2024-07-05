package com.yyws.capstone_server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {

    private final RestTemplate restTemplate;

    public HttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendRequestToEdgeDevice() {
//        String deviceUrl = "http://<device-ip-address>:<port>/endpoint"; // Replace with the actual URL
//        try {
//            String response = restTemplate.getForObject(deviceUrl, String.class);
//            // Process the response if needed
//            System.out.println("Response from device: " + response);
//        } catch (Exception e) {
//            // Handle the exception
//            System.err.println("Error while sending request to device: " + e.getMessage());
//        }
    }
}
