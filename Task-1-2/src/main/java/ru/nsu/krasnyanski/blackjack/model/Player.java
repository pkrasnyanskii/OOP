package ru.nsu.krasnyanski.blackjack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Blackjack game.
 * Each player has a name and a hand of cards. This class provides
 * methods to add cards, calculate the current score, and retrieve
 * player information.
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    /**
     * Creates a new player with the specified name.
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Calculates and returns the player's total score.
     * Aces count as 11 by default but can be reduced to 1 if the
     * total score exceeds 21.
     *
     * @return the total score of the player's hand
     */
    public int getScore() {
        int total = 0;
        int aces = 0;
        for (Card card : hand) {
            total += card.getValue();
            if (card.isAce()) {
                aces++;
            }
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    /**
     * Returns a copy of the player's current hand.
     *
     * @return a list of cards in the player's hand
     */
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }
}
