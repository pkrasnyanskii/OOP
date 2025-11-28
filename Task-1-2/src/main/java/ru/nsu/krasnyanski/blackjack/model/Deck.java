package ru.nsu.krasnyanski.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a deck of playing cards for Blackjack.
 * The deck contains cards for all suits and ranks, and can
 * shuffle and draw cards.
 */
public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private static final String[] SUITS = {"♠", "♥", "♦", "♣"};

    /**
     * Creates a new deck with all cards and shuffles it.
     */
    public Deck() {
        for (String suit : SUITS) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards);
    }

    /**
     * Draws the top card from the deck.
     *
     * @return the card drawn from the deck
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
     * @return the count of remaining cards
     */
    public int getRemainingCardsCount() {
        return cards.size();
    }
}
