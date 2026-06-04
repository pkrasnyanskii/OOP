package ru.nsu.krasnyanskii.pizzeria.config;

import lombok.experimental.UtilityClass;
import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;

/** Validates that a {@link PizzeriaConfig} contains all required positive numeric fields. */
@UtilityClass
class ConfigValidator {

    /**
     * Validates the given configuration.
     *
     * @param config configuration to validate
     * @throws IllegalArgumentException if any required field is missing or non-positive
     */
    void validate(PizzeriaConfig config) {
        if (config.getStorageCapacity() <= 0) {
            throw new IllegalArgumentException("Missing key in config: storageCapacity");
        }
        if (config.getOrderIntervalMs() <= 0) {
            throw new IllegalArgumentException("Missing key in config: orderIntervalMs");
        }
        if (config.getWorkDurationMs() <= 0) {
            throw new IllegalArgumentException("Missing key in config: workDurationMs");
        }
    }
}
