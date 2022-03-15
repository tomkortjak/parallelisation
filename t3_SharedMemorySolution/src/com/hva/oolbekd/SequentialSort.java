package com.hva.oolbekd;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class SequentialSort {

    public static int[] Sort(int[] arr, int from, int to) throws InterruptedException {
        final AtomicInteger doneSorting = new AtomicInteger(0);

        // thread 2
        int[] arraySequentialPart = new int[to - from + 1];
        int[] arrayParallelPart = new int[to - from + 1];

        int k = 0;
        for (int i = from - 1; i < to; i++) {
            arraySequentialPart[k] = arr[i];
            arrayParallelPart[to - i - 1] = arr[i];
            k++;
        }

        Thread threadOne = new Thread(() -> {
            for (int i = 1; i < arraySequentialPart.length; ++i) {
                if (doneSorting.get() > 0) {
                    break;
                }
                int key = arraySequentialPart[i];
                int j = i - 1;
                while (j >= 0 && arraySequentialPart[j] > key) {
                    arraySequentialPart[j + 1] = arraySequentialPart[j];
                    j = j - 1;
                }
                arraySequentialPart[j + 1] = key;
            }

            if (doneSorting.get() < 1) {
                doneSorting.set(1);
            }
        });
        threadOne.start();

        Thread threadTwo = new Thread(() -> {
            for (int i = 1; i < arrayParallelPart.length; ++i) {
                if (doneSorting.get() > 0) {
                    break;
                }
                int key = arrayParallelPart[i];
                int j = i - 1;
                while (j >= 0 && arrayParallelPart[j] > key) {
                    arrayParallelPart[j + 1] = arrayParallelPart[j];
                    j = j - 1;
                }
                arrayParallelPart[j + 1] = key;
            }
            if (doneSorting.get() < 1) {
                doneSorting.set(2);
            }
        });
        threadTwo.start();

        threadTwo.join();
        threadOne.join();

        if (doneSorting.get() == 1) {
            return arraySequentialPart;
        }
        if (doneSorting.get() == 2) {
            return arrayParallelPart;
        } else {
            System.out.println("we fucked up");
            return new int[]{0};
        }
    }

    public static int[] OriginalSort(int[] arr) {
        for (int i = 1; i < arr.length; ++i) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }

        return arr;
    }
}
