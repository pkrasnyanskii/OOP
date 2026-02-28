package ru.nsu.krasnyanskii.pizzeria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сохраняет и загружает незавершённые заказы в простом JSON формате.
 * Используется при "полной остановке с сериализацией".
 */
public class OrderSerializer {

    private OrderSerializer() {
        // utility class
    }

    /**
     * Сохраняет список заказов в JSON файл.
     */
    public static void save(List<Order> orders, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            sb.append("  {\"id\": ").append(o.getId())
              .append(", \"state\": \"").append(o.getState().name()).append("\"}");
            if (i < orders.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        Files.write(Paths.get(filePath), sb.toString().getBytes());
        System.out.println("[Serializer] Сохранено " + orders.size()
                + " незавершённых заказов → " + filePath);
    }

    /**
     * Загружает заказы из JSON файла (для продолжения на следующий день).
     */
    public static List<Order> load(String filePath) throws IOException {
        List<Order> orders = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        Pattern p = Pattern.compile(
                "\\{\\s*\"id\":\\s*(\\d+)\\s*,\\s*\"state\":\\s*\"([A-Z_]+)\"\\s*\\}");
        Matcher m = p.matcher(content);
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            Order.State state = Order.State.valueOf(m.group(2));
            orders.add(new Order(id, state));
        }
        return orders;
    }
}
