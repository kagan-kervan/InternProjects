package com.grpcService.gRPCClient;

import com.grpcService.gRPCClient.service.ServiceCaller;
import lombok.extern.java.Log;

@Log
public class LoadThread implements Runnable{

    private String name;
    private long sleepMiliSeconds;
    private ServiceCaller serviceCaller;
    private boolean running=true;
    private int runCount = 0;
    private long totalWaitingTime;
    private long startingTime;

    public long getStartingTime() {
        return startingTime;
    }

    public boolean isRunning(){return running;}

    public int getRunCount(){return runCount;}
    public void incrementRunCount(){
        runCount = runCount + 1;
    }

    public LoadThread(String name, long sleepMiliseconds, ServiceCaller serviceCaller) {
        this.name = name;
        this.sleepMiliSeconds = sleepMiliseconds;
        this.serviceCaller = serviceCaller;

    }
    @Override
    public void run() {
        try {
            log.info("Thread " + name + " started.");
            serviceCaller.call();
            startingTime = System.nanoTime();
            Thread.sleep(sleepMiliSeconds);
            incrementRunCount();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }
    public void addToTotalWaitingTime(long amount){
        totalWaitingTime = totalWaitingTime +amount;
    }
    public void stop() {
        this.running = false;
        log.info("Stop the thread...");
    }
}
