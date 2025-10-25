package ru.nsu.krasnyanski.blackjack.model;

/**
 * Represents a playing card in Blackjack.
 *
 * Each card has a suit and a rank.
 * The value of the card is determined by its rank.
 *
 */
public class Card {
    private final String suit;
    private final Rank rank;

    /**
     * Creates a new card with the given suit and rank.
     *
     * @param suit the suit of the card (♠, ♥, ♦, ♣)
     * @param rank the rank of the card (Rank enum)
     */
    public Card(String suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Returns the point value of the card.
     *
     * @return the integer value of the card
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * Checks if the card is an Ace.
     *
     * @return true if the card is an Ace, false otherwise
     */
    public boolean isAce() {
        return rank == Rank.ACE;
    }

    /**
     * Returns a string representation of the card.
     *
     * @return string in the format "Rank Suit (Value)"
     */
    @Override
    public String toString() {
        return rank.getLabel() + " " + suit + " (" + rank.getValue() + ")";
    }
}
