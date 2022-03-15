package com.hva.oolbekd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        int nThreads = 8;
        ArrayList<Result> results = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            int size = i * 100_000;

            int[] s = new int[size];
            for (int k = 0; k < s.length; k++) {
                s[k] = s.length - k;
            }

            int[] p = new int[size];
            for (int k = 0; k < p.length; k++) {
                p[k] = p.length - k;
            }

            int[] pSorted;

            // Parallel insertion sort
            long startTime = System.currentTimeMillis();
            try {
                int[] result = ParallelSort.Sort(p, nThreads);

                long pStart = System.currentTimeMillis();
                pSorted = SequentialSort.OriginalSort(result);
                System.out.println("\nTime sort sequential after parallel:" + (System.currentTimeMillis() - pStart));
            } catch (InterruptedException e) {
                e.printStackTrace();
                pSorted = new int[]{0, 1};
            }
            long elapsedTimeParallel = System.currentTimeMillis() - startTime;

            // Normal insertion sort
            startTime = System.currentTimeMillis();
            int[] sorted = SequentialSort.OriginalSort(s);
            long elapsedTimeSequential = System.currentTimeMillis() - startTime;

            if (Arrays.equals(sorted, pSorted)) {
                Result result = new Result(size, nThreads, elapsedTimeParallel, elapsedTimeSequential);
                results.add(result);
                System.out.println(result);
            } else {
                System.out.println("Not the same");
            }
        }

        File csvOutputFile = new File("file.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("Count,Threads,Time parallel,Time sequential");
            for (Result result : results) {
                pw.println(String.format("%d,%d,%d,%d", result.getNumberOfValues(), result.getnThreads(), result.getTimeTookPrallel(), result.getTimeTookSequential()));
            }
        }

//        Count: 100000, Threads: 8, Time parallel: 2544, Time sequential: 2103
//        Count: 200000, Threads: 8, Time parallel: 8885, Time sequential: 8848
//        Count: 300000, Threads: 8, Time parallel: 20550, Time sequential: 21544
//        Count: 400000, Threads: 8, Time parallel: 38226, Time sequential: 36262
//        Count: 500000, Threads: 8, Time parallel: 56581, Time sequential: 56583
//        Count: 600000, Threads: 8, Time parallel: 82600, Time sequential: 92629
//        Count: 700000, Threads: 8, Time parallel: 130090, Time sequential: 182455
    }
}
