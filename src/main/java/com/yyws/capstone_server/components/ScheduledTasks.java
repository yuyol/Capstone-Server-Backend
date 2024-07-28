package com.yyws.capstone_server.components;

import com.yyws.capstone_server.service.HttpService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
//        httpService.sendRequestToEdgeDevice();

        Mono<String> responseMono = httpService.sendRequestToEdgeDevice();
        try {
            responseMono.subscribe(
                    response -> {
                        // Handle the response if needed
                    },
                    error -> {
                        // Handle the error if needed
                    }
            );
        } catch (NullPointerException e) {

        }

    }
}
