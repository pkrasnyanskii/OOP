package ru.nsu.krasnyanski;

import java.util.*;

/**
 * Player class
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public int getScore() {
        return hand.stream().mapToInt(Card::getValue).sum();
    }

    @Override
    public String toString() {
        return name + ": " + hand.toString() + " > " + getScore();
    }
}