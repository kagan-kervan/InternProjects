package com.grpcService.gRPCClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GRpcClientApplication {

	public static void main(String[] args) {
        SpringApplication.run(GRpcClientApplication.class, args);
//		ThreadService service = new ThreadService();
//		try {
//			service.testThreading();
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
	}

}
