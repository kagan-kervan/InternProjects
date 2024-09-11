package org.acme.service;


import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import lombok.extern.java.Log;
import org.acme.protos.PingRequest;
import org.acme.protos.PingResponse;
import org.acme.protos.PingServerGrpc;

@GrpcService
@Log
public class PingServerService extends PingServerGrpc.PingServerImplBase {
    @Override
    public void sendPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        log.info("Started ping request..");
        long startTime = System.currentTimeMillis();
        String newPingMessage = "Sending ping to : "+request.getName();
        PingResponse pingResponse = PingResponse.newBuilder().setMessage(newPingMessage).build();
        responseObserver.onNext(pingResponse);
        long finishTime = System.currentTimeMillis();
        log.info("Execution Time: "+(finishTime-startTime)+"ms");
        responseObserver.onCompleted();
    }
}
