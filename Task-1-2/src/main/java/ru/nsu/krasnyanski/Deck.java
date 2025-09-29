package ru.nsu.krasnyanski;

import java.util.*;

/**
 * Represents a deck of playing cards.
 * This implementation creates two standard 52-card decks and shuffles them.
 */
public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private static final String[] SUITS = {"♠", "♥", "♦", "♣"};
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    /**
     * Constructs a Deck with 2 shuffled standard 52-card decks.
     */
    public Deck() {
        for (int d = 0; d < 2; d++) {
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    int value;
                    if (rank.equals("A")){
                        value = 11;
                    }
                    else if ("JQK".contains(rank)) {
                        value = 10;
                    }
                    else {
                        value = Integer.parseInt(rank);
                    }
                    cards.add(new Card(suit, rank, value));
                }
            }
        }
        Collections.shuffle(cards);
    }

    /**
     * Draws the top card from the deck.
     *
     * @return the drawn Card
     * @throws NoSuchElementException if the deck is empty
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Deck is empty");
        }
        return cards.remove(0);
    }

    /**
     * Returns the number of remaining cards in the deck.
     *
     * @return number of cards left
     */
    public int remainingCards() {
        return cards.size();
    }
}
