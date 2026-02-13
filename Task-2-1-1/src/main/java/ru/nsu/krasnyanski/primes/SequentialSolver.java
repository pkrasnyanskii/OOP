package ru.nsu.krasnyanski.primes;

/**
 * Последовательный алгоритм поиска непростого числа в массиве.
 */
public class SequentialSolver {

    /**
     * Проверяет, есть ли в массиве хотя бы одно число, которое не является простым.
     *
     * @param array массив целых чисел
     * @return true, если найдено хотя бы одно непростое число
     */
    public static boolean hasNonPrime(int[] array) {
        for (int x : array) {
            if (!PrimeChecker.isPrime(x)) {
                return true;
            }
        }
        return false;
    }
}
