package ru.nsu.krasnyanskii.pizzeria.view;

/** Output contract for pizzeria lifecycle events handled by the controller. */
public interface LifecycleView {

    /** Prints the pizzeria-opened banner. */
    void pizzeriaOpened();

    /** Prints the pizzeria-closed banner. */
    void pizzeriaClosed();

    /** Prints a confirmation that all orders were completed. */
    void allOrdersDone();

    /**
     * Prints how long the pizzeria will run.
     *
     * @param ms working duration in milliseconds
     */
    void workingFor(long ms);

    /** Prints the shutdown-started banner. */
    void shutdownStarted();

    /**
     * Prints a serializer-saved confirmation.
     *
     * @param count    number of saved orders
     * @param filePath destination file path
     */
    void serializerSaved(int count, String filePath);
}
