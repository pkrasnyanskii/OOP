package ru.nsu.krasnyanski;

import java.util.*;

/**
 * Deck of cards
 */
public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        // пока можно оставить заглушку
        // позже сюда добавим генерацию всех 52 карт
    }

    public Card draw() {
        return cards.remove(0);
    }
}