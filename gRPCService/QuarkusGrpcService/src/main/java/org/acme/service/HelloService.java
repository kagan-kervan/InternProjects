package org.acme.service;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;
import org.acme.protos.GreeterGrpc;
import org.acme.protos.HelloReply;
import org.acme.protos.HelloRequest;

@GrpcService
@Log
public class HelloService extends GreeterGrpc.GreeterImplBase {


    @Override
    @Blocking
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("Started hello request..");
        long startTime = System.currentTimeMillis();
        String name = request.getName();
        String message = "Hello " + name;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());
        long finishTime = System.currentTimeMillis();
        log.info("Execution Time: "+(finishTime-startTime)+"ms");
        responseObserver.onCompleted();
    }
}
