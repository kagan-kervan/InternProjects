package org.acme.service;


import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.acme.protos.PingRequest;
import org.acme.protos.PingResponse;
import org.acme.protos.PingServerGrpc;

@GrpcService
public class PingServerService extends PingServerGrpc.PingServerImplBase {
    @Override
    public void sendPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        String newPingMessage = "Sending ping to : "+request.getName();
        PingResponse pingResponse = PingResponse.newBuilder().setMessage(newPingMessage).build();
        responseObserver.onNext(pingResponse);
        responseObserver.onCompleted();
    }
}
