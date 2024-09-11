package com.grpcService.gRPCClient.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.java.Log;
import org.acme.protos.OltGetterGrpc;
import org.acme.protos.OltRequest;
import org.acme.protos.OltResponse;
import org.springframework.stereotype.Service;

@Service
@Log
public class OltGetterClientService implements ServiceCaller{
    @GrpcClient("oltGetterService")
    private OltGetterGrpc.OltGetterStub asyncOltGetterStub;
    @GrpcClient("oltGetterService")
    private OltGetterGrpc.OltGetterBlockingStub syncOltGetterStub;

    public OltGetterClientService(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        asyncOltGetterStub = OltGetterGrpc.newStub(channel);
        syncOltGetterStub = OltGetterGrpc.newBlockingStub(channel);
    }

    @Override
    public void call() {
        OltRequest oltRequest = OltRequest.newBuilder().setOltName("SPON-10730").build();
        log.info("Started OLT client ..");
        syncCall(oltRequest);
    }

    private void asyncCall(OltRequest oltRequest){
        asyncOltGetterStub.getOlt(oltRequest, new StreamObserver<OltResponse>() {
            @Override
            public void onNext(OltResponse oltResponse) {
                log.info("Received message : "+oltResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error for request " + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                log.info("OltGetter Service request completed successfully");
            }
        });
    }

    private void syncCall(OltRequest oltRequest){
        log.info("Message: "+syncOltGetterStub.getOlt(oltRequest).getMessage());
    }

}
