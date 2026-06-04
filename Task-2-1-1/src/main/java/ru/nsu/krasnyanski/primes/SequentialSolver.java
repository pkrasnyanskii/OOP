package ru.nsu.krasnyanski.primes;

/**
 * Sequential implementation of {@link PrimeSolver}.
 */
public class SequentialSolver implements PrimeSolver {

    @Override
    public boolean hasNonPrime(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        if (array.length == 0) {
            return false;
        }

        for (int value : array) {
            if (!PrimeChecker.isPrime(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "Sequential";
    }
}