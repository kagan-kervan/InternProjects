package com.grpcService.gRPCClient.service;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.java.Log;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.acme.protos.PingRequest;
import org.acme.protos.PingResponse;
import org.acme.protos.PingServerGrpc;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Log
@Primary
public class PingServerClientService implements ServiceCaller {

    @GrpcClient("pingServer")
    private PingServerGrpc.PingServerStub pingServerStub;

    @GrpcClient("pingServer")
    private PingServerGrpc.PingServerBlockingStub syncServerStub;

    public PingServerClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        pingServerStub = PingServerGrpc.newStub(channel);
        channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        syncServerStub = PingServerGrpc.newBlockingStub(channel);
    }
    @Override
    public void call() {
        PingRequest request = PingRequest.newBuilder().setName("Client").build();
        log.info("Started Ping client ..");
        syncCall(request);
    }

    private void syncCall(PingRequest request){
        log.info("Message: "+syncServerStub.sendPing(request).getMessage());
    }

    private void asyncCall(PingRequest request){
        pingServerStub.sendPing(request, new StreamObserver<PingResponse>() {
            @Override
            public void onNext(PingResponse pingResponse) {
                log.info("Received message: "+pingResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("Error for request " + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.info("PingServer Service request completed successfully");
            }
        });
    }
}
