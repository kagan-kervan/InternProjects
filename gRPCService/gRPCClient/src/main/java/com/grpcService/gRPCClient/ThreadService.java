package com.grpcService.gRPCClient;


import com.grpcService.gRPCClient.ThreadData;
import com.grpcService.gRPCClient.service.HelloWorldClientService;
import com.grpcService.gRPCClient.service.OltGetterClientService;
import com.grpcService.gRPCClient.service.PingServerClientService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.java.Log;
import org.acme.protos.GreeterGrpc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@Log
public class ThreadService {

    //Constant variables for test
    private static final int NUMBER_OF_THREADS = 20;
    private static final int TOTAL_CALLS = 1000;
    private static final int NUMBER_OF_SERVER_APIS = 3;


    private ThreadLocal<ThreadData> localThreadData;
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub blockingStub;
    @GrpcClient("helloServer")
    private GreeterGrpc.GreeterStub asyncStub;




    public ThreadService(){
        //Create ThreadData instances for each thread.
        localThreadData = ThreadLocal.withInitial(ThreadData::new);
        log.info("Creating Stubs for gRPC servers...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newStub(channel);
    }

    public void startThreadingTest() throws InterruptedException {
        List<Long> submissionTimes = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        log.info("Starting gRPC call iterations..");
        IntStream.range(0,TOTAL_CALLS).forEach( i-> {
            long submissionTime = System.nanoTime();
            submissionTimes.add(submissionTime);
            executorService.submit(
                    () -> {
                        long startExecutionTime = System.nanoTime();
                        ThreadData threadData = localThreadData.get();
                        long waitingTime = startExecutionTime - submissionTime;
                        threadData.addToTotalWaitingTime(waitingTime);
                        log.info("Wait time: "+waitingTime+" ns");
                        threadData.incrementTaskCount();
                        //Do the server call thing.
                        log.info("Task with selected server is running on "+ Thread.currentThread().getName());
                        long finishExecutionTime = System.nanoTime();
                        long executionTime = finishExecutionTime-startExecutionTime;
                        threadData.addToExecutionTime(executionTime);
                        log.info("Working time = "+executionTime+" ns");
                    }
            );
        });
        //executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.submit( () -> {
           ThreadData threadData = localThreadData.get();
           log.info("Thread name: "+Thread.currentThread().getName()+"\t Informations:");
           log.info("Total task completed: "+threadData.getTaskCount());
           log.info("Average execution time: "+threadData.getAverageExecutionTime());
           log.info("Average waiting time: "+threadData.getAverageWaitingTime());
        });
    }

    public void testThreading() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        long[] creationTimes = new long[TOTAL_CALLS];
        HelloWorldClientService helloWorldClientService = new HelloWorldClientService();
        OltGetterClientService oltGetterClientService = new OltGetterClientService();
        PingServerClientService pingServerClientService = new PingServerClientService();
        LoadThread helloWorldThread = new LoadThread("HelloWorldThread",500,helloWorldClientService);
        LoadThread oltGetterThread = new LoadThread("OltGetterThread",500,oltGetterClientService);
        LoadThread pingServerThread = new LoadThread("PingServerThread",500,pingServerClientService);
        IntStream.range(0,TOTAL_CALLS).forEach( i -> {
            Random random = new Random();
            int threadChoice = random.nextInt(NUMBER_OF_SERVER_APIS);
            creationTimes[i] = System.nanoTime();
            switch (threadChoice){
                case 0:
                    executorService.execute(() ->{
                        helloWorldThread.run();
                        helloWorldThread.addToTotalWaitingTime((helloWorldThread.getStartingTime()-creationTimes[i]));
                        helloWorldThread.stop();
                    });
                    return;
                case 1:
                    executorService.execute(() -> {
                        oltGetterThread.run();
                        oltGetterThread.addToTotalWaitingTime((oltGetterThread.getStartingTime()-creationTimes[i]));
                        oltGetterThread.stop();
                    });
                    return;
                case 2:
                    executorService.execute(() -> {
                        pingServerThread.run();
                        pingServerThread.addToTotalWaitingTime((pingServerThread.getStartingTime()-creationTimes[i]));
                        pingServerThread.stop();
                    });
                    return;
                default:
                    return;
            }
        });
        executorService.awaitTermination(90,TimeUnit.SECONDS);
        helloWorldThread.stop();
        oltGetterThread.stop();
        pingServerThread.stop();
        double helloWorldServerAverageWaiting = TimeUnit.NANOSECONDS.toMillis
                ((helloWorldThread.getTotalWaitingTime()/ helloWorldThread.getRunCount()));
        double oltGetterServerAverageWaiting = TimeUnit.NANOSECONDS.toMillis
                (oltGetterThread.getTotalWaitingTime()/ oltGetterThread.getRunCount());
        double pingServerAverageWaiting = TimeUnit.NANOSECONDS.toMillis
                (pingServerThread.getTotalWaitingTime()/ pingServerThread.getRunCount());
        log.info("Hello world average waiting: " +helloWorldServerAverageWaiting+"ms");
        log.info("OLT getter average waiting: " +oltGetterServerAverageWaiting+"ms");
        log.info("Ping server average waiting: " +pingServerAverageWaiting+"ms");
    }

    private void executeThread(LoadThread loadThread){
        loadThread.run();
    }
}
