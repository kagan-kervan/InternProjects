package org.acme;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.acme.protos.GreeterGrpc;

@ApplicationScoped
public class GrpcClientProducer {

    @Produces
    @Singleton
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
    }

    @Produces
    @Singleton
    public GreeterGrpc.GreeterBlockingStub blockingStub(ManagedChannel channel) {
        return GreeterGrpc.newBlockingStub(channel);
    }
}
