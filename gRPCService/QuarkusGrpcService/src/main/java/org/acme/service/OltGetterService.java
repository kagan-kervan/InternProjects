package org.acme.service;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import lombok.Builder;
import lombok.extern.java.Log;
import org.acme.protos.OltGetterGrpc;
import org.acme.protos.OltRequest;
import org.acme.protos.OltResponse;

@GrpcService
@Log
public class OltGetterService extends OltGetterGrpc.OltGetterImplBase {
    @Override
    @Blocking
    public void getOlt(OltRequest request, StreamObserver<OltResponse> responseObserver) {
        log.info("Started olt request..");
        long startTime = System.currentTimeMillis();
        String newOltMessage = "Sending OLT "+request.getOltName()+" to Client...";
        OltResponse oltResponse = OltResponse.newBuilder().setMessage(newOltMessage).build();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        responseObserver.onNext(oltResponse);
        long finishTime = System.currentTimeMillis();
        log.info("Execution Time: "+(finishTime-startTime)+"ms");
        responseObserver.onCompleted();
    }
}
