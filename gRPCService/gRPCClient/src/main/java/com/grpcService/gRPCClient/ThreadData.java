package com.grpcService.gRPCClient;

public class ThreadData {
    private int taskCount;
    private long executionTime;
    private long totalWaitingTime;

    //Constructor might cause problems
    public ThreadData(){
        this.taskCount = 0;
        this.executionTime = 0;
        this.totalWaitingTime = 0;
    }

    public void incrementTaskCount(){
        taskCount = taskCount + 1;
    }

    public void addToExecutionTime(long iterationExecutionTime){
        executionTime = executionTime + iterationExecutionTime;
    }
    public void addToTotalWaitingTime(long singleWaitTime){
        totalWaitingTime = totalWaitingTime + singleWaitTime;
    }
    public int getTaskCount(){
        return taskCount;
    }
    public long getExecutionTime(){
        return executionTime;
    }
    public double getAverageExecutionTime(){
        return getExecutionTime()/getTaskCount();
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getAverageWaitingTime(){
        return getTotalWaitingTime()/getTaskCount();
    }
}
