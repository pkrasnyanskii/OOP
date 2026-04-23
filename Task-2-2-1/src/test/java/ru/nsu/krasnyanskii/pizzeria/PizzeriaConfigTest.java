package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class PizzeriaConfigTest {

    @Test
    void fieldsAreAccessible() {
        PizzeriaConfig config = new PizzeriaConfig();
        config.setStorageCapacity(5);
        config.setOrderIntervalMs(100);
        config.setWorkDurationMs(3000L);
        config.setBakers(new ArrayList<>());
        config.setCouriers(new ArrayList<>());

        assertEquals(5, config.getStorageCapacity());
        assertEquals(100, config.getOrderIntervalMs());
        assertEquals(3000L, config.getWorkDurationMs());
        assertTrue(config.getBakers().isEmpty());
        assertTrue(config.getCouriers().isEmpty());
    }

    @Test
    void bakerConfigFields() {
        PizzeriaConfig.BakerConfig bc = new PizzeriaConfig.BakerConfig();
        bc.setCookingTimeMs(400);
        assertEquals(400, bc.getCookingTimeMs());
    }

    @Test
    void courierConfigFields() {
        PizzeriaConfig.CourierConfig cc = new PizzeriaConfig.CourierConfig();
        cc.setTrunkCapacity(3);
        cc.setDeliveryTimeMs(500);
        assertEquals(3, cc.getTrunkCapacity());
        assertEquals(500, cc.getDeliveryTimeMs());
    }
}
