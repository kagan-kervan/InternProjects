package org.acme;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.protos.Greeter;
import org.acme.protos.GreeterGrpc;
import org.acme.protos.HelloReply;
import org.acme.protos.HelloRequest;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/hello")
public class GreetingResource {

    @Inject
    GreetingsService service;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    @Inject
    public GreetingResource(GreeterGrpc.GreeterBlockingStub greeterBlockingStub){
        this.blockingStub = greeterBlockingStub;
    }
    @GET
    @Path("/grpc/{name}")
    public String HelloFromGRPC(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply reply = blockingStub.sayHello(request);
        return reply.getMessage();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@RestQuery("name") String name) {
        return "Hello from Quarkus "+ name;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public String Greeting(String name){
        return service.greeting(name);
    }

}
