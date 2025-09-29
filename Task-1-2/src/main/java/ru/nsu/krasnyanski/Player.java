package ru.nsu.krasnyanski;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Blackjack player (user or dealer).
 */
public class Player {
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    /**
     * Constructs a Player with a given name.
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
     * Returns the current score of the player, considering Ace as 11 or 1.
     *
     * @return player's score
     */
    public int getScore() {
        int total = 0;
        int aceCount = 0;
        for (Card card : hand) {
            total += card.getValue();
            if (card.getValue() == 11) {
                aceCount++;
            }
        }
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    /**
     * Returns the player's hand.
     *
     * @return list of cards in hand
     */
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    @Override
    public String toString() {
        return name + ": " + hand + " > " + getScore();
    }
}
