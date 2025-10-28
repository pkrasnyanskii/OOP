package ru.nsu.krasnyanski.blackjack.view;

import ru.nsu.krasnyanski.blackjack.localization.Messages;

/**
 * Handles output to the console for the Blackjack game.
 * Supports localization via Messages class.
 */
public class OutputHandler {
    private final Messages messages;

    /**
     * Constructs an OutputHandler with the given Messages instance.
     *
     * @param messages the Messages instance for localized strings
     */
    public OutputHandler(Messages messages) {
        this.messages = messages;
    }

    /**
     * Prints a line to the console.
     *
     * @param text the text to print
     */
    public void println(String text) {
        System.out.println(text);
    }

    /**
     * Shows the player's cards and current score.
     *
     * @param playerName the name of the player
     * @param cards      the string representation of player's hand
     * @param score      the player's current score
     */
    public void showPlayerCards(String playerName, String cards, int score) {
        println(messages.get("game.welcome") + System.lineSeparator()
                + messages.get("player.cards") + " " + playerName + ": " + cards + " > " + score);
    }

    /**
     * Shows the dealer's first card and a hidden card.
     *
     * @param dealerCard the dealer's visible card
     */
    public void showDealerHidden(String dealerCard) {
        println(messages.get("dealer.cards.hidden")
                + " [" + dealerCard + ", <" + messages.get("hidden.card") + ">]");
    }
}
