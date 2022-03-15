package com.company;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        generateTestData();
        long start = System.currentTimeMillis();
        int[][] sequentialSolutionTestData = new int[10][2];

        for (int i = 0; i < 11; i++) {
            String filename = "testArray" + (i + 1);
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));

            int[] readData = (int[]) inputStream.readObject();

            // Using sequential insertion sorting
            long startSortTime = System.currentTimeMillis();
            InsertionSort.sort(readData);
            long finishSortTime = System.currentTimeMillis();
            long timeElapsed = finishSortTime - startSortTime;

            System.out.println("Elapsed time during sorting for set of " + readData.length + ": " + timeElapsed + "ms");
            sequentialSolutionTestData[i][1] = readData.length;
            sequentialSolutionTestData[i][2] = (int) timeElapsed;
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Total elapsed time: " + timeElapsed + "ms");
        System.out.println("Times: " + Arrays.deepToString(sequentialSolutionTestData));
    }


    public static void generateTestData() throws IOException {
        for (int i = 1; i < 11; i++) {
            String filename = "testArray" + i;
            int[] testData = new int[100_000 * i];

            for (int k = 0; k < 100_000 * i; k++) {
                testData[((100_000 * i) - 1) - k] = k + 1;
            }

            // write testData to file
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            outputStream.writeObject(testData);
        }
    }
}

