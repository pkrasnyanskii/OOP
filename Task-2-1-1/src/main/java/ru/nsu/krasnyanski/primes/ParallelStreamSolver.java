package ru.nsu.krasnyanski.primes;

import java.util.Arrays;

/**
 * Алгоритм поиска непростого числа в массиве с использованием parallelStream().
 */
public class ParallelStreamSolver {

    /**
     * Проверяет массив на наличие хотя бы одного непростого числа.
     *
     * @param array массив целых чисел
     * @return true, если найдено хотя бы одно непростое число
     */
    public static boolean hasNonPrime(int[] array) {
        return Arrays.stream(array)
                .parallel()
                .anyMatch(x -> !PrimeChecker.isPrime(x));
    }
}
