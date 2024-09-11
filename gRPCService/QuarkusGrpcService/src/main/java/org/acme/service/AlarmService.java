package org.acme.service;


import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import lombok.extern.java.Log;
import org.acme.protos.AlarmRequest;
import org.acme.protos.AlarmResponse;
import org.acme.protos.AlarmServiceGrpc;
import org.acme.protos.HelloReply;

@GrpcService
@Log
public class AlarmService extends AlarmServiceGrpc.AlarmServiceImplBase {
    @Override
    @Blocking()
    public void getUserDetail(AlarmRequest request, StreamObserver<AlarmResponse> responseObserver) {
        log.info("Started alarm request..");
        long startTime = System.currentTimeMillis();
        String name = request.getName();
        String message = "Details of " + name;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        responseObserver.onNext(AlarmResponse.newBuilder().setMessage(message).build());
        long finishTime = System.currentTimeMillis();
        log.info("Execution Time: "+(finishTime-startTime)+"ms");
        responseObserver.onCompleted();
    }
}
