package ru.nsu.krasnyanskii.pizzeria;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads {@link PizzeriaConfig} from a JSON file using Jackson.
 *
 * <p>Jackson maps JSON field names to class fields automatically.
 * Lombok {@code @Data} provides the setters Jackson needs; {@code @NoArgsConstructor}
 * provides the no-arg constructor required for deserialization.</p>
 */
public class ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigLoader() {
        // utility class
    }

    /**
     * Loads and validates pizzeria configuration.
     *
     * @param filePath path to the JSON configuration file
     * @return parsed {@link PizzeriaConfig}
     * @throws IOException              if the file cannot be read or JSON is malformed
     * @throws IllegalArgumentException if a required numeric field is missing or zero
     */
    public static PizzeriaConfig load(String filePath) throws IOException {
        PizzeriaConfig config = MAPPER.readValue(new File(filePath), PizzeriaConfig.class);
        validate(config);
        return config;
    }

    private static void validate(PizzeriaConfig config) {
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
