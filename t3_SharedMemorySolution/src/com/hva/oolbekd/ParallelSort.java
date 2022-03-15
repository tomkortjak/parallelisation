package com.hva.oolbekd;

public class ParallelSort {

    static int[] Sort(int[] arr, int nTasks) throws InterruptedException {
        int perTask = arr.length / nTasks;
        int [] sortedArray = new int[arr.length];

        for (int t = 0; t < nTasks; t++) {

            // calculate the piece of array
            int from = (perTask * t + 1);
            int to = ((t + 1) * perTask);

            int[] result = SequentialSort.Sort(arr, from, to);

            // insert the sorted peice of array back into the main array
            int k = 0;
            for (int i = from - 1 ; i < to ; i++) {
                sortedArray[i] = result[k];
                k++;
            }
        }

        return sortedArray;
    }
}

