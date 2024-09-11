package com.grpcService.gRPCClient.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.java.Log;
import org.acme.protos.AlarmRequest;
import org.acme.protos.AlarmResponse;
import org.acme.protos.AlarmServiceGrpc;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.springframework.stereotype.Service;

@Log
@Service
public class AlarmServerClientService implements ServiceCaller{
    @GrpcClient("Alarm-Service")
    private AlarmServiceGrpc.AlarmServiceStub asyncAlarmStub;
    @GrpcClient("Alarm-Service")
    private AlarmServiceGrpc.AlarmServiceBlockingStub syncAlarmStub;

    public AlarmServerClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        asyncAlarmStub = AlarmServiceGrpc.newStub(channel);
        syncAlarmStub = AlarmServiceGrpc.newBlockingStub(channel);
    }
    @Override
    public void call() {
        log.info("Started Alarm client ..");
        AlarmRequest request = AlarmRequest.newBuilder().setName("TestUser").build();
        syncCall(request);
    }

    private void asyncCall(AlarmRequest request){
        asyncAlarmStub.getUserDetail(request, new StreamObserver<AlarmResponse>() {
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

    private void syncCall(AlarmRequest alarmRequest){
        log.info("Message: "+syncAlarmStub.getUserDetail(alarmRequest).getMessage());
    }
}
