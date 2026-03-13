package ru.nsu.krasnyanskii.pizzeria.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;

/** Loads {@link PizzeriaConfig} from a JSON file using Jackson. */
public class ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigLoader() {
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
        ConfigValidator.validate(config);
        return config;
    }
}
