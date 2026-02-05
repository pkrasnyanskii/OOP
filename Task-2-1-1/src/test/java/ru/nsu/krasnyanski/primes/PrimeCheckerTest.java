package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrimeCheckerTest {

    @Test
    void testExample1() {
        int[] data = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(SequentialSolver.hasNonPrime(data));
    }

    @Test
    void testExample2() {
        int[] data = {
                20319251, 6997901, 6997927, 6997937, 17858849,
                6997967, 6998009, 6998029, 6998039,
                20165149, 6998051, 6998053
        };
        assertFalse(SequentialSolver.hasNonPrime(data));
    }
}
