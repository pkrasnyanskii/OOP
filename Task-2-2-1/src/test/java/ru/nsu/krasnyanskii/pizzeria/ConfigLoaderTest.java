package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

        assertEquals(10, config.getStorageCapacity());
        assertEquals(200, config.getOrderIntervalMs());
        assertEquals(5000L, config.getWorkDurationMs());
        assertEquals(2, config.getBakers().size());
        assertEquals(300, config.getBakers().get(0).getCookingTimeMs());
        assertEquals(500, config.getBakers().get(1).getCookingTimeMs());
        assertEquals(1, config.getCouriers().size());
        assertEquals(3, config.getCouriers().get(0).getTrunkCapacity());
        assertEquals(400, config.getCouriers().get(0).getDeliveryTimeMs());
    }

    @Test
    void throwsOnMissingKey() throws IOException {
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
        assertTrue(config.getBakers().isEmpty());
        assertTrue(config.getCouriers().isEmpty());
    }
}
