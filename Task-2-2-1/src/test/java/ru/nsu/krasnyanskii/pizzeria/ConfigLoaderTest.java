package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    private Path writeConfig(String content) throws IOException {
        Path f = tempDir.resolve("config.json");
        Files.writeString(f, content);
        return f;
    }

    @Test
    void loadsFullConfig() throws IOException {
        Path cfg = writeConfig("""
            {
              "storageCapacity": 10,
              "orderIntervalMs": 200,
              "workDurationMs": 5000,
              "bakers": [
                { "cookingTimeMs": 300 },
                { "cookingTimeMs": 500 }
              ],
              "couriers": [
                { "trunkCapacity": 3, "deliveryTimeMs": 400 }
              ]
            }
            """);

        PizzeriaConfig config = ConfigLoader.load(cfg.toString());

        assertEquals(10, config.storageCapacity);
        assertEquals(200, config.orderIntervalMs);
        assertEquals(5000L, config.workDurationMs);
        assertEquals(2, config.bakers.size());
        assertEquals(300, config.bakers.get(0).cookingTimeMs);
        assertEquals(500, config.bakers.get(1).cookingTimeMs);
        assertEquals(1, config.couriers.size());
        assertEquals(3, config.couriers.get(0).trunkCapacity);
        assertEquals(400, config.couriers.get(0).deliveryTimeMs);
    }

    @Test
    void throwsOnMissingKey() throws IOException {
        // Нет storageCapacity — должен бросить исключение
        Path cfg = writeConfig("""
            {
              "orderIntervalMs": 200,
              "workDurationMs": 5000,
              "bakers": [],
              "couriers": []
            }
            """);

        assertThrows(IllegalArgumentException.class,
                () -> ConfigLoader.load(cfg.toString()));
    }

    @Test
    void parsesEmptyBakersAndCouriers() throws IOException {
        Path cfg = writeConfig("""
            {
              "storageCapacity": 5,
              "orderIntervalMs": 100,
              "workDurationMs": 1000,
              "bakers": [],
              "couriers": []
            }
            """);

        PizzeriaConfig config = ConfigLoader.load(cfg.toString());
        assertTrue(config.bakers.isEmpty());
        assertTrue(config.couriers.isEmpty());
    }
}
