package ru.nsu.krasnyanskii.pizzeria;

/**
 * Minimal JSON parser for PizzeriaConfig.
 * Uses only standard Java — no external dependencies.
 */
public class ConfigLoader {

    public static PizzeriaConfig load(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        PizzeriaConfig config = new PizzeriaConfig();

        config.storageCapacity = parseInt(json, "storageCapacity");
        config.orderIntervalMs = parseInt(json, "orderIntervalMs");
        config.workDurationMs  = parseLong(json, "workDurationMs");

        config.bakers  = parseBakers(json);
        config.couriers = parseCouriers(json);
        return config;
    }

    private static int parseInt(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) return Integer.parseInt(m.group(1));
        throw new IllegalArgumentException("Missing key: " + key);
    }

    private static long parseLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) return Long.parseLong(m.group(1));
        throw new IllegalArgumentException("Missing key: " + key);
    }

    private static List<PizzeriaConfig.BakerConfig> parseBakers(String json) {
        List<PizzeriaConfig.BakerConfig> list = new ArrayList<>();
        String section = extractArray(json, "bakers");
        Pattern p = Pattern.compile("\\{[^}]+\\}");
        Matcher m = p.matcher(section);
        while (m.find()) {
            String obj = m.group();
            PizzeriaConfig.BakerConfig bc = new PizzeriaConfig.BakerConfig();
            bc.cookingTimeMs = parseInt(obj, "cookingTimeMs");
            list.add(bc);
        }
        return list;
    }

    private static List<PizzeriaConfig.CourierConfig> parseCouriers(String json) {
        List<PizzeriaConfig.CourierConfig> list = new ArrayList<>();
        String section = extractArray(json, "couriers");
        Pattern p = Pattern.compile("\\{[^}]+\\}");
        Matcher m = p.matcher(section);
        while (m.find()) {
            String obj = m.group();
            PizzeriaConfig.CourierConfig cc = new PizzeriaConfig.CourierConfig();
            cc.trunkCapacity  = parseInt(obj, "trunkCapacity");
            cc.deliveryTimeMs = parseInt(obj, "deliveryTimeMs");
            list.add(cc);
        }
        return list;
    }

    private static String extractArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) return "[]";
        int bracketOpen = json.indexOf('[', start);
        if (bracketOpen == -1) return "[]";
        int depth = 0, i = bracketOpen;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') { depth--; if (depth == 0) return json.substring(bracketOpen, i + 1); }
            i++;
        }
        return "[]";
    }
}
