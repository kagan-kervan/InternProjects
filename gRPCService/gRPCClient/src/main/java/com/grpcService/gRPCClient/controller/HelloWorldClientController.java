package com.grpcService.gRPCClient.controller;


import com.grpcService.gRPCClient.service.HelloWorldClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HelloWorldClientController {

    private final HelloWorldClientService clientService;

     public HelloWorldClientController(HelloWorldClientService clientService){
         this.clientService = clientService;
     }

     @GetMapping(path = "/hello/")
    public void getGreeting(@RequestParam String name){
         clientService.ReceiveHello(name);
     }

     @GetMapping(path = "/thread/")
    public void threadTesting(){
         ExecutorService service = Executors.newFixedThreadPool(200);
         for (int i = 0; i < 100; i++) {
             int taskNumber  = i;
             service.submit(() ->{
                 System.out.println("Task "+ taskNumber + " is running on thread "+ Thread.currentThread().getName());
             });
         }
         service.shutdown();
     }
}
