package ru.nsu.krasnyanski.primes;

/**
 * Common interface for all prime-checking strategies.
 *
 * <p>Implementations must determine whether the given array
 * contains at least one non-prime number.</p>
 */
public interface PrimeSolver {

    /**
     * Checks whether the given array contains at least one non-prime number.
     *
     * @param array the array of integers to check
     * @return {@code true} if at least one element is not prime,
     *         {@code false} otherwise
     * @throws InterruptedException if the computation is interrupted
     */
    boolean hasNonPrime(int[] array) throws InterruptedException;

    /**
     * Returns the human-readable name of the solver implementation.
     *
     * @return solver name
     */
    String getName();
}