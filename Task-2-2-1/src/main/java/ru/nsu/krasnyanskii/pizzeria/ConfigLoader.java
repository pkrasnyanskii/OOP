package ru.nsu.krasnyanskii.pizzeria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Читает JSON конфиг без сторонних библиотек (только стандартная Java).
 * Использует регулярные выражения для парсинга.
 */
public class ConfigLoader {

    private ConfigLoader() {
        // utility class
    }

    public static PizzeriaConfig load(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        PizzeriaConfig config = new PizzeriaConfig();

        config.storageCapacity = parseInt(json, "storageCapacity");
        config.orderIntervalMs = parseInt(json, "orderIntervalMs");
        config.workDurationMs  = parseLong(json, "workDurationMs");
        config.bakers          = parseBakers(json);
        config.couriers        = parseCouriers(json);

        return config;
    }

    private static int parseInt(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalArgumentException("Missing key in config: " + key);
    }

    private static long parseLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        throw new IllegalArgumentException("Missing key in config: " + key);
    }

    private static List<PizzeriaConfig.BakerConfig> parseBakers(String json) {
        List<PizzeriaConfig.BakerConfig> list = new ArrayList<>();
        String section = extractArray(json, "bakers");
        Pattern p = Pattern.compile("\\{[^}]+\\}");
        Matcher m = p.matcher(section);
        while (m.find()) {
            PizzeriaConfig.BakerConfig bc = new PizzeriaConfig.BakerConfig();
            bc.cookingTimeMs = parseInt(m.group(), "cookingTimeMs");
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
            PizzeriaConfig.CourierConfig cc = new PizzeriaConfig.CourierConfig();
            cc.trunkCapacity  = parseInt(m.group(), "trunkCapacity");
            cc.deliveryTimeMs = parseInt(m.group(), "deliveryTimeMs");
            list.add(cc);
        }
        return list;
    }

    /** Вырезает содержимое массива [...] по ключу из JSON строки. */
    private static String extractArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        if (start == -1) {
            return "[]";
        }
        int bracketOpen = json.indexOf('[', start);
        if (bracketOpen == -1) {
            return "[]";
        }
        int depth = 0;
        for (int i = bracketOpen; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return json.substring(bracketOpen, i + 1);
                }
            }
        }
        return "[]";
    }
}
