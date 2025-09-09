package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HeapSort algorithm.
 */
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

    @Test
    public void nullArray() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> HeapSort.sort(null));
        assertEquals("Array must not be null", exception.getMessage());
    }

    @Test
    public void twoElementsReversed() {
        int[] actual = {2, 1};
        int[] expected = {1, 2};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
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

    @Test
    void allSameElements() {
        int[] actual = {4, 4, 4, 4, 4};
        int[] expected = {4, 4, 4, 4, 4};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    void oddEvenMix() {
        int[] actual = {7, 2, 5, 8, 1, 4, 3, 6};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8};
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }
}
