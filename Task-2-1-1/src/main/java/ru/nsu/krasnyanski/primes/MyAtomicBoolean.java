package ru.nsu.krasnyanski.primes;

/**
 * A simple thread-safe boolean wrapper implemented via synchronized.
 *
 * <p>This is a hand-written analogue of {@link java.util.concurrent.atomic.AtomicBoolean}.
 * Thread safety is achieved through Java's built-in monitor (object lock):
 * {@code synchronized} ensures that only one thread at a time can read or write
 * the internal value, preventing race conditions.</p>
 */
public class MyAtomicBoolean {

    private boolean value;

    /**
     * Creates a new instance with the given initial value.
     *
     * @param initialValue the initial boolean value
     */
    public MyAtomicBoolean(boolean initialValue) {
        this.value = initialValue;
    }

    /**
     * Returns the current value.
     *
     * <p>synchronized — захватывает монитор объекта.
     * Пока один поток читает, другой не может писать.</p>
     *
     * @return current value
     */
    public synchronized boolean get() {
        return value;
    }

    /**
     * Sets a new value.
     *
     * @param newValue value to set
     */
    public synchronized void set(boolean newValue) {
        this.value = newValue;
    }
}