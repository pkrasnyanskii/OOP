package ru.nsu.krasnyanski.primes;

import java.util.Arrays;

public class ParallelStreamSolver {

    public static boolean hasNonPrime(int[] array) {
        return Arrays.stream(array)
                .parallel()
                .anyMatch(x -> !PrimeChecker.isPrime(x));
    }
}
