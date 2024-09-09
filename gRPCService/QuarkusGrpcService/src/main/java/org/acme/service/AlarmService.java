package org.acme.service;


import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.acme.protos.AlarmRequest;
import org.acme.protos.AlarmResponse;
import org.acme.protos.AlarmServiceGrpc;
import org.acme.protos.HelloReply;

@GrpcService
public class AlarmService extends AlarmServiceGrpc.AlarmServiceImplBase {
    @Override
    public void getUserDetail(AlarmRequest request, StreamObserver<AlarmResponse> responseObserver) {
        String name = request.getName();
        String message = "Details of " + name;
        responseObserver.onNext(AlarmResponse.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }
}
