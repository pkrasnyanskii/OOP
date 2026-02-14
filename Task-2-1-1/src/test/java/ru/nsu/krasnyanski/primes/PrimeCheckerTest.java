package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrimeCheckerTest {

    @Test
    void testPrimeCheckerBasic() {
        assertFalse(PrimeChecker.isPrime(-1));
        assertFalse(PrimeChecker.isPrime(0));
        assertFalse(PrimeChecker.isPrime(1));
        assertTrue(PrimeChecker.isPrime(2));
        assertTrue(PrimeChecker.isPrime(3));
        assertFalse(PrimeChecker.isPrime(4));
        assertTrue(PrimeChecker.isPrime(5));
        assertFalse(PrimeChecker.isPrime(9));
        assertTrue(PrimeChecker.isPrime(13));
    }

    @Test
    void testSequentialSolver() {
        int[] arr1 = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(SequentialSolver.hasNonPrime(arr1));

        int[] arr2 = {20319251, 6997901, 6997927, 6997937, 17858849,
                6997967, 6998009, 6998029, 6998039, 20165149, 6998051, 6998053};
        assertFalse(SequentialSolver.hasNonPrime(arr2));
    }

    @Test
    void testThreadSolver() throws InterruptedException {
        int[] arr1 = {2, 3, 5, 7, 4, 11};
        assertTrue(ThreadSolver.hasNonPrime(arr1, 1));
        assertTrue(ThreadSolver.hasNonPrime(arr1, 2));
        assertTrue(ThreadSolver.hasNonPrime(arr1, 4));

        int[] arr2 = {2, 3, 5, 7, 11, 13};
        assertFalse(ThreadSolver.hasNonPrime(arr2, 2));
    }

    @Test
    void testParallelStreamSolver() {
        int[] arr1 = {2, 3, 5, 7, 4, 11};
        assertTrue(ParallelStreamSolver.hasNonPrime(arr1));

        int[] arr2 = {2, 3, 5, 7, 11, 13};
        assertFalse(ParallelStreamSolver.hasNonPrime(arr2));
    }
}
