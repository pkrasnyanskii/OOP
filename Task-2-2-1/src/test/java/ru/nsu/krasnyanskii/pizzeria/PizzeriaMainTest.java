package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Покрывает Pizzeria.main() запуская его с быстрым конфигом.
 * workDurationMs=300 чтобы тест не висел долго.
 */
class PizzeriaMainTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void mainRunsAndShutdownsCleanly() throws Exception {
        Path cfg = tempDir.resolve("config.json");
        Files.writeString(cfg, """
            {
              "storageCapacity": 5,
              "orderIntervalMs": 50,
              "workDurationMs": 300,
              "bakers": [
                { "cookingTimeMs": 80 }
              ],
              "couriers": [
                { "trunkCapacity": 3, "deliveryTimeMs": 80 }
              ]
            }
            """);

        Path serialized = tempDir.resolve("unfinished.json");

        // Не должно бросать исключений
        assertDoesNotThrow(() ->
            Pizzeria.main(new String[]{
                cfg.toString(),
                serialized.toString()
            })
        );
    }

    @Test
    void mainWithNoBakersOrCouriers() throws Exception {
        Path cfg = tempDir.resolve("config_empty.json");
        Files.writeString(cfg, """
            {
              "storageCapacity": 5,
              "orderIntervalMs": 50,
              "workDurationMs": 100,
              "bakers": [],
              "couriers": []
            }
            """);

        Path serialized = tempDir.resolve("unfinished2.json");

        assertDoesNotThrow(() ->
            Pizzeria.main(new String[]{
                cfg.toString(),
                serialized.toString()
            })
        );
    }
}
