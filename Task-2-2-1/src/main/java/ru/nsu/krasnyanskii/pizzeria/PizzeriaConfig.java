package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

/**
 * Конфигурация пиццерии, считываемая из JSON файла.
 */
public class PizzeriaConfig {

    /** Вместимость склада (в пиццах). */
    public int storageCapacity;

    /** Интервал между новыми заказами (мс). */
    public int orderIntervalMs;

    /** Сколько времени работает пиццерия (мс). */
    public long workDurationMs;

    public List<BakerConfig> bakers;
    public List<CourierConfig> couriers;

    public static class BakerConfig {
        /** Время приготовления одной пиццы (мс). */
        public int cookingTimeMs;
    }

    public static class CourierConfig {
        /** Максимальное количество пицц за одну поездку. */
        public int trunkCapacity;
        /** Время одной доставки (мс). */
        public int deliveryTimeMs;
    }
}
