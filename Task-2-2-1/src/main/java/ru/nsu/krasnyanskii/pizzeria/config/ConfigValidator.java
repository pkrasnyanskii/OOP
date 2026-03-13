package ru.nsu.krasnyanskii.pizzeria.config;

import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;

/** Validates that a {@link PizzeriaConfig} contains all required positive numeric fields. */
class ConfigValidator {

    private ConfigValidator() {
    }

    /**
     * Validates the given configuration.
     *
     * @param config configuration to validate
     * @throws IllegalArgumentException if any required field is missing or non-positive
     */
    static void validate(PizzeriaConfig config) {
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
