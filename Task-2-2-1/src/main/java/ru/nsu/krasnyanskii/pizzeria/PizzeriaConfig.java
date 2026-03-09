package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

/**
 * Pizzeria configuration loaded from a JSON file.
 */
public class PizzeriaConfig {

    /** Storage capacity in pizzas. */
    public int storageCapacity;

    /** Interval between new orders (ms). */
    public int orderIntervalMs;

    /** Total working time of the pizzeria (ms). */
    public long workDurationMs;

    /** List of baker configurations. */
    public List<BakerConfig> bakers;

    /** List of courier configurations. */
    public List<CourierConfig> couriers;

    /** Configuration for a single baker. */
    public static class BakerConfig {
        /** Cooking time in ms per pizza. */
        public int cookingTimeMs;
    }

    /** Configuration for a single courier. */
    public static class CourierConfig {
        /** Max number of pizzas per delivery. */
        public int trunkCapacity;
        /** Delivery duration in ms. */
        public int deliveryTimeMs;
    }
}