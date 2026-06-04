package ru.nsu.krasnyanskii.pizzeria.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Pizzeria configuration loaded from a JSON file. */
@Data
@NoArgsConstructor
public class PizzeriaConfig {

    private int storageCapacity;
    private int orderIntervalMs;
    private long workDurationMs;
    private List<BakerConfig> bakers;
    private List<CourierConfig> couriers;

    /** Configuration for a single baker. */
    @Data
    @NoArgsConstructor
    public static class BakerConfig {
        private int cookingTimeMs;
    }

    /** Configuration for a single courier. */
    @Data
    @NoArgsConstructor
    public static class CourierConfig {
        private int trunkCapacity;
        private int deliveryTimeMs;
    }
}
