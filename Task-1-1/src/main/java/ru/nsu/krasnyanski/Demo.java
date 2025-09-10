package ru.nsu.krasnyanski;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 1, 2, 7};
        System.out.println("Before sorting: " + Arrays.toString(arr));
        HeapSort.sort(arr);
        System.out.println("After sorting: " + Arrays.toString(arr));
    }
}
