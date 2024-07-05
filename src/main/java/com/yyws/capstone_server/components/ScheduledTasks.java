package com.yyws.capstone_server.components;

import com.yyws.capstone_server.service.HttpService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * runs every five seconds to send the HTTP request.
 */
@Component
public class ScheduledTasks {

    private final HttpService httpService;

    public ScheduledTasks(HttpService httpService) {
        this.httpService = httpService;
    }

    @Scheduled(fixedRate = 5000)
    public void sendRequestToDevice() {
        httpService.sendRequestToEdgeDevice();

        System.out.println("request sent");
    }
}
