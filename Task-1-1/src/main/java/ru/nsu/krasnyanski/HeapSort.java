package ru.nsu.krasnyanski;

public class HeapSort {

    /** Sort is a method which sorts given array by using heapify method.
     *
     * @param arr Array
     */
    public static void sort(int[] arr) {
        if (arr == null){
            throw new IllegalArgumentException("Array must not be null");
        }

        int n = arr.length;

        for(int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        for(int i = n - 1; i >= 0; i--){
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    /** Heapify is a method to create max heap from a given array.
     *
     * @param arr Array to heapify
     * @param n Array length
     * @param i Index of root
     */
    private static void heapify(int[] arr, int n, int i) {

        int large = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[large]) {
            large = l;
        }
        if (r < n && arr[r] > arr[large]){
            large = r;
        }

        if (large != i) {
            int swap = arr[i];
            arr[i] = arr[large];
            arr[large] = swap;

            heapify(arr, n, large);
        }
    }
}
