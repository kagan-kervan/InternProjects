package com.grpcService.gRPCClient.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import lombok.extern.java.Log;
import org.acme.protos.AlarmRequest;
import org.acme.protos.AlarmResponse;
import org.acme.protos.AlarmServiceGrpc;
import org.springframework.stereotype.Service;

@Log
@Service
public class AlarmServerClientService implements ServiceCaller{
    @GrpcClient("Alarm-Service")
    private AlarmServiceGrpc.AlarmServiceStub serviceStub;

    public AlarmServerClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        serviceStub = AlarmServiceGrpc.newStub(channel);
    }
    @Override
    public void call() {
        AlarmRequest request = AlarmRequest.newBuilder().setName("TestUser").build();
        serviceStub.getUserDetail(request, new StreamObserver<AlarmResponse>() {
            @Override
            public void onNext(AlarmResponse alarmResponse) {
                log.info("Received Message: "+alarmResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("Error for request " + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.info("Alarm Service responded correctly.. ");
            }
        });
    }
}
