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
    private GreeterGrpc.GreeterStub blockingStub;
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub asyncStub;


    public HelloWorldClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newStub(channel);
    }

    public void receiveHello(String name){
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
        receiveHello("ServiceTest");
    }
}
