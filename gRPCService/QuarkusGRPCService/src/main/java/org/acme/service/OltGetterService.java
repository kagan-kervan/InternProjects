package org.acme.service;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.acme.protos.OltGetterGrpc;
import org.acme.protos.OltRequest;
import org.acme.protos.OltResponse;

@GrpcService
public class OltGetterService extends OltGetterGrpc.OltGetterImplBase {
    @Override
    public void getOlt(OltRequest request, StreamObserver<OltResponse> responseObserver) {
        String newOltMessage = "Sending OLT "+request.getOltName()+" to Client...";
        OltResponse oltResponse = OltResponse.newBuilder().setMessage(newOltMessage).build();
        responseObserver.onNext(oltResponse);
        responseObserver.onCompleted();
    }
}
