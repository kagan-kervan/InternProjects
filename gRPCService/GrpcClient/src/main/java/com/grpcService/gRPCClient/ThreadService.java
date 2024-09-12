package com.grpcService.gRPCClient;


import com.grpcService.gRPCClient.service.AlarmServerClientService;
import com.grpcService.gRPCClient.service.HelloWorldClientService;
import com.grpcService.gRPCClient.service.OltGetterClientService;
import com.grpcService.gRPCClient.service.PingServerClientService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.java.Log;
import org.acme.protos.GreeterGrpc;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final int NUMBER_OF_THREADS = 50;
    private static final int TOTAL_CALLS = 4800;
    private static final int NUMBER_OF_SERVER_APIS = 3;
    private static final int THREAD_SLEEP_VALUE = 1000;

    private final PingThread pingServerThread;



    public ThreadService(PingThread pingServerThread){

        this.pingServerThread = pingServerThread;
    }

    public void testThreading(int totalThreads, int totalCalls) throws InterruptedException {
        pingServerThread.activate();
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
        long[] creationTimes = new long[TOTAL_CALLS];
        HelloWorldClientService helloWorldClientService = new HelloWorldClientService();
        OltGetterClientService oltGetterClientService = new OltGetterClientService();
        PingServerClientService pingServerClientService = new PingServerClientService();
        AlarmServerClientService alarmServerClientService = new AlarmServerClientService();
        LoadThread helloWorldThread = new LoadThread("HelloWorldThread",THREAD_SLEEP_VALUE,helloWorldClientService);
        LoadThread oltGetterThread = new LoadThread("OltGetterThread",THREAD_SLEEP_VALUE,oltGetterClientService);
        LoadThread alarmThread = new LoadThread("AlarmServiceThread",THREAD_SLEEP_VALUE,alarmServerClientService);

        IntStream.range(0,totalCalls).forEach( i -> {
            Random random = new Random();
            int threadChoice = random.nextInt(NUMBER_OF_SERVER_APIS);
            creationTimes[i] = System.nanoTime();
            switch (threadChoice){
                case 0:
                    executorService.execute(() ->{
                        executeThread(helloWorldThread,creationTimes[i]);
                    });
                    return;
                case 1:
                    executorService.execute(() -> {
                        executeThread(oltGetterThread,creationTimes[i]);
                    });
                    return;
                case 2:
                    executorService.execute(() -> {
                        executeThread(alarmThread,creationTimes[i]);
                    });
                    return;
                default:
                    return;
            }
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
//        helloWorldThread.stop();
//        oltGetterThread.stop();
        pingServerThread.stop();
        //executorService.shutdownNow();
        log.info("hello world count: "+helloWorldThread.getRunCount());
        log.info("olt getter count: "+oltGetterThread.getRunCount());
        log.info("alarm server count: "+alarmThread.getRunCount());
        log.info("ping server count: "+pingServerThread.getRunCount());
        double helloWorldServerAverageWaiting = TimeUnit.NANOSECONDS.toMillis
                (helloWorldThread.getTotalWaitingTime()/ helloWorldThread.getRunCount());
        double oltGetterServerAverageWaiting = TimeUnit.NANOSECONDS.toMillis
                (oltGetterThread.getTotalWaitingTime()/ oltGetterThread.getRunCount());
        double alarmServerWaiting = TimeUnit.NANOSECONDS
                .toMillis(alarmThread.getTotalWaitingTime()/alarmThread.getRunCount());

        log.info("Hello world average waiting: " +helloWorldServerAverageWaiting+"ms");
        log.info("OLT getter average waiting: " +oltGetterServerAverageWaiting+"ms");
        log.info("Alarm server average waiting: "+alarmServerWaiting+"ms");
        pingServerThread.printExecutionTimes();
    }

    private void executeThread(LoadThread loadThread, long creationTime){
        loadThread.run();
        loadThread.addToTotalWaitingTime(loadThread.getStartingTime()-creationTime);
        loadThread.stop();
    }
}
