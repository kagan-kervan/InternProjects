package com.grpcService.gRPCClient.service;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.java.Log;
import org.acme.protos.GreeterGrpc;
import org.acme.protos.HelloReply;
import org.acme.protos.HelloRequest;
import org.springframework.stereotype.Service;

@Service
@Log
public class HelloWorldClientService implements ServiceCaller {
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterBlockingStub blockingStub;
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub asyncStub;


    public HelloWorldClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void receiveHello(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        log.info("Started Hello client ..");
        log.info("Message: "+blockingStub.sayHello(request).getMessage());
    }

    @Override
    public void call() {
        receiveHello("ServiceTest");
    }
}
