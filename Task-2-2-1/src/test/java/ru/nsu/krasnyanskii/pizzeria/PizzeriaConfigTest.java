package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class PizzeriaConfigTest {

    @Test
    void fieldsAreAccessible() {
        PizzeriaConfig config = new PizzeriaConfig();
        config.storageCapacity = 5;
        config.orderIntervalMs = 100;
        config.workDurationMs  = 3000L;
        config.bakers          = new ArrayList<>();
        config.couriers        = new ArrayList<>();

        assertEquals(5,     config.storageCapacity);
        assertEquals(100,   config.orderIntervalMs);
        assertEquals(3000L, config.workDurationMs);
        assertTrue(config.bakers.isEmpty());
        assertTrue(config.couriers.isEmpty());
    }

    @Test
    void bakerConfigFields() {
        PizzeriaConfig.BakerConfig bc = new PizzeriaConfig.BakerConfig();
        bc.cookingTimeMs = 400;
        assertEquals(400, bc.cookingTimeMs);
    }

    @Test
    void courierConfigFields() {
        PizzeriaConfig.CourierConfig cc = new PizzeriaConfig.CourierConfig();
        cc.trunkCapacity  = 3;
        cc.deliveryTimeMs = 500;
        assertEquals(3,   cc.trunkCapacity);
        assertEquals(500, cc.deliveryTimeMs);
    }
}
