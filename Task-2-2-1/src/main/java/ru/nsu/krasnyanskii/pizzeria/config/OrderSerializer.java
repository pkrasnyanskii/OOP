package ru.nsu.krasnyanskii.pizzeria.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.nsu.krasnyanskii.pizzeria.model.Order;

/** Saves and loads unfinished orders in a minimal JSON format. */
@UtilityClass
public class OrderSerializer {

    private final Pattern ORDER_PATTERN = Pattern.compile(
            "\\{\\s*\"id\":\\s*(\\d+)\\s*,\\s*\"state\":\\s*\"([A-Z_]+)\"\\s*\\}");

    /**
     * Serializes orders to a JSON file.
     *
     * @param orders   list of orders to save
     * @param filePath destination file path
     * @throws IOException if the file cannot be written
     */
    public void save(List<Order> orders, String filePath) throws IOException {
        String body = orders.stream()
                .map(o -> "  {\"id\": " + o.getId()
                        + ", \"state\": \"" + o.getState().name() + "\"}")
                .collect(Collectors.joining(",\n"));
        String json = "[\n" + body + "\n]\n";
        Files.write(Paths.get(filePath), json.getBytes());
    }

    /**
     * Loads previously saved orders from a JSON file.
     *
     * @param filePath source file path
     * @return list of deserialized orders
     * @throws IOException if the file cannot be read
     */
    public List<Order> load(String filePath) throws IOException {
        List<Order> result = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        Matcher m = ORDER_PATTERN.matcher(content);
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            Order.State state = Order.State.valueOf(m.group(2));
            result.add(new Order(id, state));
        }
        return result;
    }
}
