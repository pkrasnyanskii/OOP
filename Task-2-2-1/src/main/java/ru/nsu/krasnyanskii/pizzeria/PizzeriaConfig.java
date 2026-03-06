package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

public class PizzeriaConfig {
    /** Storage capacity in pizzas */
    public int storageCapacity;
    /** How often new orders arrive (ms) */
    public int orderIntervalMs;
    /** Total working time of the pizzeria (ms) */
    public long workDurationMs;

    public List<BakerConfig> bakers;
    public List<CourierConfig> couriers;

    public static class BakerConfig {
        /** Cooking time in ms */
        public int cookingTimeMs;
    }

    public static class CourierConfig {
        /** Max number of pizzas per delivery */
        public int trunkCapacity;
        /** Delivery duration in ms */
        public int deliveryTimeMs;
    }
}
