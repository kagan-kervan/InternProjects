package com.grpcService.gRPCClient;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.grpc.GrpcClient;
import jakarta.inject.Inject;
import org.acme.GreetingResource;
import org.acme.protos.GreeterGrpc;
import org.acme.protos.HelloRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldClientService {
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterBlockingStub blockingStub;


    public HelloWorldClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public String ReceiveHello(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        return blockingStub.sayHello(request).getMessage();
    }

}
