package com.hva.oolbekd;

public class Result {
    private int numberOfValues;
    private long timeTookPrallel;
    private long timeTookSequential;
    private int nThreads;

    public Result(int numberOfValues, int nThreads, long timeTookPrallel, long timeTookSequential) {
        this.numberOfValues = numberOfValues;
        this.timeTookPrallel = timeTookPrallel;
        this.timeTookSequential = timeTookSequential;
        this.nThreads = nThreads;
    }

    public int getNumberOfValues() {
        return numberOfValues;
    }

    public long getTimeTookPrallel() {
        return timeTookPrallel;
    }

    public long getTimeTookSequential() {
        return timeTookSequential;
    }

    public int getnThreads() {
        return nThreads;
    }

    @Override
    public String toString() {
        return String.format("Count: %d, Threads: %d, Time parallel: %d, Time sequential: %d", this.numberOfValues, this.nThreads, this.timeTookPrallel, this.timeTookSequential);
    }
}
