package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HeapSortTests {
    @Test
    public void reversedArray() {
        int[] actual = {9, 7, 5, 3, 1};
        int[] expected = {1, 3, 5, 7, 9};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void alreadySorted() {
        int[] actual = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void withDuplicates() {
        int[] actual = {4, 1, 3, 4, 2, 1};
        int[] expected = {1, 1, 2, 3, 4, 4};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void negativeNumbers() {
        int[] actual = {0, -1, -3, 2, 1};
        int[] expected = {-3, -1, 0, 1, 2};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void singleElement() {
        int[] actual = {42};
        int[] expected = {42};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void emptyArray() {
        int[] actual = {};
        int[] expected = {};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }
}
