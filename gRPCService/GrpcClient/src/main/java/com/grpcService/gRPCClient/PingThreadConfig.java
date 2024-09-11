package com.grpcService.gRPCClient;

import com.grpcService.gRPCClient.service.PingServerClientService;
import com.grpcService.gRPCClient.service.ServiceCaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PingThreadConfig {
    @Bean
    public PingThread pingThread() {
        PingServerClientService serviceCaller = new PingServerClientService();
        return new PingThread("PingServerThread", 4000, serviceCaller);
    }
}

