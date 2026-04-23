package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pizzeria configuration loaded from a JSON file.
 *
 * <p>Lombok {@code @Data} generates getters, setters, {@code equals}, {@code hashCode},
 * and {@code toString}. {@code @NoArgsConstructor} adds the no-arg constructor required
 * by Jackson for deserialization.</p>
 */
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
