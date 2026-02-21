package ru.nsu.krasnyanskii.pizzeria;


/**
 * Saves and loads unfinished orders in a simple JSON-like text format.
 */
public class OrderSerializer {

    public static void save(List<Order> orders, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            sb.append("  {\"id\": ").append(o.getId())
              .append(", \"state\": \"").append(o.getState().name()).append("\"}");
            if (i < orders.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");
        Files.write(Paths.get(filePath), sb.toString().getBytes());
        System.out.println("[Serializer] Сохранено " + orders.size() + " незавершённых заказов в " + filePath);
    }

    public static List<Order> load(String filePath) throws IOException {
        List<Order> orders = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        // Parse {"id": N, "state": "STATE"} entries
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\\{\\s*\"id\":\\s*(\\d+)\\s*,\\s*\"state\":\\s*\"([A-Z_]+)\"\\s*\\}");
        java.util.regex.Matcher m = p.matcher(content);
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            Order.State state = Order.State.valueOf(m.group(2));
            orders.add(new Order(id, state));
        }
        return orders;
    }
}
