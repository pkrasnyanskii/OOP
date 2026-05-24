package ru.nsu.krasnyanskii.pizzeria.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;

/** Loads {@link PizzeriaConfig} from a JSON file using Jackson. */
@UtilityClass
public class ConfigLoader {

    private final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Loads and validates pizzeria configuration.
     *
     * @param filePath path to the JSON configuration file
     * @return parsed {@link PizzeriaConfig}
     * @throws IOException              if the file cannot be read or JSON is malformed
     * @throws IllegalArgumentException if a required numeric field is missing or zero
     */
    public PizzeriaConfig load(String filePath) throws IOException {
        PizzeriaConfig config = MAPPER.readValue(new File(filePath), PizzeriaConfig.class);
        ConfigValidator.validate(config);
        return config;
    }
}
