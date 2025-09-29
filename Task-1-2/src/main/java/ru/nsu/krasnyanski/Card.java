package ru.nsu.krasnyanski;

/**
 * Represents a playing card in Blackjack.
 * Each card has a suit, a rank, and a value.
 */
public class Card {
    private final String suit;
    private final String rank;
    private final int value;

    /**
     * Constructs a Card.
     *
     * @param suit  the suit of the card (♠, ♥, ♦, ♣)
     * @param rank  the rank of the card (2-10, J, Q, K, A)
     * @param value the point value of the card
     */
    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    /**
     * Returns the point value of the card.
     *
     * @return integer value of the card
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return rank + " " + suit + " (" + value + ")";
    }
}
