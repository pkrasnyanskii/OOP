package ru.nsu.krasnyanski.blackjack.localization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides access to localized messages for the Blackjack game.
 * This class loads message resources based on the given language code
 */
public class Messages {
    private final ResourceBundle bundle;

    /**
     * Creates a new {@code Messages} instance for the specified language.
     *
     * @param langCode the ISO language code (e.g., "ru", "en", "zh")
     */
    public Messages(String langCode) {
        Locale locale = new Locale(langCode);
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Returns the localized string for the specified key.
     *
     * @param key the message key to look up
     * @return the localized message text
     */
    public String get(String key) {
        return bundle.getString(key);
    }
}
