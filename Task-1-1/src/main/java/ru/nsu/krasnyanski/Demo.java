package ru.nsu.krasnyanski;

import java.util.Arrays;

/**
 * Demonstrates HeapSort with a sample array.
 */
public class Demo {

    /**
     * Main method to run the demonstration.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 1, 2, 7};
        System.out.println("Before: " + Arrays.toString(arr));
        HeapSort.sort(arr);
        System.out.println("After: " + Arrays.toString(arr));
    }
}