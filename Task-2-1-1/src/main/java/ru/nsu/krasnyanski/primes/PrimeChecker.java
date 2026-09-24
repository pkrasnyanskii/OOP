package ru.nsu.krasnyanski.primes;

/**
 * Utility class for checking whether a number is prime.
 */
public final class PrimeChecker {

    private PrimeChecker() {
        // Utility class
    }

    /**
     * Determines whether the given number is prime.
     *
     * @param number integer to check
     * @return {@code true} if the number is prime,
     *         {@code false} otherwise
     */
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }

        if (number == 2) {
            return true;
        }

        if (number % 2 == 0) {
            return false;
        }

        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
}