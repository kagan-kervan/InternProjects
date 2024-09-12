package com.grpcService.gRPCClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableScheduling
public class GRpcClientApplication {

	public static void main(String[] args) {
        SpringApplication.run(GRpcClientApplication.class, args);
	}

}
