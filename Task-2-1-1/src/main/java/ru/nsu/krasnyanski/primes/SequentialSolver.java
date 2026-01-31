package ru.nsu.krasnyanski.primes;

public class SequentialSolver {

    public static boolean hasNonPrime(int[] array) {
        for (int x : array) {
            if (!PrimeChecker.isPrime(x)) {
                return true;
            }
        }
        return false;
    }
}
