package com.grpcService.gRPCClient.controller;


import com.grpcService.gRPCClient.ThreadService;
import jakarta.ws.rs.Path;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadController {

    private ThreadService threadService;

    public ThreadController(ThreadService threadService){
        this.threadService = threadService;
    }

    @GetMapping(path = "/thread-start/")
    public void startThreading(){
        try {
            threadService.testThreading();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
