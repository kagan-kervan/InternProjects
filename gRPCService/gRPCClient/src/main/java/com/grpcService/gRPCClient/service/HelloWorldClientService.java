package com.grpcService.gRPCClient.service;


import com.google.common.base.Verify;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import org.acme.GreetingResource;
import org.acme.protos.GreeterGrpc;
import org.acme.protos.HelloReply;
import org.acme.protos.HelloRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log
public class HelloWorldClientService implements ServiceCaller {
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub blockingStub;
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub asyncStub;


    public HelloWorldClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newStub(channel);
    }

    public void ReceiveHello(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        blockingStub.sayHello(request, new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply helloReply) {
                log.info("Received Message: "+helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error for request " + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.info("HelloWorld Service request completed successfully");
            }
        });
    }

    @Override
    public void call() {
        ReceiveHello("ServiceTest");
    }
}
