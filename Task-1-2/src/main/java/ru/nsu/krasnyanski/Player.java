package ru.nsu.krasnyanski;

import java.util.*;

/**
 * Represents a player or dealer in Blackjack.
 * Stores the player's hand and calculates scores according to Blackjack rules.
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    /**
     * Constructs a Player.
     *
     * @param name player's name
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the Card to add
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Returns the current score of the player's hand.
     * Handles Ace as 11 or 1 depending on hand total.
     *
     * @return hand score
     */
    public int getScore() {
        int sum = hand.stream().mapToInt(Card::getValue).sum();
        long aces = hand.stream().filter(c -> c.getValue() == 11).count();
        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }
        return sum;
    }

    /**
     * Returns the list of cards in hand.
     *
     * @return list of Card objects
     */
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public String toString() {
        return name + ": " + hand + " > " + getScore();
    }
}
