package com.grpcService.gRPCClient;


import io.quarkus.grpc.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldClientController {

    private final HelloWorldClientService clientService;

     public HelloWorldClientController(HelloWorldClientService clientService){
         this.clientService = clientService;
     }

     @GetMapping(path = "/hello/")
    public String getGreeting(@RequestParam String name){
         return clientService.ReceiveHello(name);
     }
}
