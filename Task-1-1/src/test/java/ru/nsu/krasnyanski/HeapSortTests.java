package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HeapSort algorithm.
 */
public class HeapSortTests {

    @Test
    void emptyArray() {
        int[] actual = {};
        int[] expected = {};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void singleElement() {
        int[] actual = {42};
        int[] expected = {42};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void twoElementsReversed() {
        int[] actual = {2, 1};
        int[] expected = {1, 2};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void reversedArray() {
        int[] actual = {9, 7, 5, 3, 1};
        int[] expected = {1, 3, 5, 7, 9};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void alreadySorted() {
        int[] actual = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void withDuplicates() {
        int[] actual = {4, 1, 3, 4, 2, 1};
        int[] expected = {1, 1, 2, 3, 4, 4};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void negativeNumbers() {
        int[] actual = {0, -1, -3, 2, 1};
        int[] expected = {-3, -1, 0, 1, 2};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void nullArray() {
        assertThrows(IllegalArgumentException.class, () -> HeapSort.sort(null));
    }

    @Test
    void complexHeapify() {
        int[] actual = {3, 9, 5, 1, 2, 8, 7};
        int[] expected = {1, 2, 3, 5, 7, 8, 9};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void largerArray() {
        int[] actual = {10, 20, 5, 6, 1, 8, 9, 7, 3};
        int[] expected = {1, 3, 5, 6, 7, 8, 9, 10, 20};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }
}