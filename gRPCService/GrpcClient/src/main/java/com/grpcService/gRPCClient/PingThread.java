package com.grpcService.gRPCClient;

import com.grpcService.gRPCClient.service.ServiceCaller;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Log
@Component
public class PingThread implements Runnable{

    private String name;
    private long sleepMiliSeconds;
    private ServiceCaller serviceCaller;
    private boolean running=false;
    private int runCount = 0;
    private long totalWaitingTime;
    private long startingTime;
    public ArrayList<Long> executionTimes;

    public long getStartingTime() {
        return startingTime;
    }

    public boolean isRunning(){return running;}

    public int getRunCount(){return runCount;}
    public void incrementRunCount(){
        runCount = runCount + 1;
    }

    public PingThread(String name, long sleepMiliseconds, ServiceCaller serviceCaller) {
        this.name = name;
        this.sleepMiliSeconds = sleepMiliseconds;
        this.serviceCaller = serviceCaller;
        this.executionTimes = new ArrayList<>();

    }
    @Override
    @Scheduled(fixedRate = 4000)
    public void run() {
        if(!isRunning())
            return;
        startingTime = System.nanoTime();
        log.info("Thread "+name+" started..");
        serviceCaller.call();
        long finishTime = System.nanoTime();
        executionTimes.add((finishTime-startingTime)); // Keep the execution times
        incrementRunCount();
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }
    public void addToTotalWaitingTime(long amount){
        totalWaitingTime = totalWaitingTime +amount;
    }
    public void stop() {
        this.running = false;
        log.info("Stop the ping thread...");
    }

    public void activate(){
        this.running = true;
        this.executionTimes.clear();
        log.info("Start the ping thread...");
    }

    public void printExecutionTimes(){
        for (int i = 0; i < executionTimes.size(); i++) {
            log.info("Execution Time "+i+": "+executionTimes.get(i)+" ns");
        }
        executionTimes.sort(Comparator.naturalOrder());
        log.info("Shortest Time: "+((double)executionTimes.get(0)/1000000)+" ms");
        log.info("Longest Time: "+((double) executionTimes.get(executionTimes.size()-1)/1000000)+" ms");
        log.info("Mean Execution Time: "+calculateMean()+" ms");
        log.info("Standard Deviation: "+calculateStandardDeviation()+" ms");
    }
    public double calculateMean() {
        double sum = 0;
        for (Long time : executionTimes) {
            sum += (double)time/1000000;
        }
        return  sum / executionTimes.size();
    }

    public double calculateStandardDeviation() {
        double mean = calculateMean();
        double sumSquaredDifferences = 0;

        for (Long time : executionTimes) {
            sumSquaredDifferences += Math.pow(((double)time/1000000)- mean, 2);
        }

        return Math.sqrt(sumSquaredDifferences / executionTimes.size());
    }

}
